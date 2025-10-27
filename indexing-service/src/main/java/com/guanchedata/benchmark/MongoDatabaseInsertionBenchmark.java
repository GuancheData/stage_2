package com.guanchedata.benchmark;

import com.guanchedata.inverted_index.mongodb.MongoDBInvertedIndex;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@Fork(value=1, jvmArgs = {"-Xmx4G"})
@State(Scope.Thread)
public class MongoDatabaseInsertionBenchmark {

    //ruta datalake
    @Param({})
    private String datalakePath;

    //ruta stopwords
    @Param({})
    private String stopwordsPath;

    //db name
    @Param({})
    private String dbName;

    //collection
    @Param({})
    private String dbCollection;

    private MongoDBInvertedIndex mongoDBInvertedIndex;

    private Map<String, String> languageReferences;

    @Setup(Level.Trial)
    public void setup() {
        mongoDBInvertedIndex = new MongoDBInvertedIndex(datalakePath, stopwordsPath, dbName, dbCollection);

        languageReferences = new HashMap<>();
    }

    @Benchmark
    public void benchmarkBuildIndexForBooks() {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = database.getCollection(dbCollection);
            collection.deleteMany(new Document());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        for(int i=1; i<=5; i++){
            languageReferences.put(String.valueOf(i), "en");
            mongoDBInvertedIndex.buildIndexForBooks(i, languageReferences);
        }
    }
}
