package models.db;

import com.j256.ormlite.dao.Dao;

import java.util.HashMap;
import java.util.List;

public class DatabaseService {
    String databaseUrl;

    public HashMap<Class<?>, Dao<?, ?>> daoRepository = new HashMap<>();

    public DatabaseService(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public void addTable(Dao<?, ?> dao) {
        daoRepository.put(dao.getDataClass(), dao);
    }

    public <T> Dao<T, ?> getTable(Class<T> clazz) {
        if (daoRepository.containsKey(clazz)) {
            return (Dao<T, ?>) daoRepository.get(clazz);
        } else {
            Dao<>
        }
    }
}
