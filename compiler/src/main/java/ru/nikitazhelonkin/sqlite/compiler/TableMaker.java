package ru.nikitazhelonkin.sqlite.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Iterator;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import ru.nikitazhelonkin.sqlite.Column;
import ru.nikitazhelonkin.sqlite.IContentValues;
import ru.nikitazhelonkin.sqlite.ISQLiteCursor;
import ru.nikitazhelonkin.sqlite.ISQLiteDatabase;
import ru.nikitazhelonkin.sqlite.Table;
import ru.nikitazhelonkin.sqlite.TableBuilder;


/**
 * Created by nikita on 03.02.17.
 */

class TableMaker {

    private final TableSpec mTableSpec;

    TableMaker(TableSpec spec) {
        mTableSpec = spec;
    }

    JavaFile brewJavaFile(){
        final TypeElement originElement = mTableSpec.getOriginElement();
        final ClassName originClass = ClassName.get(originElement);
        final ClassName tableClass = mTableSpec.getClassName();
        final TypeSpec.Builder spec = TypeSpec.classBuilder(tableClass.simpleName())
                .addOriginatingElement(originElement)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(baseTable(originClass))
                .addField(makeInstanceField())
                .addMethod(makeGetNameMethod())
                .addMethod(makeCreateMethod())
                .addMethod(makeBindValuesMethod())
                .addMethod(makeFromCursorMethod());
        addColumnsFields(spec);
        return JavaFile.builder(originClass.packageName(), spec.build())
                .addFileComment("Generated code from SQLite. Do not modify!")
                .skipJavaLangImports(true)
                .build();
    }

    private FieldSpec makeInstanceField() {
        TypeName typeName = mTableSpec.getClassName();
        return FieldSpec.builder(typeName, "INSTANCE")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T()", mTableSpec.getClassName())
                .build();
    }

    private TypeName baseTable(ClassName originClass){
        return ParameterizedTypeName.get(ClassName.get(Table.class), originClass);
    }

    private MethodSpec makeGetNameMethod() {
        return MethodSpec.methodBuilder("getName")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return $S", mTableSpec.getTableName())
                .build();
    }

    private MethodSpec makeCreateMethod() {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("create")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ISQLiteDatabase.class, "db");
        TableBuilder tableBuilder = TableBuilder.create(mTableSpec.getTableName());
        final Iterator<ColumnSpec> iterator = mTableSpec.getColumns().iterator();
        while (iterator.hasNext()) {
            final ColumnSpec columnSpec = iterator.next();
            tableBuilder.add(Column.create(
                    columnSpec.columnName,
                    columnSpec.columnDef,
                    columnSpec.isPrimaryKey));
        }
        builder.addStatement("db.execSQL($S)", tableBuilder.toSql());
        return builder.build();
    }

    private MethodSpec makeBindValuesMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("bindValues")
                .addAnnotation(Override.class)
                .addParameter(IContentValues.class, "values")
                .addParameter(ClassName.get(mTableSpec.getOriginElement()), "object")
                .addModifiers(Modifier.PUBLIC);
        for (final ColumnSpec columnSpec : mTableSpec.getColumns()) {
            builder.addStatement("values.put($L, object.$L())", columnSpec.columnName.toUpperCase(),
                    ColumnSpec.getterName(columnSpec.fieldName));
        }
        return builder.build();
    }

    private MethodSpec makeFromCursorMethod() {
        final ClassName originClass = ClassName.get(mTableSpec.getOriginElement());
        MethodSpec.Builder builder =  MethodSpec.methodBuilder("fromCursor")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(originClass)
                .addParameter(ISQLiteCursor.class, "cursor");
        builder.addStatement("final $1T object = new $1T()", originClass);
        for (final ColumnSpec columnSpec : mTableSpec.getColumns()) {
            if (FieldType.isLong(columnSpec.fieldType)
                    || FieldType.isInt(columnSpec.fieldType)
                    || FieldType.isShort(columnSpec.fieldType)) {
                builder.addStatement("object.$L(($T) cursor.getLong(cursor.getColumnIndex($L)))",
                        ColumnSpec.setterName(columnSpec.fieldName), columnSpec.fieldType, columnSpec.columnName.toUpperCase());
            }
            else if (FieldType.isDouble(columnSpec.fieldType)
                    || FieldType.isFloat(columnSpec.fieldType)) {
                builder.addStatement("object.$L(($T) cursor.getDouble(cursor.getColumnIndex($L)))", ColumnSpec.setterName(columnSpec.fieldName),
                        columnSpec.fieldType, columnSpec.columnName.toUpperCase());
            } else if (FieldType.isByteArray(columnSpec.fieldType)) {
                builder.addStatement("object.$L(cursor.getBlob(cursor.getColumnIndex($L)))", ColumnSpec.setterName(columnSpec.fieldName), columnSpec.columnName.toUpperCase());
            } else if (FieldType.isString(columnSpec.fieldType)) {
                builder.addStatement("object.$L(cursor.getString(cursor.getColumnIndex($L)))", ColumnSpec.setterName(columnSpec.fieldName), columnSpec.columnName.toUpperCase());
            }
        }
        builder.addStatement("return object");
        return builder.build();
    }

    private void addColumnsFields(TypeSpec.Builder builder){
        for (final ColumnSpec columnSpec : mTableSpec.getColumns()) {
            builder.addField(makeColumnNameField(columnSpec));
        }
    }

    private FieldSpec makeColumnNameField(ColumnSpec columnSpec) {
        return FieldSpec.builder(String.class, columnSpec.columnName.toUpperCase())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", columnSpec.columnName)
                .build();
    }
}
