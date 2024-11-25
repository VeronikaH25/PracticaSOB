/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ElementCollection
    private List<String> topics; // Tópicos del artículo (máximo 2, p. ej., Web Programming, JavaScript)

    @Lob
    @Column(nullable = false)
    private String featuredImage; // URL o representación base64 de la imagen destacada

    @Lob
    @Column(nullable = false, length = 500)
    private String content; // Texto corto del artículo (ajustado a 500 palabras)

    // Constructores
    public Article() {}

    public Article(String title, String authorName, Date datePublished, int views, List<String> topics, String featuredImage, String content) {
        this.title = title;
        this.authorName = authorName;
        this.datePublished = datePublished;
        this.views = views;
        this.topics = topics;
        this.featuredImage = featuredImage;
        this.content = content;
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

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
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
}