package com.guanchedata.benchmark;

import com.guanchedata.inverted_index.mongodb.BookIndexProcessor;
import com.guanchedata.inverted_index.stopwords.StopwordsLoader;
import org.openjdk.jmh.annotations.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@Fork(value=1, jvmArgs = {"-Xmx4G"})
@State(Scope.Thread)
public class TokenizationBenchmark {

    //ruta de un libro solo
    @Param({})
    private String oneBookPath;

    // ruta stopwords
    @Param({})
    private String stopwordsPath;

    private BookIndexProcessor processor;
    private Path file;
    private Set<String> stopwords;

    @Setup(Level.Trial)
    public void setup() {
        processor = new BookIndexProcessor();
        file = Paths.get(oneBookPath);
        stopwords = StopwordsLoader.loadStopwords(stopwordsPath, new java.util.HashMap<>(), "en");
    }

    @Benchmark
    public void benchmarkExtractWordPositions() {
        processor.extractWordPositions(file, stopwords);
    }
}
