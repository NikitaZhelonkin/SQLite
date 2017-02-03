package ru.nikitazhelonkin.sqlite;


/**
 * Created by nikita on 02.02.16.
 */
public interface Table<T> {

    String getName();

    void create(ISQLiteDatabase db);

    void bindValues(IContentValues values, T t);

    T fromCursor(ISQLiteCursor cursor);

}
