package ru.nikitazhelonkin.sqlite.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import ru.nikitazhelonkin.sqlite.annotation.Reference;

/**
 * Created by nikita on 03.02.17.
 */

class ColumnSpec {

    private final String mFieldName;

    private final TypeMirror mFieldType;

    private final String mColumnName;

    private final String mColumnType;

    private final boolean mIsPrimaryKey;

    private final boolean mIsAutoincrement;

    private final boolean mUnique;

    private final boolean mNotNull;

    private final Reference mReference;

    private final TypeMirror mConverterClazz;

    ColumnSpec(Element field, String columnName, String columnType, boolean primaryKey, boolean autoincrement, boolean unique, boolean notNull, Reference reference, TypeMirror converterClazz) {
        mFieldName = field.getSimpleName().toString();
        mFieldType = field.asType();
        mColumnName = columnName;
        mColumnType = columnType;
        mIsPrimaryKey = primaryKey;
        mIsAutoincrement = autoincrement;
        mUnique = unique;
        mNotNull = notNull;
        mReference = reference;
        mConverterClazz = converterClazz;
    }

    public String getFieldName() {
        return mFieldName;
    }

    public TypeMirror getFieldType() {
        return mFieldType;
    }

    public String getColumnName() {
        return mColumnName;
    }

    public String getColumnType() {
        return mColumnType;
    }

    public boolean isPrimaryKey() {
        return mIsPrimaryKey;
    }

    public boolean isAutoincrement() {
        return mIsAutoincrement;
    }

    public boolean isUnique() {
        return mUnique;
    }

    public boolean isNotNull() {
        return mNotNull;
    }

    public Reference getReference(){
        return mReference;
    }

    public TypeMirror getConverterClazz() {
        return mConverterClazz;
    }
}
