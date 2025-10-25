package com.guanchedata.metadata.parser;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class MetadataParser {
    private final String datalakePath;
    private final Pattern pattern;

    public MetadataParser(String datalakePath) {
        this.datalakePath = datalakePath;
        this.pattern = Pattern.compile(
                "Title:\\s*(.+)|Author:\\s*(.+)|Language:\\s*(.+)|Release date:.*?(\\d{4})");
    }

    public Map<String, String> parseMetadata(int bookId) {
        Path route = Paths.get(datalakePath);
        String target = bookId + "_header.txt";
        Map<String, String> metadata = new HashMap<>();

        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(route, bookId + "_header.txt")) {
                for (Path entry : stream) {
                    System.out.println("[INDEX] Indexing book " + bookId + ".");
                    try (BufferedReader reader = Files.newBufferedReader(entry)) {
                        metadata = extractMetadata(reader);
                        if (!metadata.isEmpty()) {
                            System.out.println("[INDEX] Book " + bookId + " successfully indexed.\n");
                        } else {
                            System.out.println("[INDEX] No metadata found in book " + bookId + ".");
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[INDEX] Error reading metadata for book " + bookId + ": " + e.getMessage());
        }
        return metadata;
    }

    private Map<String, String> extractMetadata(BufferedReader reader) throws IOException {
        Map<String, String> metadata = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String[] keys = {"Title", "Author", "Language", "Year"};
                for (int i = 0; i < keys.length; i++) {
                    if (matcher.group(i + 1) != null) {
                        metadata.put(keys[i], matcher.group(i + 1).trim());
                        break;
                    }
                }
            }
        }
        return metadata;
    }
}