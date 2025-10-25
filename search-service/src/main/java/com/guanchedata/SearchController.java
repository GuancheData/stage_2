package com.guanchedata;

import com.guanchedata.SearchService;
import io.javalin.http.Context;
import java.util.*;

public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    public void getSearch(Context ctx) {
        String query = ctx.queryParam("word");
        String author = ctx.queryParam("author");
        String language = ctx.queryParam("language");
        String yearParam = ctx.queryParam("year");

        if (query == null || query.isEmpty()) {
            ctx.status(400).json(Map.of("error", "Missing required parameter 'word'"));
            return;
        }

        Map<String, Object> filters = new HashMap<>();
        if (author != null && !author.isEmpty()) filters.put("author", author);
        if (language != null && !language.isEmpty()) filters.put("language", language);
        if (yearParam != null && !yearParam.isEmpty()) {
            try {
                filters.put("year", Integer.parseInt(yearParam));
            } catch (NumberFormatException e) {
                ctx.status(400).json(Map.of("error", "Invalid year format"));
                return;
            }
        }

        List<Map<String, Object>> results = searchService.search(query, filters);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("query", query);
        response.put("filters", filters);
        response.put("count", results.size());
        response.put("results", results);

        ctx.json(response);
    }
}
