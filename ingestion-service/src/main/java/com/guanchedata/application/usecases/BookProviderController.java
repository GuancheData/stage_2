package com.guanchedata.application.usecases;

import com.google.gson.Gson;
import com.guanchedata.infrastructure.adapters.*;
import com.guanchedata.infrastructure.ports.PathGenerator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class BookProviderController {
    private final BookStorageDate storageDate;
    private final BookDownloadLog bookDownloadLog;
    private static final Gson gson = new Gson();

    public BookProviderController(BookStorageDate storageDate, BookDownloadLog bookDownloadLog) {
        this.storageDate = storageDate;
        this.bookDownloadLog = bookDownloadLog;
    }

    public static void main(String[] args){
        PathGenerator pathGenerator = new DateTimePathGenerator(Paths.get(args[0]));
        GutenbergBookContentSeparator separator = new GutenbergBookContentSeparator();
        BookStorageDate storageDate = new BookStorageDate(pathGenerator, separator);
        BookDownloadLog bookDownloadLog = new BookDownloadLog(args[1]);
        BookProviderController controller = new BookProviderController(storageDate, bookDownloadLog);

        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";}).start(7001);

        app.post("/ingest/{book_id}",controller::ingestBook);
        app.get("/ingest/status/{book_id}", controller::status);
        app.get("/ingest/list", controller::listAllBooks);
    }

    private void status(Context ctx) throws IOException {
        int bookId = Integer.parseInt(ctx.pathParam("book_id"));
        boolean isBookAvailable = bookDownloadLog.isDownloaded(bookId);

        if (isBookAvailable){
            Map<String, Object> response = Map.of(
                    "book_id", bookId,
                    "status", "available"
            );

            ctx.result(gson.toJson(response));
        } else {
            Map<String, Object> response = Map.of(
                    "book_id", bookId,
                    "status", "not_available"
            );

            ctx.result(gson.toJson(response));
        }
    }

    private void listAllBooks(Context ctx) throws IOException {
        List<Integer> downloadedBooks = bookDownloadLog.getAllDownloaded();
        Map<String, Object> response = Map.of(
                "count", downloadedBooks.size(),
                "books", downloadedBooks
        );
        ctx.result(gson.toJson(response));
    }

    private void ingestBook(Context ctx) {
        int bookId = Integer.parseInt(ctx.pathParam("book_id"));
        try{

            if (bookDownloadLog.isDownloaded(bookId)) {
                Map<String, Object> responseAPI = Map.of(
                        "book_id", bookId,
                        "status", "already_downloaded",
                        "message", "Book already exists in datalake"
                );
                ctx.result(gson.toJson(responseAPI));
                return;
            }


            GutenbergConnection connection = new GutenbergConnection();
            GutenbergFetch fetch = new GutenbergFetch();
            String response = fetch.fetchBook(connection.createConnection(bookId));
            Path savedPath = storageDate.save(bookId, response);
            bookDownloadLog.registerDownload(bookId);

            Map<String, Object> responseAPI = Map.of(
                    "book_id", bookId,
                    "status", "downloaded",
                    "path", savedPath.toString()
            );

            ctx.result(gson.toJson(responseAPI));
        } catch (Exception e) {
            Map<String, Object> responseError = Map.of(
                    "book_id", bookId,
                    "status", "error",
                    "message", e.getMessage()
            );

            ctx.result(gson.toJson(responseError));
        }
    }
}
