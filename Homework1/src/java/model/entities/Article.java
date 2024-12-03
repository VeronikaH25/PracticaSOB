/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
/**
 *
 * @author veron
 */
@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "article_gen")
    @SequenceGenerator(name = "article_gen", sequenceName = "ARTICLE_GEN", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String authorName; // Nombre del autor (p. ej., Andrew Zuo)

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date datePublished;

    @Column(nullable = false)
    private int views; // Número de visualizaciones (p. ej., 3300 representado como 3.3k)

    // Relación ManyToMany con la entidad Topic
    @ManyToMany
    @JoinTable(
        name = "article_topic", 
        joinColumns = @JoinColumn(name = "article_id"), 
        inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private List<Topic> topics; // Tópicos del artículo (máximo 2, p. ej., Web Programming, JavaScript)

    @Lob
    @Column(nullable = false)
    private String featuredImage; // URL o representación base64 de la imagen destacada

    @Lob
    @Column(nullable = false, length = 500)
    private String content; // Texto corto del artículo (ajustado a 500 palabras)

    // Relación Many-to-One con Customer (un artículo tiene un solo autor)
    @JsonbTransient  // Evita la serialización de los artículos
    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Customer author;
    
    @Column(nullable = false)
    private boolean isPrivate; // Campo que indica si el artículo es privado o no

    
    
    // Constructores
    public Article() {}

    public Article(String title, String authorName, Date datePublished, int views, List<Topic> topics, String featuredImage, String content, boolean isPrivate) {
        this.title = title;
        this.authorName = authorName;
        this.datePublished = datePublished;
        this.views = views;
        this.topics = topics;
        this.featuredImage = featuredImage;
        this.content = content;
        this.isPrivate = isPrivate;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public String getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(String featuredImage) {
        this.featuredImage = featuredImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content != null && content.length() > 500) {
            this.content = content.substring(0, 500); // Limitar a 500 caracteres
        } else {
            this.content = content;
        }
    }
    
    public Customer getAuthor() {
        return author;
    }

    public void setAuthor(Customer author) {
        this.author = author;
    }
    
    public boolean getisPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}