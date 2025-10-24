package com.guanchedata.application.usecases;

import com.google.gson.Gson;
import com.guanchedata.infrastructure.adapters.*;
import com.guanchedata.infrastructure.ports.PathGenerator;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class BookProviderController {
    private final BookStorageDate storageDate;
    private static final Gson gson = new Gson();
    private final String datalakePath;

    public BookProviderController(BookStorageDate storageDate, String datalakePath) {
        this.storageDate = storageDate;
        this.datalakePath = datalakePath;
    }

    public static void main(String[] args){
        PathGenerator pathGenerator = new DateTimePathGenerator(Paths.get(args[0]));
        GutenbergBookContentSeparator separator = new GutenbergBookContentSeparator();
        BookStorageDate storageDate = new BookStorageDate(pathGenerator, separator);
        BookProviderController controller = new BookProviderController(storageDate, args[0]);

        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";}).start(7001);

        app.post("/ingest/{book_id}",controller::ingestBook);
        app.get("ingest/status/{book_id}", controller::status);
    }

    private void status(Context context) {
    }

    private void ingestBook(Context ctx) {
        int bookId = Integer.parseInt(ctx.pathParam("book_id"));
        try{

            GutenbergConnection connection = new GutenbergConnection();
            GutenbergFetch fetch = new GutenbergFetch();
            String response = fetch.fetchBook(connection.createConnection(bookId));
            Path savedPath = storageDate.save(bookId, response);

            Map<String, Object> responseAPI = Map.of(
                    "book_id", bookId,
                    "status", "downloaded",
                    "path", savedPath.toString()
            );

            ctx.result(gson.toJson(responseAPI));
        } catch (Exception e) {
            Map<String, Object> responseError = Map.of(
                    "book_id", bookId,
                    "status", "error"
            );

            ctx.result(gson.toJson(responseError));
        }
    }

}
