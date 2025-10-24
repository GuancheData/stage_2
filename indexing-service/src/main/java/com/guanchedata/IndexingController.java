package com.guanchedata;

import java.time.Instant;

public class IndexingController {
    BookIndexer bookIndexer;
    IndexRebuilder indexRebuilder;
    int books_indexed;
    Instant last_update;

    public IndexingController(BookIndexer bookIndexer, IndexRebuilder indexRebuilder, String indexedBooksDbPath) {
        this.bookIndexer = bookIndexer;
        this.indexRebuilder = indexRebuilder;
        this.books_indexed = retrieveNumberOfIndexedBooks(indexedBooksDbPath);

    }

    public int getIndexedBooks() {
        return books_indexed;
    }

    public void indexBook(String bookId){
        this.bookIndexer.execute(bookId);
        this.last_update = Instant.now();
    }

    public void rebuildIndex(){
        this.indexRebuilder.execute();
        this.last_update = Instant.now();
    }

    public void retrieveIndexingStatus(){
        // TO DO
    }

    public int retrieveNumberOfIndexedBooks(String indexedBooksDbPath){
        // TO DO
        return 0;
    }

}
