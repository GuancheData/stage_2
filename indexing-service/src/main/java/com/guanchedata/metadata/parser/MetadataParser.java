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

    /**
     * Parse metadata from a specific book ID.
     * @param bookId The book ID to parse.
     * @return Map<String, String> with metadata (keys: Title, Author, Language)
     */
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

    /**
     * Extracts metadata from a file.
     * @param reader BufferedReader for the file.
     * @return Map<String, String> with metadata.
     */
    private Map<String, String> extractMetadata(BufferedReader reader) throws IOException {
        Map<String, String> metadata = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    metadata.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        return metadata;
    }
}