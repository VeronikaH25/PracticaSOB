/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import model.entities.Customer;
import model.entities.Article;

/**
 *
 * @author veron
 */
@Stateless
@Path("/api/v1/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerFacadeREST {

    @PersistenceContext
    private EntityManager em;

    // GET /rest/api/v1/customer
    @GET
    public Response getCustomers() {
        List<Customer> customers = em.createQuery("SELECT c FROM Customer c", Customer.class)
                .getResultList();

        // Añadir el enlace al último artículo si el cliente es autor
        for (Customer customer : customers) {
            if (customer.isAuthor()) {
                Article lastArticle = getLastArticleByAuthor(customer.getUsername());
                if (lastArticle != null) {
                    customer.setLastArticleLink("/api/v1/article/" + lastArticle.getId());
                }
            }
        }

        return Response.ok(customers).build();
    }

    // GET /rest/api/v1/customer/{id}
    @GET
    @Path("/{id}")
    public Response getCustomer(@PathParam("id") Long id) {
        Customer customer = em.find(Customer.class, id);
        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Añadir el enlace al último artículo si el cliente es autor
        if (customer.isAuthor()) {
            Article lastArticle = getLastArticleByAuthor(customer.getUsername());
            if (lastArticle != null) {
                customer.setLastArticleLink("/api/v1/article/" + lastArticle.getId());
            }
        }

        return Response.ok(customer).build();
    }

    // Método auxiliar para obtener el último artículo de un autor
    private Article getLastArticleByAuthor(String authorUsername) {
        try {
            return em.createQuery("SELECT a FROM Article a WHERE a.authorName = :author ORDER BY a.datePublished DESC", Article.class)
                    .setParameter("author", authorUsername)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // Si no se encuentra ningún artículo, retornar null
        }
    }

    // PUT /rest/api/v1/customer/{id}
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, Customer customerData) {
        Customer existingCustomer = em.find(Customer.class, id);
        if (existingCustomer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Actualizar los datos del cliente
        existingCustomer.setFirstName(customerData.getFirstName());
        existingCustomer.setLastName(customerData.getLastName());
        existingCustomer.setEmail(customerData.getEmail());
        em.merge(existingCustomer);

        return Response.ok(existingCustomer).build();
    }

    // Método auxiliar para autenticar al usuario
    private Customer authenticateUser(String token) {
        // Implementar la lógica para validar el token de autenticación
        // Este código debe verificar el token en el encabezado y autenticar al usuario
        // Por ejemplo, mediante un sistema de autenticación basado en JWT o clave API
        // Esto dependerá de tu mecanismo de autenticación.
        return null; // Devolver el cliente autenticado, o null si no se puede autenticar
    }
}