package com.guanchedata;
import io.javalin.Javalin;
import io.javalin.http.Context;
import com.google.gson.Gson;
import java.util.*;

public class Main {
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";}).start(7000);

        // third endpoint
        app.get("/index/status", ctx -> {
            Map<String, Object> status = Map.of(
                    "service", "example-service",
                    "status", "running"
            );
            ctx.result(gson.toJson(status));
        });
        app.get("/data", Main::handleData);

    }

    private static void handleData(Context ctx) {
        String filter = ctx.queryParam("filter").toLowerCase();

        List<Map<String, Object>> items = List.of(
                Map.of("id", 1, "name", "Item A"),
                Map.of("id", 2, "name", "Item B"),
                Map.of("id", 3, "name", "Item C")
        );

        List<Map<String, Object>> filteredItems = items.stream()
                .filter(item -> ((String) item.get("name")).toLowerCase().contains(filter))
                .toList();

        Map<String, Object> response = Map.of(
                "filter", filter,
                "count", filteredItems.size(),
                "items", filteredItems
        );
        ctx.result(gson.toJson(response));
    }
}
