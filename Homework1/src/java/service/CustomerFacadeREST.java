/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.List;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.entities.Customer;
import model.entities.Article;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;


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
        super(Customer.class);  // Llamada al constructor de AbstractFacade con Customer
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;  // Retorna el EntityManager para interactuar con la base de datos
    }

    // GET /rest/api/v1/customer
    @GET
    public Response getAllCustomers(@HeaderParam("Authorization") String token) {
        List<Customer> customers = findAll();

        // Recorrer los clientes y agregar el enlace al último artículo si es autor
        for (Customer customer : customers) {
            if (customer.isAuthor()) {
                // Obtiene el último artículo publicado por el autor
                Article lastArticle = getLastArticleByAuthor(customer);
                if (lastArticle != null) {
                    // Añadir un enlace HATEOAS
                    customer.setLastArticleLink("/article/" + lastArticle.getId());
                }
            }
            // No incluir información confidencial como contraseñas
            customer.setEmail(null); // Eliminar email si es necesario, o cualquier dato confidencial
        }

        return Response.ok(new GenericEntity<List<Customer>>(customers) {}).build();
    }

    // Método auxiliar para obtener el último artículo publicado por el autor
    private Article getLastArticleByAuthor(Customer customer) {
        try {
            return em.createQuery("SELECT a FROM Article a WHERE a.author.username = :username ORDER BY a.datePublished DESC", Article.class)
                     .setParameter("username", customer.getUsername())
                     .setMaxResults(1)
                     .getSingleResult();
        } catch (Exception e) {
            return null;  // Si no hay artículos, retornar null
        }
    }

    // GET /rest/api/v1/customer/{id}
    @GET
    @Path("/{id}")
    public Response getCustomer(@PathParam("id") Long id, @HeaderParam("Authorization") String token) {
        Customer customer = find(id);
        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Eliminar información confidencial antes de enviar la respuesta
        customer.setEmail(null); // Eliminar email
        // customer.setPassword(null); // Eliminar contraseña si la tienes

        return Response.ok(customer).build();
    }

    // PUT /rest/api/v1/customer/{id}
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, Customer customer, @HeaderParam("Authorization") String token) {
        Customer existingCustomer = find(id);
        if (existingCustomer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Verificar que el usuario esté autenticado y sea el mismo usuario
        if (!isAuthenticated(token, id)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // No permitir modificar la contraseña a través de la API
        existingCustomer.setFirstName(customer.getFirstName());
        existingCustomer.setLastName(customer.getLastName());
        existingCustomer.setEmail(customer.getEmail());
        // No modificar la contraseña a través de la API

        edit(existingCustomer); // Guardar los cambios
        return Response.ok(existingCustomer).build();
    }

    // Método auxiliar para verificar la autenticación (esto es solo un ejemplo)
    private boolean isAuthenticated(String token, Long id) {
        // Implementar aquí la lógica para verificar que el token es válido y corresponde al usuario con el id especificado
        // Por ejemplo, verificar que el token pertenezca al usuario correspondiente en la base de datos.
        // Si el token es válido, retornamos true. Si no, false.
        return true;  // Retornar true por ahora para simular la autenticación
    }
}