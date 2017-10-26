package ru.nikitazhelonkin.sqlite;

import android.content.ContentValues;

/**
 * Created by nikita on 03.02.17.
 */

public class ContentValuesImpl implements IContentValues {

    private ContentValues mValues;

    public ContentValuesImpl(){
        mValues = new ContentValues();
    }

    @Override
    public void put(String key, String value) {
        mValues.put(key, value);
    }

    @Override
    public void put(String key, Byte value) {
        mValues.put(key, value);
    }

    @Override
    public void put(String key, Short value) {
        mValues.put(key, value);
    }

    @Override
    public void put(String key, Integer value) {
        mValues.put(key, value);
    }

    @Override
    public void put(String key, Long value) {
        mValues.put(key, value);
    }

    @Override
    public void put(String key, Float value) {
        mValues.put(key, value);
    }

    @Override
    public void put(String key, Double value) {
        mValues.put(key, value);
    }

    @Override
    public void put(String key, Boolean value) {
        mValues.put(key, value);
    }

    @Override
    public void put(String key, byte[] value) {
        mValues.put(key, value);
    }

    public ContentValues getValues() {
        return mValues;
    }
}
