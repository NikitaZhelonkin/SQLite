package ru.nikitazhelonkin.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class SQLiteHelper extends SQLiteOpenHelper implements SQLiteDatabaseProvider{

    public SQLiteHelper(Context context, String name, int version) {
        super(context, name, new SQLiteCursorFactory(), version);
    }

    protected void createTable(SQLiteDatabase db, Table t) {
        t.create(new SQLiteDatabaseImpl(db));
    }

    protected void dropTable(SQLiteDatabase db, Table t) {
        db.execSQL("DROP TABLE IF EXISTS " + t.getName());
    }

    public void executeTransaction(Runnable transaction) {
        executeTransaction(getWritableDatabase(), transaction);
    }

    public void executeTransaction(SQLiteDatabase db, Runnable transaction) {
        try {
            db.beginTransaction();
            transaction.run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
