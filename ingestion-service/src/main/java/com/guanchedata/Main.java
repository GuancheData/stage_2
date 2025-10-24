package com.guanchedata;

import com.guanchedata.infrastructure.adapters.BookStorageDate;
import com.guanchedata.infrastructure.adapters.GutenbergConnection;
import com.guanchedata.infrastructure.adapters.GutenbergFetch;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        GutenbergConnection connection = new GutenbergConnection();
        GutenbergFetch fetch = new GutenbergFetch();
        BookStorageDate storageDate = new BookStorageDate(args[0]);

        try {
            String response = fetch.fetchBook(connection.createConnection(1));
            storageDate.save(1, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
