package ru.nikitazhelonkin.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG  = SQLiteHelper.class.getSimpleName();

    public interface OnChangeListener<T> {
        void onChange(Table<T> table);
    }

    private List<Table> mTables;

    private List<Pair<String, OnChangeListener>> mListeners;

    private Handler mMainHandler;

    private boolean mLogEnabled = false;

    public SQLiteHelper(Context context, String name, int version) {
        super(context, name, new SQLiteCursorFactory(), version);
        mTables = new ArrayList<>();
        mListeners = new CopyOnWriteArrayList<>();
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public void setLogEnabled(boolean logEnabled) {
        mLogEnabled = logEnabled;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(new SQLiteDatabaseImpl(db));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //do inheritance
    }

    protected void registerTable(Table t) {
        if (!mTables.contains(t)) {
            mTables.add(t);
        }
    }

    public <T> void registerListener(Table<T> table, OnChangeListener<T> listener) {
        mListeners.add(new Pair<String, OnChangeListener>(table.getName(), listener));
    }

    public <T> void unregisterListener(Table<T> table, OnChangeListener<T> listener) {
        int index = -1;
        for (int i = 0; i < mListeners.size(); i++) {
            if (mListeners.get(i).second == listener) {
                index = i;
            }
        }
        if (index != -1) {
            mListeners.remove(index);
        }
    }

    public <T> List<T> rawQuery(@NonNull Table<T> table, String sql, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        return mapToList(table, cursor);
    }

    @NonNull
    public <T> List<T> query(@NonNull Table<T> table) {
        return query(table, Selection.create());
    }

    @NonNull
    public <T> List<T> query(@NonNull Table<T> table, Selection selection) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(table.getName(), selection.columns(), selection.selection(), selection.args(),
                selection.groupBy(), selection.having(), selection.orderBy(), selection.limit());
        return mapToList(table, cursor);
    }

    @Nullable
    public <T> T queryFirst(@NonNull Table<T> table) {
        List<T> list = query(table, Selection.create());
        return list.size() > 0 ? list.get(0) : null;
    }

    @Nullable
    public <T> T queryFirst(@NonNull Table<T> table, Selection selection) {
        List<T> list = query(table, selection);
        return list.size() > 0 ? list.get(0) : null;
    }

    public <T> long insertOrReplace(@NonNull Table<T> table, @NonNull T object) {
        return insert(table, object, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public <T> void insertOrReplace(@NonNull Table<T> table, @NonNull Iterable<T> objects) {
        insert(table, objects, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public <T> long insert(@NonNull Table<T> table, @NonNull T object) {
        return insert(table, object, SQLiteDatabase.CONFLICT_NONE);
    }

    public <T> void insert(@NonNull Table<T> table, @NonNull Iterable<T> objects) {
        insert(table, objects, SQLiteDatabase.CONFLICT_NONE);
    }

    public <T> long insert(@NonNull Table<T> table, @NonNull T object, int conflictAlgorithm) {
        long id = insertInternal(table, object, conflictAlgorithm);
        notifyTableChangedIfNeeded(table, id != -1);
        return id;
    }

    public <T> void insert(@NonNull Table<T> table, @NonNull Iterable<T> objects, int conflictAlgorithm) {
        insertInternal(table, objects, conflictAlgorithm);
        notifyTableChanged(table);
    }

    public <T> int update(@NonNull Table<T> table, @NonNull Selection selection, @NonNull T object) {
        return update(table, selection, object, null);
    }

    public <T> int update(@NonNull Table<T> table, @NonNull Selection selection, @NonNull T object, String[] excludeUpdate) {
        ContentValuesImpl valuesImpl = new ContentValuesImpl();
        table.bindValues(valuesImpl, object);
        ContentValues values = excludeColumnsFromCV(valuesImpl.getValues(), excludeUpdate);
        int rowsAffected = updateInternal(table, selection, values);
        notifyTableChangedIfNeeded(table, rowsAffected > 0);
        return rowsAffected;
    }

    public <T> int delete(@NonNull Table<T> table) {
        return delete(table, Selection.create());
    }

    public <T> int delete(@NonNull Table<T> table, @NonNull Selection selection) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = db.delete(table.getName(), selection.selection(), selection.args());
        notifyTableChangedIfNeeded(table, rowsAffected > 0);
        return rowsAffected;
    }

    public void deleteAll() {
        for (Table t : mTables) {
            delete(t);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void notifyTableChanged(final Table<T> table) {
        for (final Pair<String, OnChangeListener> pair : mListeners) {
            if (pair.first.equals(table.getName())) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        pair.second.onChange(table);
                    }
                });
            }
        }
    }

    public <T> void notifyTableChangedIfNeeded(final Table<T> table, boolean notify) {
        if(notify){
            notifyTableChanged(table);
        }
    }

    private <T> void insertInternal(@NonNull Table<T> table, @NonNull Iterable<T> objects, int conflictAlgorithm) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (T obj : objects) {
                insertInternal(table, obj, conflictAlgorithm);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private <T> long insertInternal(@NonNull Table<T> table, @NonNull T object, int conflictAlgorithm) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValuesImpl values = new ContentValuesImpl();
            table.bindValues(values, object);
            long id = db.insertWithOnConflict(table.getName(), null, values.getValues(),
                    conflictAlgorithm);
            if (id == -1) {
                return id;
            }
            return id;
        } catch (SQLiteException e) {
            log(e.getMessage());
            return -1;
        }
    }

    private  <T> int updateInternal(@NonNull Table<T> table, @NonNull Selection selection, @NonNull ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(table.getName(), values, selection.selection(), selection.args());
    }

    private void createTables(SQLiteDatabaseImpl db) {
        for (Table t : mTables) {
            t.create(db);
        }
    }

    private <T> List<T> mapToList(@NonNull Table<T> table, Cursor cursor){
        List<T> list = new ArrayList<>();

        if (cursor == null) {
            return list;
        }
        try {
            list = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                list.add(table.fromCursor((ISQLiteCursor) cursor));
            }
            return list;
        } finally {
            cursor.close();
        }
    }

    private ContentValues excludeColumnsFromCV(ContentValues cv, String[] excludeColumns) {
        if (excludeColumns == null) {
            return cv;
        }
        Iterator<String> iterator = cv.keySet().iterator();
        for (; iterator.hasNext(); ) {
            String key = iterator.next();
            if (contains(excludeColumns, key)) {
                iterator.remove();
            }
        }
        return cv;
    }

    private boolean contains(@NonNull String[] array, @NonNull String string) {
        for (String s : array) {
            if (s.equals(string)) {
                return true;
            }
        }
        return false;
    }

    private void log(String message) {
        if (mLogEnabled) {
            Log.e(TAG, message);
        }
    }

}
