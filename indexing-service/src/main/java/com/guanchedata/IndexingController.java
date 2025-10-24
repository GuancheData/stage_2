package com.guanchedata;

import com.google.gson.Gson;
import io.javalin.http.Context;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class IndexingController {

    BookIndexer bookIndexer;
    IndexRebuilder indexRebuilder;
    String indexedBooksDbPath;
    int books_indexed;
    Instant last_update;
    private Gson gson;

    public IndexingController(BookIndexer bookIndexer, IndexRebuilder indexRebuilder, String indexedBooksDbPath) {
        this.bookIndexer = bookIndexer;
        this.indexRebuilder = indexRebuilder;
        this.indexedBooksDbPath = indexedBooksDbPath;
        this.books_indexed = retrieveNumberOfIndexedBooks(indexedBooksDbPath);
        this.last_update = null;
        this.gson = new Gson();
    }

    public void indexBook(Context ctx){
        int bookId = Integer.parseInt(ctx.pathParam("book_id"));
        this.bookIndexer.execute(bookId);
        this.books_indexed += 1;
        this.last_update = Instant.now();
        Map<String, Object> response = Map.of("book_id", bookId, "index", "updated");
        ctx.result(gson.toJson(response));
    }

    public void rebuildIndex(Context ctx){
        Instant start = Instant.now();
        this.indexRebuilder.execute();
        this.books_indexed = retrieveNumberOfIndexedBooks(this.indexedBooksDbPath);
        Instant end = Instant.now();
        double elapsedSeconds = Duration.between(start, end).toMillis() / 1000.0;
        String elapsedTime = String.format("%.1fs", elapsedSeconds);
        this.last_update = end;
        Map<String, Object> response = Map.of("books_processed", this.books_indexed, "elapsed_time", elapsedTime);
        ctx.result(gson.toJson(response));
    }

    public void retrieveIndexingStatus(){
        // TO DO
    }

    public int retrieveNumberOfIndexedBooks(String indexedBooksDbPath){
        // TO DO
        return 0;
    }

}
