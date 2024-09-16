package ru.rainchat.rlib.storage.database.dao;

import ru.rainchat.rlib.storage.database.annotation.Column;
import ru.rainchat.rlib.storage.database.annotation.ForeignCollectionField;
import ru.rainchat.rlib.storage.database.annotation.Table;
import ru.rainchat.rlib.storage.database.config.DatabaseConfig;
import ru.rainchat.rlib.storage.database.utils.SerializationUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDao<T, K> {
    protected DatabaseConfig config;
    private final Class<T> type;

    public BaseDao(DatabaseConfig config, Class<T> type) {
        this.config = config;
        this.type = type;
    }

    private String getTableName(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        return table != null ? table.name() : clazz.getSimpleName().toLowerCase();
    }

    private String getTableName() {
        return getTableName(type);
    }

    private Field getPrimaryKeyField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.primaryKey()) {
                field.setAccessible(true); // Ensure the primary key field is accessible
                return field;
            }
        }
        throw new RuntimeException("No primary key field found on class " + clazz.getName());
    }

    private Field getPrimaryKeyField() {
        return getPrimaryKeyField(type);
    }

    protected T mapRow(ResultSet rs) {
        try {
            T instance = type.getDeclaredConstructor().newInstance();
            for (Field field : type.getDeclaredFields()) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    field.setAccessible(true);
                    Object value = column.isBlob() ? SerializationUtils.deserialize(rs.getBytes(column.name())) : rs.getObject(column.name());
                    field.set(instance, value);
                }
            }
            loadForeignCollections(instance);
            return instance;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to map row to instance of " + type.getName(), e);
        }
    }

    private void loadForeignCollections(T instance) {
        for (Field field : type.getDeclaredFields()) {
            ForeignCollectionField foreignCollectionField = field.getAnnotation(ForeignCollectionField.class);
            if (foreignCollectionField != null) {
                field.setAccessible(true);
                try {
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> collectionType = (Class<?>) listType.getActualTypeArguments()[0];
                    BaseDao<?, ?> foreignDao = DaoFactory.getDao(collectionType, config);

                    // Get primary key value of the current instance
                    Field primaryKeyField = getPrimaryKeyField();
                    primaryKeyField.setAccessible(true);  // Ensure accessibility
                    Object primaryKeyValue = primaryKeyField.get(instance);

                    // Find all foreign collections by foreign key
                    List<?> foreignCollection = foreignDao.findAllByForeignKey(foreignCollectionField.mappedBy(), primaryKeyValue);
                    field.set(instance, foreignCollection);
                } catch (IllegalAccessException | ClassCastException e) {
                    throw new RuntimeException("Failed to load foreign collection for field " + field.getName(), e);
                }
            }
        }
    }

    public void createTable() {
        try {
            createSingleTable(type);
            for (Field field : type.getDeclaredFields()) {
                ForeignCollectionField foreignCollectionField = field.getAnnotation(ForeignCollectionField.class);
                if (foreignCollectionField != null) {
                    createSingleTable((Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table for class " + type.getName(), e);
        }
    }

    private void createSingleTable(Class<?> clazz) throws SQLException {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(getTableName(clazz)).append(" (");
        boolean columnsAdded = false;

        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                if (columnsAdded) query.append(", ");
                query.append(column.name()).append(" ").append(getTypeSyntax(field, column));
                if (column.primaryKey()) query.append(" PRIMARY KEY");
                if (column.autoIncrement()) query.append(" ").append(config.getTypeSyntax("autoIncrement", 0));
                columnsAdded = true;
            }
        }

        if (!columnsAdded) throw new SQLException("No valid columns found for table creation: " + getTableName(clazz));
        query.append(")");

        try (Connection conn = config.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(query.toString());
        }
    }

    private String getTypeSyntax(Field field, Column column) {
        String typeName;
        switch (field.getType().getSimpleName()) {
            case "int", "Integer" -> typeName = "int";
            case "String" -> typeName = "string";
            case "double", "Double" -> typeName = "double";
            case "boolean", "Boolean" -> typeName = "boolean";
            default -> {
                if (column.isBlob()) {
                    typeName = "blob";
                } else {
                    throw new UnsupportedOperationException("Unsupported field type: " + field.getType().getName());
                }
            }
        }
        return config.getTypeSyntax(typeName, column.length());
    }

    public void save(T entity) {
        try {
            Field primaryKeyField = getPrimaryKeyField();
            primaryKeyField.setAccessible(true);
            Object primaryKeyValue = primaryKeyField.get(entity);

            boolean isInsert = isInsert(primaryKeyField, primaryKeyValue);
            List<Field> validFields = getValidFields(isInsert);

            if (validFields.isEmpty()) {
                throw new SQLException("No columns to insert or update");
            }

            StringBuilder query = new StringBuilder();
            if (isInsert) {
                buildInsertQuery(query, validFields);
            } else {
                buildUpdateQuery(query, validFields, primaryKeyField);
            }

            try (Connection conn = config.getConnection(); PreparedStatement stmt = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
                int index = 1;
                for (Field field : validFields) {
                    field.setAccessible(true);
                    Column column = field.getAnnotation(Column.class);
                    Object value = field.get(entity);
                    if (column.isBlob()) {
                        stmt.setBytes(index++, SerializationUtils.serialize(value));
                    } else {
                        stmt.setObject(index++, value);
                    }
                }

                // Set the primary key value for the WHERE clause
                if (!isInsert) stmt.setObject(index, primaryKeyValue);

                int affectedRows = stmt.executeUpdate();

                if (primaryKeyValue == null) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            primaryKeyField.set(entity, generatedKeys.getObject(1));
                        }
                    }
                }
            }

            deleteForeignCollections(entity);
            saveForeignCollections(entity);
        } catch (SQLException | IllegalAccessException | IOException e) {
            throw new RuntimeException("Failed to save entity of class " + type.getName(), e);
        }
    }

    private void deleteForeignCollections(T entity) {
        for (Field field : type.getDeclaredFields()) {
            ForeignCollectionField foreignCollectionField = field.getAnnotation(ForeignCollectionField.class);
            if (foreignCollectionField != null) {
                field.setAccessible(true); // Ensure the field is accessible
                try {
                    List<?> currentCollection = (List<?>) field.get(entity);
                    if (currentCollection != null) {
                        ParameterizedType listType = (ParameterizedType) field.getGenericType();
                        Class<?> collectionType = (Class<?>) listType.getActualTypeArguments()[0];
                        BaseDao<?, ?> foreignDao = DaoFactory.getDao(collectionType, config);

                        // Get primary key value of the current instance
                        Field primaryKeyField = getPrimaryKeyField();
                        primaryKeyField.setAccessible(true);  // Ensure accessibility
                        Object primaryKeyValue = primaryKeyField.get(entity);

                        // Find all foreign collections by foreign key
                        List<?> foreignCollection = foreignDao.findAllByForeignKey(foreignCollectionField.mappedBy(), primaryKeyValue);
                        field.set(entity, foreignCollection);

                        // Delete orphaned foreign collection items
                        List<Object> idsToKeep = new ArrayList<>();
                        for (Object item : currentCollection) {
                            Field itemPrimaryKeyField = getPrimaryKeyField(item.getClass());
                            itemPrimaryKeyField.setAccessible(true); // Ensure the primary key field of the collection item is accessible
                            idsToKeep.add(itemPrimaryKeyField.get(item));
                        }

                        if (idsToKeep.isEmpty()) {
                            String deleteAllQuery = "DELETE FROM " + getTableName(collectionType) + " WHERE " + foreignCollectionField.mappedBy() + " = ?";
                            try (Connection conn = config.getConnection(); PreparedStatement stmt = conn.prepareStatement(deleteAllQuery)) {
                                stmt.setObject(1, primaryKeyValue);
                                stmt.executeUpdate();
                            }
                        } else {
                            StringBuilder query = new StringBuilder("DELETE FROM ")
                                    .append(getTableName(collectionType))
                                    .append(" WHERE ").append(foreignCollectionField.mappedBy()).append(" = ? AND ")
                                    .append(getPrimaryKeyField(collectionType).getAnnotation(Column.class).name())
                                    .append(" NOT IN (");
                            for (Object id : idsToKeep) {
                                query.append("?,");
                            }
                            query.setLength(query.length() - 1);
                            query.append(")");

                            try (Connection conn = config.getConnection(); PreparedStatement stmt = conn.prepareStatement(query.toString())) {
                                stmt.setObject(1, primaryKeyValue);
                                for (int i = 0; i < idsToKeep.size(); i++) {
                                    stmt.setObject(i + 2, idsToKeep.get(i));
                                }
                                stmt.executeUpdate();
                            }
                        }
                    }
                } catch (IllegalAccessException | SQLException e) {
                    throw new RuntimeException("Failed to delete foreign collections for entity of class " + type.getName(), e);
                }
            }
        }
    }

    private boolean isInsert(Field primaryKeyField, Object primaryKeyValue) {
        Column primaryKeyColumn = primaryKeyField.getAnnotation(Column.class);
        if (primaryKeyColumn.autoIncrement() && (primaryKeyField.getType() == int.class || primaryKeyField.getType() == Integer.class)) {
            return primaryKeyValue == null || (int) primaryKeyValue == 0;
        } else {
            return primaryKeyValue == null || findById((K) primaryKeyValue) == null;
        }
    }

    private List<Field> getValidFields(boolean isInsert) {
        List<Field> validFields = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && (!column.autoIncrement() && (!column.primaryKey() || isInsert))) validFields.add(field);
        }
        return validFields;
    }

    private void buildInsertQuery(StringBuilder query, List<Field> validFields) {
        query.append("INSERT INTO ").append(getTableName()).append(" (");
        validFields.forEach(field -> query.append(field.getAnnotation(Column.class).name()).append(", "));
        query.setLength(query.length() - 2);
        query.append(") VALUES (");
        validFields.forEach(field -> query.append("?, "));
        query.setLength(query.length() - 2);
        query.append(")");
    }

    private void buildUpdateQuery(StringBuilder query, List<Field> validFields, Field primaryKeyField) {
        query.append("UPDATE ").append(getTableName()).append(" SET ");
        validFields.forEach(field -> query.append(field.getAnnotation(Column.class).name()).append(" = ?, "));
        query.setLength(query.length() - 2);
        query.append(" WHERE ").append(primaryKeyField.getAnnotation(Column.class).name()).append(" = ?");
    }

    private void saveForeignCollections(T entity) {
        for (Field field : type.getDeclaredFields()) {
            ForeignCollectionField foreignCollectionField = field.getAnnotation(ForeignCollectionField.class);
            if (foreignCollectionField != null) {
                field.setAccessible(true);
                try {
                    List<?> foreignCollection = (List<?>) field.get(entity);
                    if (foreignCollection != null) {
                        for (Object foreignItem : foreignCollection) {
                            Field foreignKeyField = foreignItem.getClass().getDeclaredField(foreignCollectionField.mappedBy());
                            foreignKeyField.setAccessible(true);
                            foreignKeyField.set(foreignItem, getPrimaryKeyField().get(entity));
                            BaseDao<Object, Object> foreignDao = (BaseDao<Object, Object>) DaoFactory.getDao(foreignItem.getClass(), config);
                            foreignDao.save(foreignItem);
                        }
                    }
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException("Failed to save foreign collections for entity of class " + type.getName(), e);
                }
            }
        }
    }

    public T findById(K id) {
        String query = "SELECT * FROM " + getTableName() + " WHERE " + getPrimaryKeyField().getAnnotation(Column.class).name() + " = ?";
        try (Connection conn = config.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find entity by ID for class " + type.getName(), e);
        }
    }

    public List<T> findAll() {
        String query = "SELECT * FROM " + getTableName();
        try (Connection conn = config.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            List<T> results = new ArrayList<>();
            while (rs.next()) results.add(mapRow(rs));
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all entities for class " + type.getName(), e);
        }
    }

    public List<T> findAllByForeignKey(String foreignKeyColumn, Object foreignKeyValue) {
        String query = "SELECT * FROM " + getTableName() + " WHERE " + foreignKeyColumn + " = ?";
        try (Connection conn = config.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, foreignKeyValue);
            try (ResultSet rs = stmt.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) results.add(mapRow(rs));
                return results;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all entities by foreign key for class " + type.getName(), e);
        }
    }

    public void executeUpdateQuery(String query, Object... params) {
        try (Connection conn = config.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            // Set parameters for the query
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            int affectedRows = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute update query: " + query, e);
        }
    }

    public void deleteById(K id) {
        try {
            deleteForeignCollections(findById(id));
            String query = "DELETE FROM " + getTableName() + " WHERE " + getPrimaryKeyField().getAnnotation(Column.class).name() + " = ?";
            try (Connection conn = config.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setObject(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete entity by ID for class " + type.getName(), e);
        }
    }

}
