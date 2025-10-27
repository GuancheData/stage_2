package com.guanchedata.benchmark;

import com.guanchedata.metadata.storage.sqlite.MetadataSQLiteDB;
import com.guanchedata.metadata.parser.MetadataParser;
import org.openjdk.jmh.annotations.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@Fork(value=1, jvmArgs = {"-Xmx4G"})
@State(Scope.Thread)
public class MetadataDatabaseInsertionBenchmark {

    //ruta datalake
    @Param({})
    private String datalakePath;

    //ruta metadata.db
    @Param({})
    private String metadataPath;



    private MetadataSQLiteDB metadataDB;

    @Setup(Level.Trial)
    public void setup() {
        MetadataParser parser = new MetadataParser(datalakePath);
        metadataDB = new MetadataSQLiteDB(parser, metadataPath);
    }

    @Benchmark
    public void benchmarkSaveMetadata() {
        try {
            Files.deleteIfExists(Paths.get(metadataPath));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        for(int i=1; i<=5; i++) {
            metadataDB.saveMetadata(i);
        }
    }
}
