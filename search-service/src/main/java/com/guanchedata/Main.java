package com.guanchedata;

import com.guanchedata.mongoDB.MongoDBConnector;
import com.guanchedata.sqlite.SQLiteConnector;
import com.mongodb.client.*;

import io.javalin.Javalin;
import org.bson.Document;
import io.javalin.json.JavalinGson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //args[0] is the path to the sqlite database
        SQLiteConnector sqliteConnector = new SQLiteConnector(args[0]);
        //args[1] is the mongodb connection uri
        //args[2] is the db name
        //args[3] is the collection name
        MongoDBConnector mongoDBConnector = new MongoDBConnector(args[1], args[2], args[3]);

        SearchService searchService = new SearchService(mongoDBConnector, sqliteConnector);
        SearchController searchController = new SearchController(searchService);

        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
            config.jsonMapper(new JavalinGson());
        }).start(7003);

        app.get("/search", searchController::getSearch);

        System.out.println("API running in port 7003");
    }
}
