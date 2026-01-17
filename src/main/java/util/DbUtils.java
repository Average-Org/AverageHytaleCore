package util;

import models.db.DatabaseService;

public class DbUtils {

    public static DatabaseService initializeDatabase(String databaseName) {
        var path = PathUtils.getPathForConfig(databaseName + ".db");
        String databaseUrl = "jdbc:sqlite:" + path.toAbsolutePath().toString();
        return new DatabaseService(databaseUrl);
    }
}
