package com.guanchedata;

import com.guanchedata.infrastructure.adapters.GutenbergConnection;
import com.guanchedata.infrastructure.adapters.GutenbergFetch;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        GutenbergConnection connection = new GutenbergConnection();
        GutenbergFetch fetch = new GutenbergFetch();

        try {
            String response = fetch.fetchBook(connection.createConnection(1));
            System.out.println(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
