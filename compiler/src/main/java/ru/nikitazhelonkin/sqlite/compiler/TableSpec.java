package ru.nikitazhelonkin.sqlite.compiler;

import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.TypeElement;

import ru.nikitazhelonkin.sqlite.annotation.SQLiteObject;

/**
 * Created by nikita on 03.02.17.
 */

class TableSpec {

    private final List<ColumnSpec> mColumns = new ArrayList<>();

    private TypeElement mOriginElement;

    private String mTableName;

    private List<IndexSpec> mIndices = new ArrayList<>();

    private SQLiteObject.Deserialization mDeserialization = SQLiteObject.Deserialization.METHOD;

    List<ColumnSpec> getColumns() {
        return mColumns;
    }

    static TableSpec getOrCreate(Map<TypeElement, TableSpec> specs, TypeElement element) {
        TableSpec spec = specs.get(element);
        if (spec == null) {
            spec = new TableSpec();
            specs.put(element, spec);
        }
        return spec;
    }

    void addColumn(ColumnSpec columnSpec) {
        mColumns.add(columnSpec);
    }

    TypeElement getOriginElement() {
        return mOriginElement;
    }

    void setOriginElement(TypeElement originElement) {
        mOriginElement = originElement;
    }

    String getTableName() {
        return mTableName;
    }

    void setTableName(String tableName) {
        mTableName = tableName;
    }

    public void setDeserialization(SQLiteObject.Deserialization deserialization) {
        mDeserialization = deserialization;
    }

    public SQLiteObject.Deserialization getDeserialization() {
        return mDeserialization;
    }

    void setIndices(List<IndexSpec> indices) {
        mIndices = indices;
    }

    List<IndexSpec> getIndices() {
        return mIndices;
    }

    ClassName getClassName() {
        final ClassName originClassName = ClassName.get(mOriginElement);
        return ClassName.get(originClassName.packageName(), originClassName.simpleName() + "Table");
    }
}
