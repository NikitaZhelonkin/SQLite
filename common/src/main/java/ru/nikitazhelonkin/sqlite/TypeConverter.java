package ru.nikitazhelonkin.sqlite;

import android.support.annotation.Nullable;

public interface TypeConverter<T,R> {

    @Nullable
    T serialize(@Nullable R r);

    @Nullable
    R deserialize(@Nullable T t);

}
