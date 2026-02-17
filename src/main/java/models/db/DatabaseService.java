package models.db;

import com.hypixel.hytale.logger.HytaleLogger;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Database service allowing plugin developers to easily manage tables based on ORMLite-annotated classes.
 */
public class DatabaseService implements AutoCloseable {
    public final ConnectionSource connectionSource;
    public final Map<Class<?>, Dao<?, ?>> daoRepository = new ConcurrentHashMap<>();
    private final String dbFilePath;

    public DatabaseService(String databaseUrl) throws SQLException {
        this.dbFilePath = databaseUrl.replace("jdbc:sqlite:", "");

        try {
            Class.forName("io.roastedroot.sqlite4j.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Could not find Sqlite4j JDBC driver", e);
        }

        try {
            DatabaseType dbType = new Sqlite4JDatabaseType();
            connectionSource = new JdbcConnectionSource(databaseUrl, dbType);

            HytaleLogger.forEnclosingClass().at(java.util.logging.Level.INFO).log("Connected to database at " + databaseUrl);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a table to the database from an ORMLite-annotated class.
     *
     * @param clazz The class to add a table for
     */
    public void addTable(Class<?> clazz, boolean saveImmediately) {
        try {
            Dao<?, ?> dao = DaoManager.createDao(connectionSource, clazz);
            TableUtils.createTableIfNotExists(connectionSource, clazz);
            daoRepository.put(clazz, dao);
            HytaleLogger.forEnclosingClass().at(java.util.logging.Level.INFO).log("Registered table for class " + clazz.getName());

            if(saveImmediately) save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addTable(Class<?> clazz) {
        addTable(clazz, true);
    }

    public void addTables(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            try {
                addTable(clazz, false);
            } catch (Exception e) {
                HytaleLogger.forEnclosingClass().at(java.util.logging.Level.SEVERE)
                        .log("Could not register table for class " + clazz.getName() + ": " + e.getMessage());
            }
        }

        save();
    }

    /**
     * Gets a DAO of a specific ID type for a given class.
     *
     * @param clazz The class to get a DAO for
     * @param <T>   The class type
     * @param <ID>  The ID type
     * @return The DAO
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
     *
     * @param clazz The class to get a DAO for
     * @param <T>   The class type
     * @return The DAO
     */
    @SuppressWarnings("unchecked")
    public <T> Dao<T, Long> getTable(Class<T> clazz) {
        Dao<?, ?> dao = daoRepository.get(clazz);
        if (dao == null) {
            throw new IllegalArgumentException("No DAO found for " + clazz.getName());
        }
        return (Dao<T, Long>) dao;
    }

    /**
     * Dumps the in-memory database to the disk file.
     * Call this periodically or after major changes.
     */
    public synchronized void save() {
        // 1. Resolve paths
        Path finalDbPath = Paths.get(dbFilePath).toAbsolutePath();
        Path backupPath = Paths.get(dbFilePath + ".bak").toAbsolutePath();

        try {
            // Ensure folder exists
            if (Files.notExists(backupPath.getParent())) {
                Files.createDirectories(backupPath.getParent());
            }

            // Get the ORMLite connection
            DatabaseConnection conn = connectionSource.getReadWriteConnection(null);
            try {
                // BYPASS ORMLITE: Get the raw java.sql.Connection
                java.sql.Connection jdbcConn = (java.sql.Connection) conn.getUnderlyingConnection();

                // Run the backup command using standard JDBC
                try (java.sql.Statement stmt = jdbcConn.createStatement()) {
                    stmt.executeUpdate("backup to \"" + backupPath.toString() + "\"");
                }
            } finally {
                connectionSource.releaseConnection(conn);
            }

            // 2. Move the backup file to the real location
            Files.move(backupPath, finalDbPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);

            HytaleLogger.forEnclosingClass().at(Level.INFO).log("Saved database to " + finalDbPath);

        } catch (Exception e) {
            HytaleLogger.forEnclosingClass().at(Level.SEVERE).log("Could not save database to " + finalDbPath + ": " + e);
        }
    }


    @Override
    public void close() throws Exception {
        try {
            save();
            connectionSource.close();
        } catch (Exception e) {
            throw new IOException("Could not close connection source", e);
        }
    }
}
