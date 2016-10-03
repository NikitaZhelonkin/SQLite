package ru.nikitazhelonkin.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by nikita on 02.02.16.
 */
public interface Table<T> {

    String getName();

    void onCreate(SQLiteDatabase db);

    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    ContentValues toContentValues(T t);

    T fromCursor(Cursor cursor);

}
