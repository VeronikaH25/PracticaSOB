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
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Cybersecurity')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Data Science')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Cloud Computing')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Machine Learning')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Software Engineering')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'DevOps')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Quantum Computing')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Game Development')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Big Data')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Mobile App Development')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Augmented Reality')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Virtual Reality')",
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Blockchain')",
               
                // Insertando credenciales para los usuarios
                "INSERT INTO " + schema + ".CREDENTIALS (id, username,password) VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'maria14_tgn', '1234')",
                "INSERT INTO " + schema + ".CREDENTIALS (id, username,password) VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'jana_lopz','1235')",
                "INSERT INTO " + schema + ".CREDENTIALS (id, username,password) VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'lector123','1236')",
                "INSERT INTO " + schema + ".CREDENTIALS (id, username, password) VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'john_doe', 'password1')",
                "INSERT INTO " + schema + ".CREDENTIALS (id, username, password) VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'alice_w', 'securepass2')",
                "INSERT INTO " + schema + ".CREDENTIALS (id, username, password) VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'charlie_brown', 'mypassword3')",
                "INSERT INTO " + schema + ".CREDENTIALS (id, username, password) VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'david_smith', 'passcode4')",
                "INSERT INTO " + schema + ".CREDENTIALS (id, username, password) VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'emily_jones', 'key12345')",
                "INSERT INTO " + schema + ".CREDENTIALS (id, username, password) VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'frank_white', 'letmein6')",
                "INSERT INTO " + schema + ".CREDENTIALS (id, username, password) VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'grace_h', 'topsecret7')",
                "INSERT INTO " + schema + ".CREDENTIALS (id, username, password) VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'harry_p', 'open_sesame8')",
                "INSERT INTO " + schema + ".CREDENTIALS (id, username, password) VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'isabella_k', 'passw0rd9')",
                "INSERT INTO " + schema + ".CREDENTIALS (id, username, password) VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'jack_black', 'hunter10')",

               
                // Insertando clientes con las credenciales
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Maria', 'Sevilla', 'maria14@example.com',  1, 1)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Jana', 'Lopez', 'janal@example.com', 1, 2)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Susana', 'Fernandez', 'susif@example.com', 0, 3)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'John', 'Doe', 'johndoe@example.com', 1, 4)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Alice', 'Williams', 'alicew@example.com', 0, 5)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Charlie', 'Brown', 'charlieb@example.com', 1, 6)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'David', 'Smith', 'davids@example.com', 1, 7)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Emily', 'Jones', 'emilyj@example.com', 0, 8)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Frank', 'White', 'frankw@example.com', 1, 9)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Grace', 'Hopper', 'graceh@example.com', 1, 10)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Harry', 'Potter', 'harryp@example.com', 1, 11)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Isabella', 'King', 'isabellak@example.com', 0, 12)",
                "INSERT INTO " + schema + ".CUSTOMER (id, firstName, lastName, email, isAuthor, credentials_id) VALUES (NEXT VALUE FOR CUSTOMER_GEN, 'Jack', 'Black', 'jackb@example.com', 1, 13)",

                // Insertando artículos con `authorName` y `author_id`
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Understanding Java Streams', DATE('2023-09-15'), 3300, 'image1.jpg', 'Java Streams simplify processing of collections and streams in functional programming style.', 'maria14_tgn', 1, 0)", // Artículo no privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Exploring REST APIs', DATE('2023-08-10'), 1200, 'image2.jpg', 'REST APIs are essential for building scalable services.', 'jana_lopz', 2, 0)", // Artículo no privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Modern Database Design', DATE('2023-07-20'), 2200, 'image3.jpg', 'This article covers normalization and database schemas.', 'lector123', 3, 1)", // Artículo privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'java tutorials', DATE('2023-07-20'), 2500, 'image3.jpg', 'This article contains a java tutorial.', 'lector123', 3, 1)",
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Introduction to Cybersecurity', DATE('2023-06-15'), 1500, 'cyber1.jpg', 'Learn the basics of cybersecurity and how to protect your systems.', 'john_doe', 4, 0)", // Artículo no privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Getting Started with Data Science', DATE('2023-05-10'), 1800, 'data1.jpg', 'A beginner-friendly guide to exploring data science concepts.', 'alice_w', 5, 0)", // Artículo no privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Cloud Computing Explained', DATE('2023-04-20'), 2500, 'cloud1.jpg', 'An overview of cloud computing and its benefits.', 'charlie_brown', 6, 1)", // Artículo privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Demystifying Machine Learning', DATE('2023-03-30'), 3000, 'ml1.jpg', 'Breaking down machine learning concepts for beginners.', 'david_smith', 7, 0)", // Artículo no privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'The Evolution of Software Engineering', DATE('2023-02-25'), 2200, 'se1.jpg', 'How software engineering has evolved over the decades.', 'emily_jones', 8, 0)", // Artículo no privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'DevOps Best Practices', DATE('2023-01-10'), 3100, 'devops1.jpg', 'Learn the best practices for adopting DevOps in your organization.', 'frank_white', 9, 0)", // Artículo no privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Understanding Quantum Computing', DATE('2022-12-15'), 4000, 'quantum1.jpg', 'An introduction to the principles of quantum computing.', 'grace_h', 10, 1)", // Artículo privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Building Games with Unity', DATE('2022-11-20'), 2800, 'game1.jpg', 'Learn how to develop games using the Unity engine.', 'harry_p', 11, 0)", // Artículo no privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Big Data for Beginners', DATE('2022-10-25'), 2700, 'bigdata1.jpg', 'A beginner-friendly guide to understanding big data.', 'isabella_k', 12, 0)", // Artículo no privado
                "INSERT INTO " + schema + ".ARTICLE (id, title, datePublished, views, featuredImage, content, authorName, author_id, isPrivate) VALUES (NEXT VALUE FOR ARTICLE_GEN, 'Mobile App Development Basics', DATE('2022-09-10'), 3500, 'mobile1.jpg', 'A guide to getting started with mobile app development.', 'jack_black', 13, 1)" // Artículo privado
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
                // 'Understanding Java Streams' -> 'Computer Science', 'Web Development'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (1, 1)",  // 'Computer Science'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (1, 2)",  // 'Web Development'

                // 'Exploring REST APIs' -> 'Web Development', 'AI'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (2, 2)",  // 'Web Development'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (2, 3)",  // 'AI'

                // 'Modern Database Design' -> 'Databases', 'Computer Science'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (3, 4)",  // 'Databases'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (3, 1)",  // 'Computer Science'

                // 'Java Tutorials' -> 'Web Development', 'Software Engineering'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (4, 2)",  // 'Web Development'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (4, 9)",  // 'Software Engineering'

                // 'Introduction to Cybersecurity' -> 'Cybersecurity', 'Data Science'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (5, 5)",  // 'Cybersecurity'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (5, 6)",  // 'Data Science'

                // 'Getting Started with Data Science' -> 'Data Science', 'Big Data'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (6, 6)",  // 'Data Science'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (6, 13)", // 'Big Data'

                // 'Cloud Computing Explained' -> 'Cloud Computing', 'DevOps'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (7, 7)",  // 'Cloud Computing'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (7, 10)", // 'DevOps'

                // 'Demystifying Machine Learning' -> 'AI', 'Machine Learning'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (8, 3)",  // 'AI'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (8, 8)",  // 'Machine Learning'

                // 'The Evolution of Software Engineering' -> 'Software Engineering', 'Quantum Computing'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (9, 9)",  // 'Software Engineering'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (9, 11)", // 'Quantum Computing'

                // 'DevOps Best Practices' -> 'DevOps', 'Cloud Computing'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (10, 10)", // 'DevOps'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (10, 7)",  // 'Cloud Computing'

                // 'Understanding Quantum Computing' -> 'Quantum Computing', 'AI'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (11, 11)", // 'Quantum Computing'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (11, 3)",  // 'AI'

                // 'Building Games with Unity' -> 'Game Development', 'Virtual Reality'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (12, 12)", // 'Game Development'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (12, 16)", // 'Virtual Reality'

                // 'Big Data for Beginners' -> 'Big Data', 'Data Science'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (13, 13)", // 'Big Data'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (13, 6)",  // 'Data Science'

                // 'Mobile App Development Basics' -> 'Mobile App Development', 'Augmented Reality'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (14, 14)", // 'Mobile App Development'
                "INSERT INTO " + schema + ".ARTICLE_TOPIC VALUES (14, 15)"  // 'Augmented Reality'
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
        <button onclick="window.location = '<%=request.getSession().getServletContext().getContextPath()%>'">Go home</button>
    </body>
</html>