package com.guanchedata;

import com.guanchedata.infrastructure.adapters.GutenbergBookContentSeparator;
import com.guanchedata.infrastructure.adapters.GutenbergConnection;
import com.guanchedata.infrastructure.adapters.GutenbergFetch;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        GutenbergConnection connection = new GutenbergConnection();
        GutenbergFetch fetch = new GutenbergFetch();
        GutenbergBookContentSeparator bookContentSeparator = new GutenbergBookContentSeparator();

        try {
            String response = fetch.fetchBook(connection.createConnection(1));
            String[] content = bookContentSeparator.separateContent(response);
            System.out.println(content[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
