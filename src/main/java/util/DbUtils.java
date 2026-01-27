package util;

import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import models.db.DatabaseService;

import java.sql.SQLException;

public class DbUtils {

    static {
        Logger.setGlobalLogLevel(Level.WARNING);
    }

    public static DatabaseService initializeDatabase(String databaseName) throws SQLException {
        var path = PathUtils.getPathForConfig(databaseName + ".db");
        String databaseUrl = "jdbc:sqlite:" + path.toAbsolutePath().toString();
        return new DatabaseService(databaseUrl);
    }
}
