/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import model.entities.Article;
import model.entities.Customer;

import java.util.List;
import java.util.Date;
import jakarta.ws.rs.core.GenericEntity;
import java.net.URI;


/**
 *
 * @author veron
 */

@Stateless
@Path("article")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArticleFacadeREST extends AbstractFacade<Article> {

    @PersistenceContext
    private EntityManager em;

    public ArticleFacadeREST() {
        super(Article.class);  // Llamada al constructor de AbstractFacade con Article
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    // GET /rest/api/v1/article?topic=${topic}&author=${author}
    @GET
    public Response getArticles(@QueryParam("topic") List<String> topics, 
                                 @QueryParam("author") String author) {
        StringBuilder queryStr = new StringBuilder("SELECT a FROM Article a WHERE 1=1");

        // Filtrar por tópico
        if (topics != null && !topics.isEmpty()) {
            queryStr.append(" AND a.topics IN :topics");
        }

        // Filtrar por autor
        if (author != null && !author.isEmpty()) {
            queryStr.append(" AND a.authorName = :author");
        }

        // Ordenar por popularidad
        queryStr.append(" ORDER BY a.views DESC");

        TypedQuery<Article> query = em.createQuery(queryStr.toString(), Article.class);

        if (topics != null && !topics.isEmpty()) {
            query.setParameter("topics", topics);
        }

        if (author != null && !author.isEmpty()) {
            query.setParameter("author", author);
        }

        List<Article> articles = query.getResultList();
        return Response.ok(new GenericEntity<List<Article>>(articles) {}).build();
    }

    // GET /rest/api/v1/article/${id}
    @GET
    @Path("/{id}")
    public Response getArticle(@PathParam("id") Long id, @HeaderParam("Authorization") String token) {
        Article article = find(id);

        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Verificación de autenticación
        if (!isAuthenticated(token)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Aumentar las vistas
        article.setViews(article.getViews() + 1);
        edit(article);

        return Response.ok(article).build();
    }

    // DELETE /rest/api/v1/article/${id}
    @DELETE
    @Path("/{id}")
    public Response deleteArticle(@PathParam("id") Long id, @HeaderParam("Authorization") String token) {
        Article article = find(id);

        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Verificar autenticación y si el usuario es el autor
        Customer author = article.getAuthorName() != null ? findAuthorByUsername(article.getAuthorName()) : null;
        if (!isAuthenticated(token) || author == null || !author.getUsername().equals(getUsernameFromToken(token))) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        remove(article);  // Eliminar el artículo
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    // POST /rest/api/v1/article
    @POST
    public Response createArticle(Article article, @HeaderParam("Authorization") String token) {
        // Verificar si el usuario está autenticado
        if (!isAuthenticated(token)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Verificar que el usuario sea autor
        Customer author = findAuthorByUsername(getUsernameFromToken(token));
        if (author == null || !author.isAuthor()) {
            return Response.status(Response.Status.FORBIDDEN).entity("You are not an authorized author.").build();
        }

        // Validar que los tópicos sean correctos (máximo 2)
        if (article.getTopics() == null || article.getTopics().size() > 2) {
            return Response.status(Response.Status.BAD_REQUEST).entity("You must provide 1 or 2 topics.").build();
        }

        // Establecer la fecha de publicación automáticamente
        article.setDatePublished(new Date());

        // Asignar el autor al artículo
        article.setAuthorName(author.getUsername());

        create(article);  // Guardar el artículo

        // Devolver el enlace al artículo creado
        
        URI uri = UriBuilder.fromPath("/article/{id}").build(article.getId());
        return Response.created(uri).entity("Article created with ID: " + article.getId()).build();
        
    }

    // Métodos auxiliares
    private boolean isAuthenticated(String token) {
        // Implementar la lógica de autenticación (por ejemplo, verificar un token JWT)
        return token != null && !token.isEmpty();  // Simulación de autenticación
    }

    private String getUsernameFromToken(String token) {
        // Implementar la lógica para obtener el nombre de usuario desde el token
        return "exampleUser";  // Simulación
    }

    private Customer findAuthorByUsername(String username) {
        // Buscar un autor en la base de datos por su nombre de usuario
        try {
            return em.createQuery("SELECT c FROM Customer c WHERE c.username = :username", Customer.class)
                     .setParameter("username", username)
                     .getSingleResult();
        } catch (Exception e) {
            return null;  // No se encontró el autor
        }
    }
}