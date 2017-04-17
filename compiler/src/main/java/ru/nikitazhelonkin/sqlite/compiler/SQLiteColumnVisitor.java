package ru.nikitazhelonkin.sqlite.compiler;

import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import ru.nikitazhelonkin.sqlite.annotation.SQLiteColumn;

/**
 * Created by nikita on 03.02.17.
 */

class SQLiteColumnVisitor extends Visitor {

    public SQLiteColumnVisitor(ProcessingEnvironment env) {
        super(env);
    }

    @Override
    public Void visitVariable(VariableElement e, Map<TypeElement, TableSpec> specs) {
        fixFieldAccess(e);
        SQLiteColumn annotation = e.getAnnotation(SQLiteColumn.class);

        TableSpec spec = TableSpec.getOrCreate(specs, (TypeElement) e.getEnclosingElement());

        String columnName = annotation.value();
        if (columnName.isEmpty()) {
            columnName = Field.columnName(e.getSimpleName().toString());
        }

        String columnType;
        final TypeMirror fieldType = e.asType();
        if (!annotation.type().isEmpty()) {
            columnType = annotation.type();
        } else if (Field.isLong(fieldType) || Field.isInt(fieldType) || Field.isBoolean(fieldType) || Field.isShort(fieldType)) {
            columnType = SQLiteColumn.INTEGER;
        } else if (Field.isDouble(fieldType) || Field.isFloat(fieldType)) {
            columnType = SQLiteColumn.REAL;
        } else if (Field.isByteArray(fieldType)) {
            columnType = SQLiteColumn.BLOB;
        } else {
            columnType = SQLiteColumn.TEXT;
        }

        spec.addColumn(new ColumnSpec(e, columnName, columnType, annotation.primaryKey(), annotation.autoincrement()));
        return super.visitVariable(e, specs);
    }



}
