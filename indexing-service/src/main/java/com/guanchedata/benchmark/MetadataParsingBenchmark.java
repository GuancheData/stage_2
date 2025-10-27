package com.guanchedata.benchmark;

import com.guanchedata.metadata.parser.MetadataParser;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@Fork(value=1, jvmArgs = {"-Xmx4G"})
@State(Scope.Thread)
public class MetadataParsingBenchmark {

    //ruta datalake
    @Param({})
    private String datalakePath;

    private MetadataParser metadataParser;

    @Setup(Level.Trial)
    public void setup() {
        metadataParser = new MetadataParser(datalakePath);
    }

    @Benchmark
    public void benchmarkParseMetadata() {
        for (int i=1; i<=5; i++) {
            metadataParser.parseMetadata(i);
        }
    }
}
