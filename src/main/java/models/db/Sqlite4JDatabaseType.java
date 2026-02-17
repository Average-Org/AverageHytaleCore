package models.db;

import com.j256.ormlite.jdbc.db.SqliteDatabaseType;

public class Sqlite4JDatabaseType extends SqliteDatabaseType {

    private final static String DRIVER_CLASS_NAME = "io.roastedroot.sqlite4j.JDBC";

    @Override
    protected String[] getDriverClassNames() {
        return new String[]{DRIVER_CLASS_NAME};
    }
}

