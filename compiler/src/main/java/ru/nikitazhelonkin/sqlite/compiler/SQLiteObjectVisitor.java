package ru.nikitazhelonkin.sqlite.compiler;


import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import ru.nikitazhelonkin.sqlite.annotation.SQLiteObject;

/**
 * Created by nikita on 03.02.17.
 */

public class SQLiteObjectVisitor extends Visitor {


    public SQLiteObjectVisitor(ProcessingEnvironment env) {
        super(env);
    }

    @Override
    public Void visitType(TypeElement e, Map<TypeElement, TableSpec> specs) {
        SQLiteObject sqLiteObject = e.getAnnotation(SQLiteObject.class);
        TableSpec spec = TableSpec.getOrCreate(specs, e);
        spec.setTableName(sqLiteObject.value());
        spec.setOriginElement(e);
        return super.visitType(e, specs);
    }
}
