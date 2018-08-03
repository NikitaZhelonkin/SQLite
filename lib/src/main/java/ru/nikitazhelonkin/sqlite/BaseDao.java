package ru.nikitazhelonkin.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDao<T> {

    private Table<T> mTable;
    private SQLiteDatabaseProvider mSQLiteDatabaseProvider;

    public BaseDao(SQLiteDatabaseProvider sqLiteDatabaseProvider, Table<T> table){
        mSQLiteDatabaseProvider = sqLiteDatabaseProvider;
        mTable = table;
    }

    public List<T> rawQuery(String sql, String... selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        return mapToList(cursor);
    }

    @NonNull
    public List<T> query() {
        return query(Selection.create());
    }

    @NonNull
    public List<T> query(Selection selection) {
        Cursor cursor = getReadableDatabase().query(mTable.getName(), selection.columns(), selection.selection(), selection.args(),
                selection.groupBy(), selection.having(), selection.orderBy(), selection.limit());
        return mapToList(cursor);
    }

    @Nullable
    public T queryFirst() {
        List<T> list = query(Selection.create());
        return list.size() > 0 ? list.get(0) : null;
    }

    @Nullable
    public T queryFirst(Selection selection) {
        List<T> list = query(selection);
        return list.size() > 0 ? list.get(0) : null;
    }

    public int queryCount() {
        return queryCount(null);
    }

    public int queryCount(@Nullable Selection selection) {
        Cursor cursor = getReadableDatabase().query(mTable.getName(), new String[]{"COUNT(*)"},
                selection == null ? null : selection.selection(),
                selection == null ? null : selection.args(),
                null, null, null);
        if (cursor == null) {
            return 0;
        }
        try {
            int count = 0;
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            return count;
        } finally {
            cursor.close();
        }
    }

    public long insertOrReplace(@NonNull T object) {
        return insert(object, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void insertOrReplace(@NonNull Iterable<T> objects) {
        insert(objects, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public long insert(@NonNull T object) {
        return insert(object, SQLiteDatabase.CONFLICT_NONE);
    }

    public void insert(@NonNull Iterable<T> objects) {
        insert(objects, SQLiteDatabase.CONFLICT_NONE);
    }

    public void insert(@NonNull Iterable<T> objects, int conflictAlgorithm) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (T obj : objects) {
                insert( obj, conflictAlgorithm);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public long insert(@NonNull T object, int conflictAlgorithm) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValuesImpl values = new ContentValuesImpl();
            mTable.bindValues(values, object);
            long id = db.insertWithOnConflict(mTable.getName(), null, values.getValues(),
                    conflictAlgorithm);
            if (id == -1) {
                return id;
            }
            return id;
        } catch (SQLiteException e) {
            return -1;
        }
    }

    public void insertOrUpdate(Selection updateSelection, @NonNull T object) {
        long id = insert(object);
        if (id == -1) {
            update(updateSelection, object);
        }
    }

    public void insertOrUpdate(final @NonNull Iterable<T> objects, final SelectionProvider<T> selectionProvider) {
        inTransaction(new Runnable() {
            @Override
            public void run() {
                for (T object : objects) {
                    insertOrUpdate(selectionProvider.selection(object), object);
                }
            }
        });
    }

    public int update(@NonNull Selection selection, @NonNull T object) {
        ContentValuesImpl valuesImpl = new ContentValuesImpl();
        mTable.bindValues(valuesImpl, object);
        ContentValues values = valuesImpl.getValues();
        return update(selection, values);
    }


    public int update(@NonNull Selection selection, @NonNull ContentValues values) {
        return getWritableDatabase().update(mTable.getName(), values, selection.selection(), selection.args());
    }

    public int delete() {
        return delete(Selection.create());
    }

    public int delete(@NonNull Selection selection) {
        return getWritableDatabase().delete(mTable.getName(), selection.selection(), selection.args());
    }

    private List<T> mapToList(Cursor cursor) {
        List<T> list = new ArrayList<>();

        if (cursor == null) {
            return list;
        }
        try {
            list = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                list.add(mTable.fromCursor((ISQLiteCursor) cursor));
            }
            return list;
        } finally {
            cursor.close();
        }
    }

    public void inTransaction(Runnable transaction) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            transaction.run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private SQLiteDatabase getReadableDatabase(){
        return mSQLiteDatabaseProvider.getReadableDatabase();
    }

    private SQLiteDatabase getWritableDatabase(){
        return mSQLiteDatabaseProvider.getWritableDatabase();
    }

    public interface SelectionProvider<T> {
        Selection selection(T object);
    }

}
