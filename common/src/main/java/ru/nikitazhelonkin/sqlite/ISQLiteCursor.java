package ru.nikitazhelonkin.sqlite;

/**
 * Created by nikita on 03.02.17.
 */

public interface ISQLiteCursor {

    long getLong(int index);

    int getInt(int index);

    double getDouble(int index);

    String getString(int index);

    byte[] getBlob(int index);

    int getColumnIndex(String name);
}
