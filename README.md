# Vertx-Library
The Backend development of a web library that tracks, employees, customers and books, using : Java, Maven, Vertx, RestAPI, SQL

Specifically: RestAPI, Java 8, Vertx 4.0.0, Json, MariaDB, Maven , Postman

-----------------------------------------------
How it works:

I have chosen port 8091 for my httpServer to listen [httpServer.requestHandler(router).listen(8091);]
and my Database occupies port 3306 [/localhost:3306].

http://localhost:8091/login : is the starting point and instucts how the user can log in using data from the database (example users are Bill Papadas (admin) and Clarice Calderon (customer)).

http://localhost:8091/login/:firstname/:lastname : checks the database for the user data given and proceeds to a welcome him.

http://localhost:8091/login/:firstname/:lastname/logout : logs the existing user out

http://localhost:8091/login/:firstname/:lastname/Books : if the current user is a customer this shows the books he kas borrowed, 
                                                         if the user is an employee this shows all customers and their books.

http://localhost:8091/login/:firstname/:lastname/returnBook/:bookId/:borrowerId : only employees have access here and can return any                                                                                   books borrowed by any customer.

http://localhost:8091/login/:firstname/:lastname/returnBook/:bookId/:borrowerId : only employees have access here and can lend any                                                                                   book to any customer.

--- From here on in order to function we need to have Postman (https://www.postman.com/) to send GET, POST, PUT, DELETE requests --- 

http://localhost:8091/getBooks :  using Postman to send a GET request, this provides all books inside the Database in a .json format.

http://localhost:8091/postBook : using Postman to send a POST request of this style ->({"bookName" : 14}), a new book is added in the database

http://localhost:8091/putBook : using Postman to send a PUT request of this style ->({"bookName" : 14,"bookId" : 15}), a book's name is updated in the database

http://localhost:8091/deleteBook : using Postman to send a DELETE request of this style ->({"bookId" : 15}), a book is deleted from tha database.


-----------------------------------------------

Possible future changes and updates:

1. Make application more 'Restful'.

2. Implement a solid authentication method like OAuth 2.0 or at the very least a SQL Table Session Handler (currently just using static variables, which is a bad practice).

3. Scale up the applications capabilities with Frontend development, more uses, more diversity and cleaner code.

4. Break application into smaller but easier to read, understand and revisit parts (more classes, more queries, etc).

5. Improve Documentation.

