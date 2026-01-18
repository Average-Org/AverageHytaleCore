package models.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class DatabaseService {
    String databaseUrl;

    public ConnectionSource connectionSource;
    public HashMap<Class<?>, Dao<?, Long>> daoRepository = new HashMap<>();

    public DatabaseService(String databaseUrl) throws SQLException {
        this.databaseUrl = databaseUrl;
        connectionSource = new JdbcConnectionSource(databaseUrl);
    }

    public void addTable(Class<?> clazz) throws SQLException {
        Dao<?, Long> dao = (Dao<?, Long>) DaoManager.createDao(connectionSource, clazz);
        TableUtils.createTableIfNotExists(connectionSource, clazz);

        daoRepository.put(clazz, dao);
    }

    public <T> Dao<T, Long> getTable(Class<T> clazz) {
        return (Dao<T, Long>) daoRepository.get(clazz);
    }
}
