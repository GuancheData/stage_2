package com.guanchedata.sqlite;

import java.sql.*;
import java.util.*;

public class SQLiteConnector {
    private final String url;

    public SQLiteConnector(String url) {
        this.url = "jdbc:sqlite:" + url;
    }

    public List<Map<String, Object>> findMetadata(List<Integer> ids, Map<String, Object> filters) {
        List<Map<String, Object>> results = new ArrayList<>();

        if (ids.isEmpty()) return results;

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        StringBuilder sql = new StringBuilder("SELECT id, title, author, language, year FROM metadata WHERE id IN ( " + placeholders + " )" );

        List<Object> params = new ArrayList<>(ids);

        if (filters != null && !filters.isEmpty()) {
            for (String key : filters.keySet()) {
                Object value = filters.get(key);
                if (value != null) {
                    if (key.equals("author")) {
                        sql.append("AND author LIKE ?");
                        params.add("%" + value + "%");
                    } else {
                        sql.append(" AND ").append(key).append(" = ?");
                        params.add(value);
                    }
                }
            }
        }

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement statement = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("title", rs.getString("title"));
                row.put("author", rs.getString("author"));
                row.put("language", rs.getString("language"));
                row.put("year", rs.getInt("year"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}
