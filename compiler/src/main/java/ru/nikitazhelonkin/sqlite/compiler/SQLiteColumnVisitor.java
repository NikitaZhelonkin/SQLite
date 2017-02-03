package ru.nikitazhelonkin.sqlite.compiler;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import ru.nikitazhelonkin.sqlite.annotation.SQLiteColumn;

/**
 * Created by nikita on 03.02.17.
 */

public class SQLiteColumnVisitor extends Visitor {

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
            columnName = ColumnSpec.columnName(e.getSimpleName().toString());
        }

        spec.addColumn(new ColumnSpec(e, columnName, annotation.type(), annotation.primaryKey()));
        return super.visitVariable(e, specs);
    }



}
