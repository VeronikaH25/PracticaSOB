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
        if (article.getAuthorName().equals("private") && authorization == null) { 
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
    @Secured
    public Response remove(@PathParam("id") Long id, @HeaderParam("Authorization") String authorization) {
        Article article = super.find(id);
        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Verificar si el usuario autenticado es el autor del artículo
        if (!isAuthorizedToDelete(article, authorization)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        super.remove(article);
        return Response.noContent().build();
    }

    // POST /rest/api/v1/article
    @POST
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(Article article, @HeaderParam("Authorization") String authorization) {
        // Validar que los topics sean válidos y el autor exista
        if (article.getTopics().size() > 2) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los artículos pueden tener hasta dos temas").build();
        }

        // Suponiendo que el nombre del autor debe coincidir con el usuario autenticado
        String authenticatedUsername = getAuthenticatedUsername(authorization);
        if (!authenticatedUsername.equals(article.getAuthorName())) {
            return Response.status(Response.Status.FORBIDDEN).build();
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

        super.create(article);
        return Response.status(Response.Status.CREATED).entity(article.getId()).build();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    // Método auxiliar para validar si el usuario está autorizado a borrar un artículo
    private boolean isAuthorizedToDelete(Article article, String authorization) {
        // Aquí debes validar el token y comparar el autor real con el que está autenticado.
        return article.getAuthorName().equals(getAuthenticatedUsername(authorization));
    }

    // Método auxiliar para obtener el nombre de usuario autenticado (a partir del token de autorización)
    private String getAuthenticatedUsername(String authorization) {
        // Este es solo un ejemplo de cómo podrías hacerlo; deberías integrarlo con tu sistema de autenticación
        if (authorization == null || authorization.isEmpty()) {
            return null;
        }

        // Extraer el nombre del usuario del token (simplificado)
        String username = authorization.replace("Bearer ", "").split(":")[0];

        // Ahora buscar al Customer relacionado con el nombre de usuario
        TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c WHERE c.credentials.username = :username", Customer.class);
        query.setParameter("username", username);
        List<Customer> customers = query.getResultList();

        if (!customers.isEmpty()) {
            return customers.get(0).getCredentials().getUsername();  // Obtener el username desde Credentials
        }

        return null;  // Si no se encuentra el usuario
    }
}