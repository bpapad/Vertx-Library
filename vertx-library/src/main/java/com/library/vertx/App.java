package com.library.vertx;

import com.library.vertx.classes.Book;
import com.library.vertx.classes.Database;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;

public class App{
    static String userRights;
    static int userId;

    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();
        JDBCClient jdbcClient = new Database(vertx).getDbClient();

        HttpServer httpServer = vertx.createHttpServer();

        Router router =  Router.router(vertx);

//---------------------------------   LOGIN ROUTE AND INSTRUCTIONS   (INFORMS THE USER HOW TO LOG IN)   -----------------------------------------------------------------------------------------------------------------------------------
        /**
         *  LOGIN ROUTE AND INSTRUCTIONS   (INFORMS THE USER HOW TO LOG IN)
         */
        Route loginRoute = router.route("/login").handler(routingContext -> {
            System.out.println("to login -> /login/firstname/lastname\n");
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            response.write("to login -> /login/firstname/lastname\n");
            response.end();
        });

//---------------------------------   LOGOUT ROUTE    -----------------------------------------------------------------------------------------------------------------------------------
        /**
         *  LOGOUT ROUTE
         */
        Route logoutRoute = router.route("/login/:firstname/:lastname/logout").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            if(App.userId==0){
                response.end("You must be logged in to log out..\n\nto login -> /login/firstname/lastname");
            }
            else{
                response.end("You logged out.");
                App.userId=0;
            }
        });
//---------------------------------   LOGGED IN USER ROUTE INFORMATION AND INSTRUCTIONS  (USER PROFILE)  -----------------------------------------------------------------------------------------------------------------------------------
        /**
         *  LOGGED IN USER ROUTE INFORMATION AND INSTRUCTIONS  (USER PROFILE)
         */
        Route loggedUser = router.route("/login/:firstname/:lastname").handler(BodyHandler.create()).handler(routingContext -> {
            String firstname = routingContext.request().getParam("firstname");
            String lastname = routingContext.request().getParam("lastname");
            String sql = "SELECT firstName, lastName, rights, id FROM people WHERE firstName = ? AND lastName = ?";

            JsonArray params = new JsonArray().add(firstname).add(lastname);
            jdbcClient.queryWithParams(sql,params,queryRes -> {
                HttpServerResponse response = routingContext.response();
                response.setChunked(true);
                try{
                    if(queryRes.succeeded()){

                        response.write("Welcome!\n");
                        ResultSet resultSet = queryRes.result();
                        JsonObject row = resultSet.getRows().get(0);
                        System.out.println(row.toString());

                        //SAVING CURRENT USERS RIGHTS AND ID FOR LATER USE
                        App.userRights = row.getString("rights");
                        App.userId = row.getInteger("id");


                        response.write(row.getString("rights")+": "+row.getString("firstName")+" "+row.getString("lastName"));
                        System.out.println(row.getString("rights")+": "+row.getString("firstName")+" "+row.getString("lastName"));

                    }else{
                        System.out.println ( "Error querying the database!");
                        response.setStatusCode(404);

                    }
                }catch(IndexOutOfBoundsException e){
                    response.write("Unregistered user..");
                }

                response.end();
            });
        });

//---------------------------------   AFTER A USER LOGS IN HERE HE CAN SEE THE BOOKS HE HAS BORROWED BUT IF HE IS AN EMPLOYEE HE CAN SEE THE WHOLE LIST OF BORROWED BOOKS  --------------------------------------------------------------
        /**
         *  AFTER A USER LOGS IN HERE HE CAN SEE THE BOOKS HE HAS BORROWED BUT IF HE IS AN EMPLOYEE HE CAN SEE THE WHOLE LIST OF BORROWED BOOKS
         */

        Route getBooksBorrowed = router.route("/login/:firstname/:lastname/Books").handler(BodyHandler.create()).handler(routingContext -> {
            String firstname = routingContext.request().getParam("firstname");
            String lastname = routingContext.request().getParam("lastname");

            if (App.userId==0){
                HttpServerResponse response = routingContext.response();
                response.setChunked(true);
                response.end("Log in first by -> /login/firstname/lastname");
            }

            if(App.userRights.equals("borrower")){
                String sql = "SELECT books.bookName, books.bookId FROM books JOIN lending_log ON lending_log.bookId = books.bookId JOIN people ON people.id = lending_log.borrowerId WHERE people.id = ?";
                JsonArray params = new JsonArray().add(App.userId);
                jdbcClient.queryWithParams(sql,params,queryRes -> {
                    HttpServerResponse response = routingContext.response();
                    response.setChunked(true);
                    try{
                        if(queryRes.succeeded()){

                            response.write("Books borrowed: \n\n");
                            ResultSet resultSet = queryRes.result();
                            List<JsonObject> rows = resultSet.getRows();
                            if(rows.size()<1){ response.write("No books borrowed."); }
                            for (JsonObject row:rows){
                                response.write(row.getString("bookId")+". "+row.getString("bookName")+"\n");
                            }
                        }else{
                            System.out.println ( "Error querying the database!");
                            response.setStatusCode(404);

                        }
                    }catch(NullPointerException e){
                        response.write("No books borrowed.");
                    }

                    response.end();
                });

            }else{
                String sql = "SELECT books.bookName,books.bookId,people.firstName, people.lastName, people.id  \n" +
                        "FROM books \n" +
                        "JOIN lending_log ON lending_log.bookId = books.bookId \n" +
                        "JOIN people ON people.id = lending_log.borrowerId\n" +
                        "ORDER BY people.id";
                jdbcClient.query(sql,queryRes -> {
                    HttpServerResponse response = routingContext.response();
                    response.setChunked(true);

                    if(queryRes.succeeded()){

                        ResultSet resultSet = queryRes.result();
                        List<JsonObject> rows = resultSet.getRows();
                        rows.forEach(System.out::println);
                        for (JsonObject row:rows) {
                            response.write("User: "+row.getString("id")+". "+row.getString("firstName")+
                                    " "+row.getString("lastName")+" -> "+row.getString("bookName")+" (bookId = "+
                                    row.getString("bookId")+")\n\n");
                        }

                    }else{

                        System.out.println ( "Error querying the database!");
                        response.setStatusCode(404);

                    }
                    response.end();
                });
            }



        });

