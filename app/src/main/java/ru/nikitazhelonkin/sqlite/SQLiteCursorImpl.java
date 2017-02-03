package ru.nikitazhelonkin.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteQuery;

/**
 * Created by nikita on 03.02.17.
 */

public class SQLiteCursorImpl extends SQLiteCursor implements ISQLiteCursor {

    public SQLiteCursorImpl(SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
        super(driver, editTable, query);
    }
}
