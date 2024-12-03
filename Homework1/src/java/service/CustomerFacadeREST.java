/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.entities.Customer;
import model.entities.Article; // Asegúrate de importar la clase Article
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import authn.Secured;
import java.util.List;
import java.util.Comparator;
import java.util.Optional;


/**
 *
 * @author veron
 */
@Stateless
@Path("Customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerFacadeREST extends AbstractFacade<Customer> {

    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

    public CustomerFacadeREST() {
        super(Customer.class);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Override
    public List<Customer> findAll() {
        List<Customer> customers = super.findAll();
        
        // Añadir enlaces al último artículo de los usuarios que son autores
        for (Customer customer : customers) {
            if (customer.isAuthor()) {
                customer.setLastArticleLink("/Homework1/webresources/article/" + getLastArticleIdForCustomer(customer));
            }
        }
        
        // Aseguramos que los datos sensibles como la contraseña no se incluyan en la respuesta
        customers.forEach(c -> c.setCredentials(null));
        
        return customers;
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response find(@PathParam("id") Long id) {
        Customer customer = super.find(id);
        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Si el usuario es autor, se añade el enlace al último artículo
        if (customer.isAuthor()) {
            customer.setLastArticleLink("/Homework1/webresources/article/" + getLastArticleIdForCustomer(customer));
        }

        // Aseguramos que no se envíe información sensible como la contraseña
        customer.setCredentials(null);
        
        return Response.ok().entity(customer).build();
    }

    @PUT
    @Path("{id}")
    @Secured // Asegura que el usuario esté autenticado
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response edit(@PathParam("id") Long id, Customer customer, @HeaderParam("Authorization") String authorization) {
        // Busca el cliente existente
        Customer existingCustomer = super.find(id);
        if (existingCustomer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Valida el encabezado de autorización (puedes personalizar esta validación)
        if (authorization == null || authorization.isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Authorization header is missing").build();
        }

        // Puedes añadir lógica adicional aquí para verificar si el usuario tiene permisos específicos para editar.

        // Actualiza las credenciales si están presentes
        if (customer.getCredentials() != null) {
            if (existingCustomer.getCredentials() != null) {
                existingCustomer.getCredentials().setUsername(customer.getCredentials().getUsername());
                existingCustomer.getCredentials().setPassword(customer.getCredentials().getPassword());
            } else {
                // Si no existen credenciales, crea nuevas
                existingCustomer.setCredentials(customer.getCredentials());
            }
        }

        // Actualiza los demás campos del cliente
        if (customer.getFirstName() != null) existingCustomer.setFirstName(customer.getFirstName());
        if (customer.getLastName() != null) existingCustomer.setLastName(customer.getLastName());
        if (customer.getEmail() != null) existingCustomer.setEmail(customer.getEmail());
        if (customer.getRegisteredDate() != null) existingCustomer.setRegisteredDate(customer.getRegisteredDate());
        existingCustomer.setAuthor(customer.isAuthor());

        // Guarda los cambios en la base de datos
        super.edit(existingCustomer);

        // Devuelve la respuesta con el cliente actualizado
        return Response.ok().entity(existingCustomer).build();
    }

    @DELETE
    @Path("{id}")
    @Secured
    public Response remove(@PathParam("id") Long id) {
        Customer customer = super.find(id);
        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        super.remove(customer);
        return Response.noContent().build();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    // Método actualizado para obtener el ID del último artículo publicado
    private Long getLastArticleIdForCustomer(Customer customer) {
        // Obtener la lista de artículos del cliente
        List<Article> articles = customer.getArticles();

        // Si no hay artículos, devolvemos null
        if (articles == null || articles.isEmpty()) {
            return null;
        }

        // Ordenar los artículos por fecha de publicación, del más reciente al más antiguo
        Optional<Article> lastArticle = articles.stream()
                .max(Comparator.comparing(Article::getDatePublished)); // Se asume que Article tiene un campo publishedDate

        // Si hay un artículo, devolver su ID
        return lastArticle.map(Article::getId).orElse(null);
    }
}