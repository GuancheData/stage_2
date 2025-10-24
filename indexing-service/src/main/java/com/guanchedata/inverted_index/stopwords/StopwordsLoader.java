package com.guanchedata.inverted_index.stopwords;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StopwordsLoader {

    public static Set<String> loadStopwords(String stopwordsPath, Map<String, Set<String>> stopwordsCache, String language) {
        if (stopwordsCache.containsKey(language)) {
            return stopwordsCache.get(language);
        }
        Set<String> stopWords = new HashSet<>();
        Path stopwordsFilePath = Paths.get(stopwordsPath, language);
        if (Files.exists(stopwordsFilePath)) {
            try (BufferedReader br = Files.newBufferedReader(stopwordsFilePath)) {
                String line;
                while ((line = br.readLine()) != null) {
                    stopWords.add(line.trim().toLowerCase());
                }
            } catch (IOException e) {

            }
        }
        stopwordsCache.put(language, stopWords);
        return stopWords;
    }

}
