package ru.nikitazhelonkin.sqlite;

/**
 * Created by nikita on 03.10.16.
 */

public class Column {

    private String mType;
    private String mName;
    private boolean mIsPrimaryKey;
    private boolean mIsAutoincrement;
    private boolean mIsNotNull;

    private Column(String name, String type) {
        this.mName = name;
        this.mType = type;
    }

    public static Column integer(String name) {
        return new Column(name, "INTEGER");
    }

    public static Column text(String name) {
        return new Column(name, "TEXT");
    }

    public static Column real(String name) {
        return new Column(name, "REAL");
    }

    public static Column blob(String name) {
        return new Column(name, "BLOB");
    }

    public static Column create(String name, String type) {
        return new Column(name, type);
    }

    public Column primaryKey() {
        mIsPrimaryKey = true;
        return this;
    }

    public Column autoincrement() {
        mIsAutoincrement = true;
        return this;
    }

    public Column notNull() {
        mIsNotNull = true;
        return this;
    }

    public String toSql() {
        StringBuilder builder = new StringBuilder(mName).append(" ").append(mType);
        if (mIsNotNull) builder.append(" NOT NULL");
        if (mIsPrimaryKey) builder.append(" PRIMARY KEY");
        if (mIsAutoincrement) builder.append(" AUTOINCREMENT");
        return builder.toString();
    }

}