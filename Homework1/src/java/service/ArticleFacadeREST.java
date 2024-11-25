/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import authn.Secured;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.entities.Article;
import model.entities.Customer;
import java.util.List;

/**
 *
 * @author veron
 */

@Stateless
@Path("/api/v1/article")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArticleFacadeREST {

    @PersistenceContext
    private EntityManager em;

    // GET /rest/api/v1/article?topic=${topic}&author=${author}
    @GET
    public Response getArticles(@QueryParam("topic") List<String> topics, 
                                 @QueryParam("author") String author) {
        StringBuilder query = new StringBuilder("SELECT a FROM Article a");
        boolean hasWhere = false;

        // Filtrado por topic y author
        if ((topics != null && !topics.isEmpty()) || author != null) {
            query.append(" WHERE");
        }

        // Filtrado por topic
        if (topics != null && !topics.isEmpty()) {
            query.append(" a.topics IN :topics");
            hasWhere = true;
        }

        // Filtrado por author
        if (author != null) {
            if (hasWhere) {
                query.append(" AND");
            }
            query.append(" a.authorName = :author");
        }

        query.append(" ORDER BY a.views DESC");

        TypedQuery<Article> queryObj = em.createQuery(query.toString(), Article.class);
        if (topics != null && !topics.isEmpty()) queryObj.setParameter("topics", topics);
        if (author != null) queryObj.setParameter("author", author);

        List<Article> articles = queryObj.getResultList();
        return Response.ok(articles).build();
    }

    // GET /rest/api/v1/article/{id}
    @GET
    @Path("/{id}")
    public Response getArticle(@PathParam("id") Long id, @HeaderParam("Authorization") String token) {
        Customer user = authenticateUser(token); // Método para autenticar

        Article article = em.find(Article.class, id);
        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Verificación de acceso según autor y visibilidad del artículo
        if (article.getFeaturedImage() == null && (user == null || !user.getUsername().equals(article.getAuthorName()))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // Incrementar el número de visualizaciones
        article.setViews(article.getViews() + 1);
        em.merge(article);

        return Response.ok(article).build();
    }

    // DELETE /rest/api/v1/article/{id}
    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteArticle(@PathParam("id") Long id, @HeaderParam("Authorization") String token) {
        Customer user = authenticateUser(token); // Método para autenticar

        Article article = em.find(Article.class, id);
        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Si el usuario no es el autor del artículo, no puede eliminarlo
        if (!article.getAuthorName().equals(user.getUsername())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        em.remove(article);
        return Response.noContent().build();
    }

    // POST /rest/api/v1/article
    @POST
    @Secured
    public Response createArticle(Article article, @HeaderParam("Authorization") String token) {
        Customer user = authenticateUser(token); // Método para autenticar
        if (user == null || !user.isAuthor()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Solo un usuario con isAuthor = true puede crear artículos
        article.setAuthorName(user.getUsername()); // Setear el autor del artículo
        article.setDatePublished(new java.util.Date());
        em.persist(article);

        return Response.status(Response.Status.CREATED)
                       .entity(article)
                       .build();
    }

    // Método auxiliar para autenticar al usuario
    private Customer authenticateUser(String token) {
        // Implementar lógica de autenticación
        if (token == null || token.isEmpty()) {
            return null; // Simulación de autenticación fallida si no hay token
        }

        // Aquí puedes usar el token para autenticar al usuario, por ejemplo con una API Key o JWT
        // Para este ejemplo simularé que el token es un nombre de usuario.

        Customer user = em.createQuery("SELECT c FROM Customer c WHERE c.username = :username", Customer.class)
                          .setParameter("username", token)
                          .getResultStream()
                          .findFirst()
                          .orElse(null);
        return user;
    }
}