package ru.rainchat.rlib.storage.database.dao;

import ru.rainchat.rlib.storage.database.config.DatabaseConfig;

import java.util.HashMap;
import java.util.Map;

public class DaoFactory {
    private static final Map<Class<?>, BaseDao<?, ?>> daos = new HashMap<>();

    public static <T, K> BaseDao<T, K> getDao(Class<T> entityType, DatabaseConfig config) {
        if (!daos.containsKey(entityType)) {
            synchronized (daos) {
                if (!daos.containsKey(entityType)) {
                    BaseDao<T, K> dao = new GenericDao<>(config, entityType);
                    daos.put(entityType, dao);
                }
            }
        }
        return (BaseDao<T, K>) daos.get(entityType);
    }

    private static class GenericDao<T, K> extends BaseDao<T, K> {
        public GenericDao(DatabaseConfig config, Class<T> type) {
            super(config, type);
        }
    }
}
