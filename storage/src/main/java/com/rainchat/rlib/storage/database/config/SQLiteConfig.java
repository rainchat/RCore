package com.rainchat.rlib.storage.database.config;

import com.rainchat.rlib.storage.database.config.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteConfig implements DatabaseConfig {
    private HikariDataSource dataSource;

    public SQLiteConfig(String filename) {
        String url = "jdbc:sqlite:" + filename;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Override
    public String getTypeSyntax(String type, int length) {
        switch (type) {
            case "int":
                return "INTEGER";
            case "string":
                return "VARCHAR(" + length + ")";
            case "double":
                return "REAL";
            case "boolean":
                return "BOOLEAN";
            case "blob":
                return "BLOB";
            case "autoIncrement":
                return "AUTOINCREMENT";
            default:
                return "BLOB";
        }
    }
}
