package com.rainchat.rlib.storage.database.dao;

import com.rainchat.rlib.storage.database.annotation.Column;
import com.rainchat.rlib.storage.database.annotation.Table;
import com.rainchat.rlib.storage.database.config.DatabaseConfig;
import com.rainchat.rlib.storage.database.utils.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDao<T, K> {
    protected DatabaseConfig config;
    private Class<T> type;

    public BaseDao(DatabaseConfig config, Class<T> type) {
        this.config = config;
        this.type = type;
    }

    private String getTableName() {
        Table table = type.getAnnotation(Table.class);
        if (table == null) {
            throw new RuntimeException("No Table annotation found on class " + type.getName());
        }
        return table.name();
    }

    private Field getPrimaryKeyField() {
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.primaryKey()) {
                return field;
            }
        }
        throw new RuntimeException("No primary key field found on class " + type.getName());
    }

    protected T mapRow(ResultSet rs) throws SQLException {
        try {
            T instance = type.getDeclaredConstructor().newInstance();
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    field.setAccessible(true);
                    Object value;
                    if (column.isBlob()) {
                        value = SerializationUtils.deserialize(rs.getBytes(column.name()));
                    } else {
                        value = rs.getObject(column.name());
                    }
                    field.set(instance, value);
                }
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to map row to instance of " + type.getName(), e);
        }
    }

    public void createTable() throws SQLException {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        query.append(getTableName()).append(" (");

        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                String typeSyntax;
                if (field.getType() == int.class || field.getType() == Integer.class) {
                    typeSyntax = config.getTypeSyntax("int", column.length());
                } else if (field.getType() == String.class) {
                    typeSyntax = config.getTypeSyntax("string", column.length());
                } else if (field.getType() == double.class || field.getType() == Double.class) {
                    typeSyntax = config.getTypeSyntax("double", column.length());
                } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                    typeSyntax = config.getTypeSyntax("boolean", column.length());
                } else if (column.isBlob()) {
                    typeSyntax = config.getTypeSyntax("blob", column.length());
                } else {
                    throw new UnsupportedOperationException("Unsupported field type: " + field.getType().getName() + ".");
                }
                query.append(column.name()).append(" ").append(typeSyntax);
                if (column.primaryKey()) {
                    query.append(" PRIMARY KEY");
                }
                if (column.autoIncrement()) {
                    query.append(" ").append(config.getTypeSyntax("autoIncrement", 0));
                }
                query.append(", ");
            }
        }
        query.setLength(query.length() - 2); // Remove last comma and space
        query.append(")");

        try (Connection conn = config.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query.toString());
        }
    }

    public void save(T entity) throws SQLException, IllegalAccessException {
        Field primaryKeyField = getPrimaryKeyField();
        primaryKeyField.setAccessible(true);
        Object primaryKeyValue = primaryKeyField.get(entity);

        boolean isInsert = false;

        // Check if the primary key field is auto-increment and its type is int
        Column primaryKeyColumn = primaryKeyField.getAnnotation(Column.class);
        if (primaryKeyColumn.autoIncrement() && (primaryKeyField.getType() == int.class || primaryKeyField.getType() == Integer.class)) {
            if (primaryKeyValue == null || (int) primaryKeyValue == 0) {
                isInsert = true;
            }
        } else {
            isInsert = (primaryKeyValue == null || findById((K) primaryKeyValue) == null);
        }

        StringBuilder query = new StringBuilder();
        Field[] fields = type.getDeclaredFields();

        if (isInsert) {
            query.append("INSERT INTO ").append(getTableName()).append(" (");
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column != null && !column.autoIncrement()) {
                    query.append(column.name()).append(", ");
                }
            }
            query.setLength(query.length() - 2); // Remove last comma and space
            query.append(") VALUES (");
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column != null && !column.autoIncrement()) {
                    query.append("?, ");
                }
            }
            query.setLength(query.length() - 2); // Remove last comma and space
            query.append(")");
        } else {
            query.append("UPDATE ").append(getTableName()).append(" SET ");
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column != null && !column.primaryKey()) {
                    query.append(column.name()).append(" = ?, ");
                }
            }
            query.setLength(query.length() - 2); // Remove last comma and space
            query.append(" WHERE ").append(primaryKeyField.getAnnotation(Column.class).name()).append(" = ?");
        }

        System.out.println("Generated SQL: " + query.toString());

        try (Connection conn = config.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
            int index = 1;
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column != null && !column.autoIncrement() && (!column.primaryKey() || isInsert)) {
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    if (column.isBlob()) {
                        try {
                            stmt.setBytes(index++, SerializationUtils.serialize(value));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        stmt.setObject(index++, value);
                    }
                }
            }
            if (!isInsert) {
                stmt.setObject(index, primaryKeyValue);
            }

            stmt.executeUpdate();

            if (isInsert) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        for (Field field : fields) {
                            Column column = field.getAnnotation(Column.class);
                            if (column != null && column.primaryKey() && column.autoIncrement()) {
                                field.setAccessible(true);
                                field.set(entity, generatedKeys.getObject(1));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public List<T> findAll() throws SQLException {
        List<T> results = new ArrayList<>();
        String query = "SELECT * FROM " + getTableName();

        try (Connection conn = config.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }

        return results;
    }

    public T findById(K id) throws SQLException {
        T result = null;
        String query = "SELECT * FROM " + getTableName() + " WHERE " + getPrimaryKeyField().getAnnotation(Column.class).name() + " = ?";

        try (Connection conn = config.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result = mapRow(rs);
                }
            }
        }

        return result;
    }

    public void delete(K id) throws SQLException {
        String query = "DELETE FROM " + getTableName() + " WHERE " + getPrimaryKeyField().getAnnotation(Column.class).name() + " = ?";

        try (Connection conn = config.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, id);
            stmt.executeUpdate();
        }
    }

    public List<T> executeQuery(String sql) throws SQLException {
        List<T> results = new ArrayList<>();
        try (Connection conn = config.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    public int executeUpdate(String sql) throws SQLException {
        try (Connection conn = config.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return stmt.executeUpdate();
        }
    }

}

