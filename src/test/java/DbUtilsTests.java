import com.google.common.flogger.AbstractLogger;
import com.google.common.flogger.FluentLogger;
import models.db.DatabaseService;
import org.junit.jupiter.api.Test;
import util.DbUtils;

import java.sql.SQLException;

public class DbUtilsTests {
    private final AbstractLogger<?> logger;
    private final String dbFilePath = "test.db";

    public DbUtilsTests() {
        logger = FluentLogger.forEnclosingClass();
    }

    private void deleteDatabase() {
        var file = new java.io.File(dbFilePath);
        file.delete();
    }

    private DatabaseService createDatabase() throws SQLException {
        return DbUtils.initializeDatabase("test", logger);
    }

    @Test
    public void canCreateDatabase() throws SQLException {
        deleteDatabase();
        createDatabase();
    }

    @Test
    public void canCreateDatabaseWithTable() throws SQLException {
        deleteDatabase();
        var db = DbUtils.initializeDatabase("test", logger, models.TestTable.class);
    }

    @Test
    public void canCreateDatabaseEvenIfDuplicateTablesAreAdded() throws SQLException {
        deleteDatabase();
        var db = DbUtils.initializeDatabase("test", logger, models.TestTable.class, models.TestTable.class);
    }
}
