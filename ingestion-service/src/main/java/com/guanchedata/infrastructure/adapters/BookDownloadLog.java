package com.guanchedata.infrastructure.adapters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class BookDownloadLog {
    private final String downloadedBookFile;

    public BookDownloadLog(String downloadedBookFile) {
        this.downloadedBookFile = downloadedBookFile;
    }

    public void registerDownload(int bookId) throws IOException {
        Set<Integer> books = loadBooks();
        books.add(bookId);
        saveBooks(bookId);
    }

    public boolean isDownloaded(int bookId) throws IOException {
        return loadBooks().contains(bookId);
    }

    public List<Integer> getAllDownloaded() throws IOException {
        return new ArrayList<>(loadBooks());
    }

    private Set<Integer> loadBooks() throws IOException {
        if (!Files.exists(Paths.get(downloadedBookFile))) {
            return new HashSet<>();
        }
        List<String> lines = Files.readAllLines(Paths.get(downloadedBookFile));
        Set<Integer> books = new HashSet<>();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                books.add(Integer.parseInt(line.trim()));
            }
        }
        return books;
    }


    private void saveBooks(int bookId) throws IOException {
        Path parent = Paths.get(downloadedBookFile).getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        String line = bookId + System.lineSeparator();

        Files.writeString(Paths.get(downloadedBookFile), line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
