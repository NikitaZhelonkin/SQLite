package ru.nikitazhelonkin.test;

import android.support.annotation.Nullable;

import ru.nikitazhelonkin.sqlite.ArrayUtils;
import ru.nikitazhelonkin.sqlite.TypeConverter;

public class StringArrayConverter implements TypeConverter<String, String[]> {

    @Nullable
    @Override
    public String serialize(@Nullable String[] strings) {
        return ArrayUtils.join(strings, ", ");
    }

    @Nullable
    @Override
    public String[] deserialize(@Nullable String s) {
        return s == null ? null : s.split(", ");
    }
}
