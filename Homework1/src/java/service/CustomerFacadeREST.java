/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.entities.Customer;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import authn.Secured;
import java.util.List;


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
        // Añadir los enlaces de los últimos artículos a los usuarios que son autores
        for (Customer customer : customers) {
            if (customer.isAuthor()) {
                customer.setLastArticleLink("/article/" + getLastArticleIdForCustomer(customer.getId()));
            }
        }
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
        // Omite la contraseña y añade el enlace al artículo si el usuario es autor
        if (customer.isAuthor()) {
            customer.setLastArticleLink("/article/" + getLastArticleIdForCustomer(id));
        }
        return Response.ok().entity(customer).build();
    }

    @PUT
@Path("{id}")
@Secured
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public Response edit(@PathParam("id") Long id, Customer customer) {
    Customer existingCustomer = super.find(id);
    if (existingCustomer == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // Si la entidad Customer tiene una relación con Credentials, deberíamos actualizarla
    if (existingCustomer.getCredentials() != null) {
        // Aquí se actualiza la relación con Credentials, no directamente en Customer
        existingCustomer.getCredentials().setUsername(customer.getCredentials().getUsername());
        existingCustomer.getCredentials().setPassword(customer.getCredentials().getPassword());
    }

    // Actualiza solo los demás campos de Customer, excepto los relacionados con las credenciales.
    existingCustomer.setFirstName(customer.getFirstName());
    existingCustomer.setLastName(customer.getLastName());
    existingCustomer.setEmail(customer.getEmail());
    existingCustomer.setRegisteredDate(customer.getRegisteredDate());
    existingCustomer.setAuthor(customer.isAuthor());

    super.edit(existingCustomer);
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

    // Este método simula la obtención del último artículo de un usuario.
    private int getLastArticleIdForCustomer(Long customerId) {
        // Aquí puedes implementar la lógica real para obtener el último artículo del usuario.
        // Esta es una simulación que retorna un ID ficticio.
        return 1; // Ejemplo de ID de artículo
    }
}