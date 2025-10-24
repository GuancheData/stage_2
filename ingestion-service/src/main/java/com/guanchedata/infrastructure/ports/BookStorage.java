package com.guanchedata.infrastructure.ports;

import java.io.IOException;

public interface BookStorage {
    void save(int book_id, String content) throws IOException;
}
