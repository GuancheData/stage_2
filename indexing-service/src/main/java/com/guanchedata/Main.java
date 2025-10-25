package com.guanchedata;
import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) {
        IndexingController indexingController = new IndexingController(new BookIndexer(args[0], args[1], args[2], args[3], args[4]), new IndexEraser(args[1],args[3],args[4]), new DatalakeBookIdExtractor(args[0]));
        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";}).start(7002);

        // first endpoint
        app.post("/index/update/{book_id}", indexingController::indexBook);

        // second endpoint
        app.post("/index/rebuild", indexingController::rebuildIndex);

    }
}
