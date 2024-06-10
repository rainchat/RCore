package com.rainchat.rlib.storage.database.config;

import com.rainchat.rlib.storage.database.config.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConfig implements DatabaseConfig {
    private HikariDataSource dataSource;

    public MySQLConfig(String url, String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(50); // Максимальное количество соединений в пуле
        config.setMinimumIdle(10); // Минимальное количество свободных соединений в пуле
        config.setConnectionTimeout(30000); // Тайм-аут на получение соединения в миллисекундах
        config.setIdleTimeout(600000); // Тайм-аут простоя соединения в миллисекундах
        config.setMaxLifetime(1800000); // Максимальное время жизни соединения в миллисекундах

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
                return "AUTO_INCREMENT";
            default:
                return "BLOB";
        }
    }
}
