/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import jakarta.persistence.*;
import java.util.Date;
/**
 *
 * @author veron
 */
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_gen")
    @SequenceGenerator(name = "customer_gen", sequenceName = "CUSTOMER_GEN", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // Nombre de usuario único
    
    @Column(nullable = false)
    private String password; // Contraseña del usuario
   
    @Column(nullable = false)
    private String firstName; // Nombre

    @Column(nullable = false)
    private String lastName; // Apellido

    @Column(nullable = false, unique = true)
    private String email; // Correo electrónico

    @Temporal(TemporalType.DATE)
    private Date registeredDate; // Fecha de registro

    @Column(nullable = false)
    private boolean isAuthor; // Si el usuario es autor de artículos

    @Transient
    private String lastArticleLink; // HATEOAS: enlace al último artículo si es autor

    // Constructores
    public Customer() {}

    public Customer(String username, String password, String firstName, String lastName, String email, Date registeredDate, boolean isAuthor) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registeredDate = registeredDate;
        this.isAuthor = isAuthor;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public boolean isAuthor() {
        return isAuthor;
    }

    public void setAuthor(boolean isAuthor) {
        this.isAuthor = isAuthor;
    }

    public String getLastArticleLink() {
        return lastArticleLink;
    }

    public void setLastArticleLink(String lastArticleLink) {
        this.lastArticleLink = lastArticleLink;
    }
}