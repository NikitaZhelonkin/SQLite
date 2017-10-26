package ru.nikitazhelonkin.test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import ru.nikitazhelonkin.sqlite.SQLiteHelper;

/**
 * Created by nikita on 03.04.17.
 */

public class DatabaseHelper extends SQLiteHelper {

    private static final String DATABASE_NAME = "test";

    private static final int VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, VERSION);
        setLogEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db, DogOwnerTable.INSTANCE);
        createTable(db, DogTable.INSTANCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db, DogOwnerTable.INSTANCE);
        dropTable(db, DogTable.INSTANCE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
