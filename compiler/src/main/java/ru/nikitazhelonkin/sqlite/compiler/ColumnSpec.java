package ru.nikitazhelonkin.sqlite.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

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

    ColumnSpec(Element field, String columnName, String columnType, boolean primaryKey, boolean autoincrement) {
        mFieldName = field.getSimpleName().toString();
        mFieldType = field.asType();
        mColumnName = columnName;
        mColumnType = columnType;
        mIsPrimaryKey = primaryKey;
        mIsAutoincrement = autoincrement;
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
}
