package ru.nikitazhelonkin.sqlite;

import android.database.sqlite.SQLiteDatabase;

public interface SQLiteDatabaseProvider {

    SQLiteDatabase getWritableDatabase();

    SQLiteDatabase getReadableDatabase();
}
