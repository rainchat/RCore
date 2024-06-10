package com.rainchat.rlib.storage.database.config;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConfig {
    Connection getConnection() throws SQLException;
    String getTypeSyntax(String type, int length);
}
