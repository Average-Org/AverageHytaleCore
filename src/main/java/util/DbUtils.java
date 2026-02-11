package util;

import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import models.db.DatabaseService;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Static utility class that facilitates the creation of a database service. Sitting as a wrapper for ORMLite
 */
public final class DbUtils {
    private DbUtils() {
    }

    static {
        // set ormlite logger to warning level
        Logger.setGlobalLogLevel(Level.WARNING);
    }

    /**
     * Initializes a database service with the given name.
     *
     * @param databaseName The name of the database to initialize
     * @return The database service
     */
    public static DatabaseService initializeDatabase(String databaseName) {
        return initializeDatabase(databaseName, new Class<?>[0]);
    }

    public static DatabaseService initializeDatabase(String databaseName, Class<?>... tables) {
        Objects.requireNonNull(databaseName, "Database name cannot be null");

        if (databaseName.isBlank()) {
            throw new IllegalArgumentException("Database name cannot be blank");
        }

        var path = PathUtils.getPathForConfig(databaseName + ".db");
        ensureDirectoryExists(path);

        String databaseUrl = "jdbc:sqlite:" + path.toAbsolutePath();
        try {
            var databaseService = new DatabaseService(databaseUrl);
            if (tables.length > 0) {
                databaseService.addTables(tables);
            }

            return databaseService;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void ensureDirectoryExists(Path path) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create database directory", e);
        }
    }
}
