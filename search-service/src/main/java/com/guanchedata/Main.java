package com.guanchedata;

import com.guanchedata.mongoDB.MongoDBConnector;
import com.guanchedata.sqlite.SQLiteConnector;
import com.mongodb.client.*;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //args[0] is the path to the sqlite database
        SQLiteConnector sqliteConnector = new SQLiteConnector(args[0]);
        MongoDBConnector mongoDBConnector = new MongoDBConnector("mongodb://localhost:27017/",
                "SearchEngineInvertedIndex",
                "InvertedIndex");

        SearchService searchService = new  SearchService(mongoDBConnector, sqliteConnector);

        Map<String, Object> filters = new HashMap<>();
        List<Map<String, Object>> results = new ArrayList<>();
        results = searchService.search("horse", filters);

        System.out.println(results);
    }
}