//---------------------------------   AN EMPLOYEE CAN RETURN BORROWED BOOKS BACK TO THE LIBRARY AND THA DATABASE IS BEING UPDATED  --------------------------------------------------------------
        /**
         *  AN EMPLOYEE CAN RETURN BORROWED BOOKS BACK TO THE LIBRARY AND THA DATABASE IS BEING UPDATED
         */
        Route returnBook = router.route("/login/:firstname/:lastname/returnBook/:bookId/:borrowerId").handler(BodyHandler.create()).handler(routingContext -> {
            String firstname = routingContext.request().getParam("firstname");
            String lastname = routingContext.request().getParam("lastname");
            int bookId = Integer.parseInt(routingContext.request().getParam("bookId"));
            int borrowerId = Integer.parseInt(routingContext.request().getParam("borrowerId"));

            if (App.userId==0){
                HttpServerResponse response = routingContext.response();
                response.setChunked(true);
                response.end("Log in first by -> /login/firstname/lastname");
            }
            else if (App.userRights.equals("borrower")){
                HttpServerResponse response = routingContext.response();
                response.setChunked(true);
                response.end("Only Employees can return the customers books\n\n\nLog in with employee/admin credentials -> /login/firstname/lastname");
            }
            else{
                String sql = "DELETE lending_log FROM lending_log\n" +
                        "JOIN books ON books.bookId = lending_log.bookId\n" +
                        "JOIN people ON people.id = lending_log.borrowerId\n" +
                        "WHERE books.bookId = ? AND people.id = ?";
                JsonArray params = new JsonArray().add(bookId).add(borrowerId);
                jdbcClient.queryWithParams(sql,params,queryRes -> {
                    HttpServerResponse response = routingContext.response();
                    response.setChunked(true);
                    try{
                        if(queryRes.succeeded()){

                            response.write("Book returned! \n\n");
                        }else{
                            System.out.println ( "Error querying the database!");
                            response.setStatusCode(404);

                        }
                    }catch(NullPointerException e){
                        response.write("No such book is borrowed by this user.");
                    }

                    response.end();
                });
            }
        });

//---------------------------------   AN EMPLOYEE CAN LEND A BOOK A CUSTOMER AND THE DATABASE IS BEING UPDATED  --------------------------------------------------------------
        /**
         *  AN EMPLOYEE CAN LEND A BOOK A CUSTOMER AND THE DATABASE IS BEING UPDATED
         */
        Route lendBook = router.route("/login/:firstname/:lastname/lendBook/:bookId/:borrowerId").handler(BodyHandler.create()).handler(routingContext -> {
            String firstname = routingContext.request().getParam("firstname");
            String lastname = routingContext.request().getParam("lastname");
            int bookId = Integer.parseInt(routingContext.request().getParam("bookId"));
            int borrowerId = Integer.parseInt(routingContext.request().getParam("borrowerId"));

            if (App.userId==0){
                HttpServerResponse response = routingContext.response();
                response.setChunked(true);
                response.end("Log in first by -> /login/firstname/lastname");
            }
            else if (App.userRights.equals("borrower")){
                HttpServerResponse response = routingContext.response();
                response.setChunked(true);
                response.end("Only Employees can return the customers books\n\n\nLog in with employee/admin credentials -> /login/firstname/lastname");
            }
            else{
                String sql = "INSERT INTO lending_log (lending_log.bookId, lending_log.borrowerId) VALUES (?,?)";
                JsonArray params = new JsonArray().add(bookId).add(borrowerId);
                jdbcClient.queryWithParams(sql,params,queryRes -> {
                    HttpServerResponse response = routingContext.response();
                    response.setChunked(true);
                    try{
                        if(queryRes.succeeded()){

                            response.write("Book lended! \n\n");
                        }else{
                            System.out.println ( "Error querying the database!");
                            response.setStatusCode(404);

                        }
                    }catch(NullPointerException e){
                        response.write("Book or customer are not registered");
                    }

                    response.end();
                });
            }
        });
