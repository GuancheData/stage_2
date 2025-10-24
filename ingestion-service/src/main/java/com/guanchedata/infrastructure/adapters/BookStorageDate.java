package com.guanchedata.infrastructure.adapters;

import com.guanchedata.infrastructure.ports.BookStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class BookStorageDate implements BookStorage {
    private final Path datalakePath;
    private final GutenbergBookContentSeparator contentSeparator;

    public BookStorageDate(String datalakePath){
        this.datalakePath = Paths.get(datalakePath);
        this.contentSeparator = new GutenbergBookContentSeparator();
    }

    @Override
    public void save(int bookId, String content) throws IOException {
        String[] contentSeparated = contentSeparator.separateContent(content);

        String header = contentSeparated[0];
        String body = contentSeparated[1];

        Path path = this.getBookPath();
        Path headerPath = path.resolve(Paths.get(String.format("%d_header.txt", bookId)));
        Path contentPath = path.resolve(Paths.get(String.format("%d_content.txt", bookId)));

        Files.writeString(headerPath, header);
        Files.writeString(contentPath, body);
    }

    private Path getBookPath() throws IOException {
        Instant instant = Instant.now();
        ZoneId zone = ZoneId.of("GMT");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH");

        String dateDirectory = instant.atZone(zone).format(dateFormatter);
        String timeDirectory = instant.atZone(zone).format(timeFormatter);

        Path date = this.datalakePath.resolve(dateDirectory) ;
        Path time = date.resolve(timeDirectory);

        Files.createDirectories(time);

        return time;
    }
}
