package com.guanchedata.inverted_index.mongodb;

import com.guanchedata.inverted_index.InvertedIndex;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.*;
import org.bson.Document;
import com.guanchedata.inverted_index.stopwords.StopwordsLoader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MongoDB implements InvertedIndex {

    private String datalakePath;
    private String stopwordsPath;
    private String databaseName;
    private String collectionName;
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private Map<String, Set<String>> stopwordsCache = new HashMap<>();

    public MongoDB(String datalakePath, String stopwordsPath, String databaseName, String collectionName) {
        this.datalakePath = datalakePath;
        this.stopwordsPath = stopwordsPath;
        this.databaseName = databaseName;
        this.collectionName = collectionName;
        this.client = MongoClients.create("mongodb://localhost:27017");
        this.database = client.getDatabase(this.databaseName);
        this.collection = database.getCollection(this.collectionName);
        this.collection.createIndex(new Document("word", 1), new IndexOptions().unique(true));
    }

    public void saveIndexForBook(int bookId, Map<String, List<Integer>> positionMap) {
        List<WriteModel<Document>> operation = new ArrayList<>();
        String bookIdStr = String.valueOf(bookId);

        for (Map.Entry<String, List<Integer>> entry : positionMap.entrySet()) {
            String word = entry.getKey();
            List<Integer> positions = entry.getValue();
            Document data = new Document("frecuency", positions.size())
                    .append("position", positions);

            operation.add(new UpdateOneModel<>(
                    Filters.eq("word", word),
                    Updates.set("documents." + bookIdStr, data),
                    new UpdateOptions().upsert(true)
            ));

            if (operation.size() >= 5000){
                collection.bulkWrite(operation, new BulkWriteOptions().ordered(false));
                operation.clear();
            }

        }
        if (!operation.isEmpty()) {
            collection.bulkWrite(operation, new BulkWriteOptions().ordered(false));
        }

    }

    public void buildIndexForBooks(Integer bookId, Map<String, String> languageReferences) {
        Path route = Paths.get(this.datalakePath);
        long initTime = System.nanoTime();

        try (Stream<Path> files = Files.walk(route)) {
            files.filter(path -> path.getFileName().toString().matches("\\d+_.*body\\.txt"))
                    .forEach(f -> {
                        String fileName = f.getFileName().toString();
                        Matcher matcher = Pattern.compile("^(\\d+)_").matcher(fileName);
                        if (!matcher.find()) {
                            return;
                        }

                        int filebookId = Integer.parseInt(matcher.group(1));
                        String bookIdStr = String.valueOf(filebookId);

                        if (!bookId.equals(filebookId) || !languageReferences.containsKey(bookIdStr)) {
                            return;
                        }

                        String language = languageReferences.get(bookIdStr).toLowerCase();
                        System.out.printf("[INVERTED INDEX] Indexing book %d (%s)...%n", filebookId, language);

                        Set<String> stopWords = StopwordsLoader.loadStopwords(this.stopwordsPath, stopwordsCache, language);
                        if (stopWords.isEmpty()) {
                            System.out.println("No se cargaron stopwords para el idioma: " + language);
                            return;
                        }

                        Map<String, List<Integer>> positionDict = new HashMap<>();
                        int wordPosition = 0;

                        try (BufferedReader br = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                String[] tokens = line.split("[^\\p{Alpha}]+");
                                for (String token : tokens) {
                                    if (token.isEmpty()) continue;
                                    String word = token.toLowerCase();
                                    if (!stopWords.contains(word)) {
                                        positionDict.computeIfAbsent(word, k -> new ArrayList<>())
                                                .add(wordPosition);
                                    }
                                    wordPosition++;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        saveIndexForBook(filebookId, positionDict);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        long finalTime = System.nanoTime();
        double seconds = (finalTime - initTime) / 1_000_000_000.0;
        System.out.printf("[INVERTED INDEX] Total indexing time: %.2f segundos%n", seconds);
    }


}
