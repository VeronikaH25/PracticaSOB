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
import authn.Credentials;
import com.sun.xml.messaging.saaj.util.Base64;
import com.sun.xml.ws.api.security.trust.Claims;
import jakarta.persistence.NoResultException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    @Secured  // Asegura que solo usuarios autenticados puedan acceder
    public Response remove(@PathParam("id") Long id, @HeaderParam("Authorization") String authorization) {
        // Validar la autenticación
        if (authorization == null || authorization.isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Authentication required").build();
        }

        // Decodificar las credenciales de autenticación
        String username;
        try {
            String auth = authorization.replace("Basic ", "");
            String decodedAuth = new String(Base64.base64Decode(auth));  // Tu método de decodificación
            username = decodedAuth.split(":")[0];  // Extraer el username (antes del ':')
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid authentication format").build();
        }

        // Buscar el artículo por ID
        Article article = em.find(Article.class, id);
        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Article not found").build();
        }

        // Obtener el nombre del autor del artículo
        String articleAuthorUsername = article.getAuthorName();
        if (articleAuthorUsername == null || articleAuthorUsername.isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Article has no author").build();
        }

        // Comparar el username autenticado con el del autor del artículo
        if (!username.equals(articleAuthorUsername)) {
            return Response.status(Response.Status.FORBIDDEN).entity("You are not the author of this article").build();
        }

        // Eliminar el artículo
        em.remove(article);
        return Response.noContent().build();
    }



@POST
@Secured // Asegura que solo los usuarios autenticados pueden crear artículos
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public Response createArticle(Article article, @HeaderParam("Authorization") String authorization) {
    // Validar que Authorization esté presente
    if (authorization == null || authorization.isEmpty()) {
        return Response.status(Response.Status.FORBIDDEN).entity("Authorization header is missing").build();
    }

    // Extraer username y password de la cabecera Authorization (Basic Auth)
    String username;
    String password;
    try {
        String auth = authorization.replace("Basic ", "");
        String decodedAuth = new String(Base64.base64Decode(auth));
        String[] credentials = decodedAuth.split(":");
        username = credentials[0];
        password = credentials[1];
    } catch (Exception e) {
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid authentication format").build();
    }

    // Verificar si el usuario autenticado es el mismo que el del artículo
    if (!article.getAuthorName().equals(username)) {
        return Response.status(Response.Status.FORBIDDEN).entity("Authenticated user does not match the article author").build();
    }

    // Validar que los topics sean válidos
    if (article.getTopics() == null || article.getTopics().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST).entity("El artículo debe tener al menos un tópico.").build();
    }

    if (article.getTopics().size() > 2) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Los artículos pueden tener hasta dos temas").build();
    }

Set<Long> uniqueTopicIds = new HashSet<>(); // Para evitar duplicados por ID
    Set<String> uniqueTopicNames = new HashSet<>(); // Para evitar duplicados por nombre
    List<Topic> validTopics = new ArrayList<>();

    for (Topic topic : article.getTopics()) {
        // Verificar si el nombre del tópico existe en la base de datos
        TypedQuery<Topic> query = em.createQuery("SELECT t FROM Topic t WHERE t.name = :name", Topic.class);
        query.setParameter("name", topic.getName());

        Topic existingTopic = null;
        try {
            existingTopic = query.getSingleResult();
        } catch (NoResultException e) {
            // Si no se encuentra un tópico con ese nombre, se lanza la excepción y retornamos un error
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Tópico no válido: " + topic.getName()).build();
        }

        /* Validar duplicados por ID (no permitir que el mismo tópico se repita por ID)
        if (!uniqueTopicIds.add(existingTopic.getId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Tópico duplicado por ID: " + topic.getName()).build();
        }*/

        // Validar duplicados por nombre (no permitir que el mismo nombre se repita dentro del artículo)
        if (!uniqueTopicNames.add(topic.getName())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Tópico duplicado por nombre: " + topic.getName()).build();
        }

        // Agregar el tópico válido a la lista
        validTopics.add(existingTopic);
    }

    article.setTopics(validTopics); // Asociamos los tópicos válidos

    // Validar si el autor existe en la base de datos
    Customer existingCustomer = em.createNamedQuery("Credentials.findUser", Credentials.class)
            .setParameter("username", username) // Buscar el autor por username
            .getResultList()
            .stream()
            .map(Credentials::getCustomer) // Obtener el Customer asociado
            .findFirst()
            .orElse(null);

    if (existingCustomer == null) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("El autor con el username proporcionado no existe.").build();
    }

    // Asociar el autor al artículo
    article.setAuthor(existingCustomer);

    // Establecer la fecha de publicación
    article.setDatePublished(new java.util.Date());

    // Persistir el artículo en la base de datos
    super.create(article);

    // Devolver la respuesta con el ID del artículo creado
    return Response.status(Response.Status.CREATED).entity(article.getId()).build();
}



    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}