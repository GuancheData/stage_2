package com.guanchedata;

import com.guanchedata.infrastructure.adapters.*;
import com.guanchedata.infrastructure.ports.PathGenerator;
import org.jsoup.Connection;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        GutenbergConnection connection = new GutenbergConnection();
        GutenbergFetch fetch = new GutenbergFetch();
        GutenbergBookContentSeparator separator = new GutenbergBookContentSeparator();

        PathGenerator pathGenerator = new DateTimePathGenerator(Paths.get(args[0]));

        BookStorageDate storageDate = new BookStorageDate(pathGenerator, separator);

        try {
            Connection conn = connection.createConnection(1);
            String response = fetch.fetchBook(conn);
            storageDate.save(1, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
