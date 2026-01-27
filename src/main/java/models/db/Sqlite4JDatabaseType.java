package models.db;

import com.j256.ormlite.db.BaseSqliteDatabaseType;

public class Sqlite4JDatabaseType extends BaseSqliteDatabaseType {

    private final static String DATABASE_URL_PORTION = "sqlite";
    private final static String DRIVER_CLASS_NAME = "io.roastedroot.sqlite4j.JDBC";
    private final static String DATABASE_NAME = "SQLite";

    public Sqlite4JDatabaseType() {
    }

    @Override
    public boolean isDatabaseUrlThisType(String url, String dbTypePart) {
        return DATABASE_URL_PORTION.equals(dbTypePart);
    }

    @Override
    protected String[] getDriverClassNames() {
        return new String[] { DRIVER_CLASS_NAME };
    }

    @Override
    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    @Override
    public void appendLimitValue(StringBuilder sb, long limit, Long offset) {
        sb.append("LIMIT ");
        if (offset != null) {
            sb.append(offset).append(',');
        }
        sb.append(limit).append(' ');
    }

    @Override
    public boolean isOffsetLimitArgument() {
        return true;
    }

    @Override
    public boolean isNestedSavePointsSupported() {
        return false;
    }

    @Override
    public void appendOffsetValue(StringBuilder sb, long offset) {
        throw new IllegalStateException("Offset is part of the LIMIT in database type " + getClass());
    }

    @Override
    public boolean isLimitUpdateAtEndSupported() {
        return true;
    }

    @Override
    public boolean isLimitDeleteAtEndSupported() {
        return true;
    }
}