//---------------------------------   GET BOOKS ROUTE AND INSTRUCTIONS  (SHOWS ALL BOOKS INSIDE THE DATABASE)  -----------------------------------------------------------------------------------------------------------------------------------
        /**
         *  GET BOOKS ROUTE AND INSTRUCTIONS  (SHOWS ALL BOOKS INSIDE THE DATABASE)
         */
        Route getBooksRoute = router.get("/getBooks").produces("*/json").handler(BodyHandler.create()).handler(routingContext -> {
            String sql = "SELECT * FROM books";
            jdbcClient.query(sql,queryRes -> {
                HttpServerResponse response = routingContext.response();
                response.setChunked(true);

                if(queryRes.succeeded()){

                    ResultSet resultSet = queryRes.result();
                    List<JsonObject> rows = resultSet.getRows();
                    rows.forEach(System.out::println);
                    for (JsonObject row:rows) {
                        //response.write(row.encodePrettily()+"\n");
                        response.write("Book: "+row.getString("bookId")+". "+row.getString("bookName")+"\n\n");
                    }

                }else{

                    System.out.println ( "Error querying the database!");
                    response.setStatusCode(404);

                }
                response.end();
            });
        });

//---------------------------------   POST BOOK ROUTE  (INSERTS A NEW BOOK IN THE DATABASE USING THE POST METHOD)  -----------------------------------------------------------------------------------------------------------------------------------
        /**
         *  POST BOOK ROUTE  (INSERTS A NEW BOOK IN THE DATABASE USING THE POST METHOD)
         */
        Route postBook = router.post("/postBook").handler(BodyHandler.create()).handler(routingContext -> {
            System.out.println(routingContext.getBodyAsString());
            String sql = "INSERT INTO books (bookName) VALUES (?)";
            JsonObject jsonObject = new JsonObject(routingContext.getBodyAsString());

            Book book = new Book(jsonObject.getString("bookName"));
            System.out.println(book.getBookName());
            JsonArray params = new JsonArray().add(book.getBookName());

            jdbcClient.queryWithParams(sql,params,queryRes -> {
                HttpServerResponse response = routingContext.response();
                response.setChunked(true);

                if(queryRes.succeeded()){

                    response.write("Success!");

                }else{

                    System.out.println ( "Error querying the database!");
                    response.setStatusCode(404);

                }
                response.end();
            });
        });

//---------------------------------   PUT BOOK ROUTE  (UPDATES A BOOK'S NAME IN THE DATABASE USING THE PUT METHOD)  -----------------------------------------------------------------------------------------------------------------------------------
        /**
         *  PUT BOOK ROUTE  (UPDATES A BOOK'S NAME IN THE DATABASE USING THE PUT METHOD)
         */
        Route putBook = router.put("/putBook").handler(BodyHandler.create()).handler(routingContext -> {
            System.out.println(routingContext.getBodyAsString());
            String sql = "UPDATE books SET bookName = ? WHERE bookId = ?";
            JsonObject jsonObject = new JsonObject(routingContext.getBodyAsString());
            Book book = new Book(jsonObject.getString("bookName"),jsonObject.getInteger("bookId"));
            System.out.println(book.getBookName() +" "+ book.getBookId() );
            JsonArray params = new JsonArray().add(book.getBookName()).add(book.getBookId());
            jdbcClient.queryWithParams(sql,params,queryRes -> {
                HttpServerResponse response = routingContext.response();
                response.setChunked(true);

                if(queryRes.succeeded()){

                    response.write("Success!");

                }else{

                    System.out.println ( "Error querying the database!");
                    response.setStatusCode(404);

                }
                response.end();
            });
        });

//---------------------------------   DELETE BOOK ROUTE  (DELETES A BOOK FROM THE DATABASE USING THE DELETE METHOD)  -----------------------------------------------------------------------------------------------------------------------------------
        /**
         *  DELETE BOOK ROUTE  (DELETES A BOOK FROM THE DATABASE USING THE DELETE METHOD)
         */
        Route deleteBook = router.delete("/deleteBook").handler(BodyHandler.create()).handler(routingContext -> {
            System.out.println(routingContext.getBodyAsString());
            String sql = "DELETE FROM books WHERE bookId = ?";
            JsonObject jsonObject = new JsonObject(routingContext.getBodyAsString());
            System.out.println(jsonObject.getString("bookId"));
            JsonArray params = new JsonArray().add(jsonObject.getString("bookId"));
            jdbcClient.queryWithParams(sql,params,queryRes -> {
                HttpServerResponse response = routingContext.response();
                response.setChunked(true);

                if(queryRes.succeeded()){

                    response.write("Success!");

                }else{

                    System.out.println ( "Error querying the database!");
                    response.setStatusCode(404);

                }
                response.end();
            });
        });



        httpServer.requestHandler(router).listen(8091);
    }
}
