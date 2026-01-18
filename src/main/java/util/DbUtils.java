package util;

import models.db.DatabaseService;

import java.sql.SQLException;

public class DbUtils {

    public static DatabaseService initializeDatabase(String databaseName) throws SQLException {
        var path = PathUtils.getPathForConfig(databaseName + ".db");
        String databaseUrl = "jdbc:sqlite:" + path.toAbsolutePath().toString();
        return new DatabaseService(databaseUrl);
    }
}
