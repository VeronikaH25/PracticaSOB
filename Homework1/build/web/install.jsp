<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import = "java.sql.*" %>
<%@ page import = "model.entities.*" %>
<%@ page import = "authn.*" %>
<%@ page import = "java.util.*" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Database SQL Load</title>
    </head>
    <style>
        .error {
            color: red;
        }
        pre {
            color: green;
        }
    </style>
    <body>
        <h2>Database SQL Load</h2>
        <%
            String dbname = "homework1";
            String schema = "ROOT";
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            /* Conexión a la base de datos */
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/" + dbname, "root", "root");
            Statement stmt = con.createStatement();
            
            /* Insertando datos */
            String data[] = new String[]{
                // Insertando tópicos
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Computer Science')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Web Development')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'AI')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Databases')",
                
                // Insertando credenciales para los usuarios
                "INSERT INTO " + schema + ".CREDENTIALS VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'maria14_tgn', '1234')",
                "INSERT INTO " + schema + ".CREDENTIALS VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'jana_lopz','1235')",
                "INSERT INTO " + schema + ".CREDENTIALS VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'lector123','1236')",
                
                // Insertando clientes con las credenciales
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Maria', 'Sevilla', 'maria14@example.com',  1, 1)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Jana', 'Lopez', 'janal@example.com', 1, 2)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Susana', 'Fernandez', 'susif@example.com', 0, 3)",

                // Insertando artículos con `authorName` y `author_id`
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Understanding Java Streams', DATE('2023-09-15'), 3300, 'image1.jpg', 'Java Streams simplify processing of collections and streams in functional programming style.', 'Maria Sevilla', 1, 0)", // Artículo no privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Exploring REST APIs', DATE('2023-08-10'), 1200, 'image2.jpg', 'REST APIs are essential for building scalable services.', 'Jana Lopez', 2, 0)", // Artículo no privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Modern Database Design', DATE('2023-07-20'), 2200, 'image3.jpg', 'This article covers normalization and database schemas.', 'Susana Fernandez', 3, 1)" // Artículo privado
            };

            // Insertar los datos en las tablas
            for (String datum : data) {
                if (stmt.executeUpdate(datum) <= 0) {
                    out.println("<span class='error'>Error inserting data: " + datum + "</span>");
                    return;
                }
                out.println("<pre> -> " + datum + "<pre>");
            }

            // Relacionar artículos con tópicos
            String[] articleTopicRelations = new String[] {
                // Asumimos que los artículos tienen ID de 1 a 3, y tópicos 1, 2, 3
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (1, 1)",  // 'Understanding Java Streams' -> 'Computer Science'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (1, 2)",  // 'Understanding Java Streams' -> 'Web Development'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (2, 2)",  // 'Exploring REST APIs' -> 'Web Development'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (2, 3)",  // 'Exploring REST APIs' -> 'AI'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (3, 1)"   // 'Modern Database Design' -> 'Computer Science'
            };

            // Insertar las relaciones entre artículos y tópicos
            for (String relation : articleTopicRelations) {
                if (stmt.executeUpdate(relation) <= 0) {
                    out.println("<span class='error'>Error inserting relation: " + relation + "</span>");
                    return;
                }
                out.println("<pre> -> " + relation + "<pre>");
            }
        %>
        <button onclick="window.location='<%=request.getSession().getServletContext().getContextPath()%>'">Go home</button>
    </body>
</html>
