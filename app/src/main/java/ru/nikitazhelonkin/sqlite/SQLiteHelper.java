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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class SQLiteHelper extends SQLiteOpenHelper {

    public interface OnChangeListener<T> {
        void onChange(Table<T> table);
    }

    private List<Table> mTables;

    private List<Pair<String, OnChangeListener>> mListeners;

    private Handler mMainHandler;

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mTables = new ArrayList<>();
        mListeners = new ArrayList<>();
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        upgradeTables(db, oldVersion, newVersion);
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

    @NonNull
    public <T> List<T> query(@NonNull Table<T> table) {
        return query(table, Selection.create());
    }

    @NonNull
    public <T> List<T> query(@NonNull Table<T> table, Selection selection) {
        List<T> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(table.getName(), null, selection.selection(), selection.args(),
                selection.groupBy(), selection.having(), selection.orderBy(), selection.limit());

        if (cursor == null) {
            return list;
        }
        try {
            list = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                list.add(table.fromCursor(cursor));
            }
            return list;
        } finally {
            cursor.close();
        }
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

    public <T> long insert(@NonNull Table<T> table, @NonNull T object) {
        long id = insertInternal(table, object);
        notifyTableChangedIfNeeded(table, id != -1);
        return id;
    }

    public <T> void insert(@NonNull Table<T> table, @NonNull Iterable<T> objects) {
        insertInternal(table, objects);
        notifyTableChanged(table);
    }

    public <T> int update(@NonNull Table<T> table, @NonNull Selection selection, @NonNull T object) {
        return update(table, selection, object, null);
    }

    public <T> int update(@NonNull Table<T> table, @NonNull Selection selection, @NonNull T object, String[] excludeUpdate) {
        ContentValues values = excludeColumnsFromCV(table.toContentValues(object), excludeUpdate);
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

    private <T> void insertInternal(@NonNull Table<T> table, @NonNull Iterable<T> objects) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (T obj : objects) {
                insertInternal(table, obj);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private <T> long insertInternal(@NonNull Table<T> table, @NonNull T object) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = table.toContentValues(object);
            long id = db.insertWithOnConflict(table.getName(), null, values,
                    SQLiteDatabase.CONFLICT_IGNORE);
            if (id == -1) {
                return id;
            }
            return id;
        } catch (SQLiteException e) {
            return -1;
        }
    }

    private  <T> int updateInternal(@NonNull Table<T> table, @NonNull Selection selection, @NonNull ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(table.getName(), values, selection.selection(), selection.args());
    }

    private void createTables(SQLiteDatabase db) {
        for (Table t : mTables) {
            t.onCreate(db);
        }
    }

    private void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (Table t : mTables) {
            t.onUpgrade(db, oldVersion, newVersion);
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

}