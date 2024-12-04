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
import authn.Credentials;
import com.sun.xml.messaging.saaj.util.Base64;
import java.util.List;
import java.util.Comparator;
import java.util.Optional;
import java.util.StringTokenizer;


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
    // Validar que Authorization esté presente
    if (authorization == null || authorization.isEmpty()) {
        return Response.status(Response.Status.FORBIDDEN).entity("Authorization header is missing").build();
    }

    // Busca el cliente existente
    Customer existingCustomer = super.find(id);
    if (existingCustomer == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Customer not found").build();
    }

    // Mostrar el username almacenado del Customer
    System.out.println("Customer Username from DB: " + existingCustomer.getCredentials().getUsername());

    // Extraer las credenciales de la cabecera Authorization (Basic Auth)
    String username = null;
    String password = null;
    try {
        String auth = authorization.replace("Basic ", "");
        String decodedAuth = new String(Base64.base64Decode(auth));
        StringTokenizer tokenizer = new StringTokenizer(decodedAuth, ":");
        username = tokenizer.nextToken();
        password = tokenizer.nextToken();
        
        // Depurar los valores de username y password
        System.out.println("Decoded Username from Auth Header: " + username);
        System.out.println("Decoded Password from Auth Header: " + password);
    } catch (Exception e) {
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid authentication format").build();
    }

    // Comparar el username en la base de datos con el que llega de la cabecera
    if (!existingCustomer.getCredentials().getUsername().equals(username)) {
        // Si no coincide, devolver el mensaje de error con los detalles de los usernames
        String message = String.format(
            "You are not authorized to edit this customer. Username from DB: '%s', Username from credentials: '%s'",
            existingCustomer.getCredentials().getUsername(),
            username
        );
        System.out.println("Username mismatch: Customer Username (" + existingCustomer.getCredentials().getUsername() 
                           + ") doesn't match provided Username (" + username + ")");
        return Response.status(Response.Status.FORBIDDEN).entity(message).build();
    }

    // Comparar el password en la base de datos con el que llega de la cabecera
    if (!existingCustomer.getCredentials().getPassword().equals(password)) {
        // Si no coincide, devolver el mensaje de error
        System.out.println("Password mismatch: Customer Password (" + existingCustomer.getCredentials().getPassword() 
                           + ") doesn't match provided Password (" + password + ")");
        return Response.status(Response.Status.FORBIDDEN).entity("Incorrect password").build();
    }

    // Si las credenciales son correctas, actualizar la información del cliente
    if (customer.getCredentials() != null) {
        existingCustomer.getCredentials().setUsername(customer.getCredentials().getUsername());
        existingCustomer.getCredentials().setPassword(customer.getCredentials().getPassword());
    }

    // Actualizar los demás campos del cliente
    if (customer.getFirstName() != null) existingCustomer.setFirstName(customer.getFirstName());
    if (customer.getLastName() != null) existingCustomer.setLastName(customer.getLastName());
    if (customer.getEmail() != null) existingCustomer.setEmail(customer.getEmail());
    if (customer.getRegisteredDate() != null) existingCustomer.setRegisteredDate(customer.getRegisteredDate());
    existingCustomer.setAuthor(customer.isAuthor());

    // Guardar los cambios en la base de datos
    super.edit(existingCustomer);

    // Devolver la respuesta con el cliente actualizado
    return Response.ok().entity("Customer updated successfully").build();
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