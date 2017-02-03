package ru.nikitazhelonkin.sqlite;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by nikita on 03.02.17.
 */

public class SQLiteDatabaseImpl implements ISQLiteDatabase {

    private SQLiteDatabase mDb;

    public SQLiteDatabaseImpl(SQLiteDatabase db){
        mDb = db;
    }

    @Override
    public void execSQL(String sql) {
        mDb.execSQL(sql);
    }
}
