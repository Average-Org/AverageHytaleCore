import com.google.common.flogger.AbstractLogger;
import com.google.common.flogger.FluentLogger;
import models.TestTable;
import models.db.DatabaseService;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class DatabaseServiceTests {

    private final AbstractLogger<?> logger;
    private final String dbFilePath = "test.db";

    public DatabaseServiceTests() {
        logger = FluentLogger.forEnclosingClass();
    }

    private void deleteDatabase() {
        var file = new java.io.File(dbFilePath);
        file.delete();
    }

    private DatabaseService createDatabase() throws SQLException {
        return new DatabaseService("jdbc:sqlite:" + dbFilePath, logger);
    }

    @Test
    void canCreateDatabase() throws SQLException {
        deleteDatabase();
        createDatabase();
    }

    @Test
    void canCreateTable() throws SQLException {
        deleteDatabase();
        var db = createDatabase();
        db.addTable(TestTable.class);

        // is the file more than 0 bytes?
        var file = new java.io.File(dbFilePath);
        assert file.length() > 0;
    }

    @Test
    void canInsertRow() throws SQLException {
        deleteDatabase();
        var db = createDatabase();
        db.addTable(TestTable.class);

        db.getTypedTable(TestTable.class).create(new TestTable("test"));
        db.save();

        assert db.getTypedTable(TestTable.class).countOf() == 1;

        var file = new java.io.File(dbFilePath);
        assert file.length() > 0;
    }

}
