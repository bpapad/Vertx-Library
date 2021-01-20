package com.library.vertx.classes;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;

import java.util.List;

public class Database extends AbstractVerticle {
    private JDBCClient dbClient;

    public Database(Vertx vertx){

        JsonObject dbConfig = new JsonObject();
        // serverTimezone = GMT% 2B8 solve the problem zone when connecting to the database
        dbConfig.put("url", "jdbc:mysql://localhost:3306/vertxlib?serverTimezone=GMT%2B8");
        dbConfig.put("driver_class", "com.mysql.cj.jdbc.Driver");
        dbConfig.put("user", "root");
        dbConfig.put("password", "admin");

        // Create a client
        dbClient = JDBCClient.createShared(vertx, dbConfig);
    }

    public JDBCClient getDbClient(){
        return dbClient;
    }
}
