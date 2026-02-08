package models.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Database service allowing plugin developers to easily manage tables based on ORMLite-annotated classes.
 */
public class DatabaseService implements AutoCloseable {
    public final ConnectionSource connectionSource;
    public final Map<Class<?>, Dao<?, ?>> daoRepository = new ConcurrentHashMap<>();

    public DatabaseService(String databaseUrl) throws SQLException {
        DatabaseType dbType = new Sqlite4JDatabaseType();
        connectionSource = new JdbcConnectionSource(databaseUrl, dbType);
    }

    /**
     * Adds a table to the database from an ORMLite-annotated class.
     * @param clazz The class to add a table for
     * @throws SQLException If the table could not be created
     */
    public void addTable(Class<?> clazz) throws SQLException {
        Dao<?, ?> dao = DaoManager.createDao(connectionSource, clazz);
        TableUtils.createTableIfNotExists(connectionSource, clazz);

        daoRepository.put(clazz, dao);
    }

    /**
     * Gets a DAO of a specific ID type for a given class.
     * @param clazz The class to get a DAO for
     * @return The DAO
     * @param <T> The class type
     * @param <ID> The ID type
     */
    @SuppressWarnings("unchecked")
    public <T, ID> Dao<T, ID> getTypedTable(Class<T> clazz) {
        Dao<?, ?> dao = daoRepository.get(clazz);
        if (dao == null) {
            throw new IllegalArgumentException("No DAO registered for class: " + clazz.getName() + ". Did you call addTable()?");
        }
        return (Dao<T, ID>) dao;
    }

    /**
     * Gets a DAO for a given class, only works if the ID type is Long.
     * @param clazz The class to get a DAO for
     * @return The DAO
     * @param <T> The class type
     */
    @SuppressWarnings("unchecked")
    public <T> Dao<T, Long> getTable(Class<T> clazz) {
        Dao<?, ?> dao = daoRepository.get(clazz);
        if (dao == null) {
            throw new IllegalArgumentException("No DAO found for " + clazz.getName());
        }
        return (Dao<T, Long>) dao;
    }


    @Override
    public void close() throws Exception {
        try {
            connectionSource.close();
        } catch (Exception e) {
            throw new IOException("Could not close connection source", e);
        }
    }
}
