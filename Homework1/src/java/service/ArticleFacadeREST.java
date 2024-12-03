/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.entities.Article;
import model.entities.Topic;
import model.entities.Customer;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import authn.Secured;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author veron
 */

@Stateless
@Path("article")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArticleFacadeREST extends AbstractFacade<Article> {

    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

    public ArticleFacadeREST() {
        super(Article.class);
    }

    // GET /rest/api/v1/article?topic=${topic}&author=${author}
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Article> findAll(@QueryParam("topic") List<String> topicNames, @QueryParam("author") String author) {
        String queryStr = "SELECT a FROM Article a WHERE 1=1";

        // Añadir filtro por topic
        if (topicNames != null && !topicNames.isEmpty()) {
            queryStr += " AND EXISTS (SELECT t FROM a.topics t WHERE t.name IN :topicNames)";
        }

        // Añadir filtro por autor
        if (author != null && !author.isEmpty()) {
            queryStr += " AND a.authorName = :author";
        }

        queryStr += " ORDER BY a.views DESC";  // Ordenar por popularidad

        TypedQuery<Article> query = em.createQuery(queryStr, Article.class);

        // Establecer parámetros
        if (topicNames != null && !topicNames.isEmpty()) {
            query.setParameter("topicNames", topicNames);
        }
        if (author != null && !author.isEmpty()) {
            query.setParameter("author", author);
        }

        return query.getResultList();
    }

    // GET /rest/api/v1/article/{id}
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response find(@PathParam("id") Long id, @HeaderParam("Authorization") String authorization) {
        Article article = super.find(id);

        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Si el artículo es privado, se requiere que el usuario esté registrado
        if (article.getisPrivate() && authorization == null) { 
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // Incrementar las visualizaciones
        article.setViews(article.getViews() + 1);
        super.edit(article);  // Guardar el incremento de visualizaciones

        return Response.ok().entity(article).build();
    }

    // DELETE /rest/api/v1/article/{id}
    @DELETE
    @Path("{id}")
    @Secured  // Asegura que solo los usuarios autenticados pueden borrar
    public Response remove(@PathParam("id") Long id) {
        Article article = super.find(id);
        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Eliminar el artículo de la base de datos
        em.remove(article);
        // El filtro RESTRequestFilter ya asegura que el usuario esté autenticado,
        // así que ya no necesitamos hacer nada adicional aquí.
        // En lugar de verificar manualmente el autor, ahora confiamos en el filtro para eso.
        return Response.noContent().build();
    }

    // POST /rest/api/v1/article
    @POST
    @Secured  // Asegura que solo los usuarios autenticados pueden crear artículos
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createArticle(Article article, @HeaderParam("Authorization") String authorization) {
        
        // Valida el encabezado de autorización (puedes personalizar esta validación)
        if (authorization == null || authorization.isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Authorization header is missing").build();
        }
        // Validar que los topics sean válidos y el autor exista
        if (article.getTopics().size() > 2) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los artículos pueden tener hasta dos temas").build();
        }

        // Validar si los tópicos existen en la base de datos y asociarlos
        List<Topic> validTopics = new ArrayList<>();
        for (Topic topic : article.getTopics()) {
            Topic existingTopic = em.find(Topic.class, topic.getId());
            if (existingTopic == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Tópico no válido: " + topic.getName()).build();
            }
            validTopics.add(existingTopic);
        }
        article.setTopics(validTopics);  // Asociamos los tópicos válidos

        // Establecer la fecha de publicación
        article.setDatePublished(new java.util.Date());

        // Llamamos al método create de la clase base AbstractFacade para persistir el artículo
        super.create(article);  // Aquí no se sobrescribe el método, solo se llama

        return Response.status(Response.Status.CREATED).entity(article.getId()).build();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}