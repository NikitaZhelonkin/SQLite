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

    JavaFile brewJavaFile() {
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

    private TypeName baseTable(ClassName originClass) {
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
        for (ColumnSpec columnSpec : mTableSpec.getColumns()) {
            tableBuilder.add(Column.create(
                    columnSpec.getColumnName(),
                    columnSpec.getColumnType(),
                    columnSpec.isPrimaryKey(),
                    columnSpec.isAutoincrement()));
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
            if(!columnSpec.isAutoincrement()){
                builder.addStatement("values.put($L, object.$L())", columnSpec.getColumnName().toUpperCase(),
                        Field.getterName(columnSpec.getFieldName()));
            }
        }
        return builder.build();
    }

    private MethodSpec makeFromCursorMethod() {
        final ClassName originClass = ClassName.get(mTableSpec.getOriginElement());
        MethodSpec.Builder builder = MethodSpec.methodBuilder("fromCursor")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(originClass)
                .addParameter(ISQLiteCursor.class, "cursor");
        builder.addStatement("final $1T object = new $1T()", originClass);
        for (final ColumnSpec columnSpec : mTableSpec.getColumns()) {
            if (Field.isLong(columnSpec.getFieldType())
                    || Field.isInt(columnSpec.getFieldType())
                    || Field.isShort(columnSpec.getFieldType())) {
                builder.addStatement("object.$L(($T) cursor.getLong(cursor.getColumnIndex($L)))",
                        Field.setterName(columnSpec.getFieldName()), columnSpec.getFieldType(), columnSpec.getColumnName().toUpperCase());
            } else if (Field.isBoolean(columnSpec.getFieldType())) {
                builder.addStatement("object.$L(cursor.getLong(cursor.getColumnIndex($L)) == 1)",
                        Field.setterName(columnSpec.getFieldName()), columnSpec.getColumnName().toUpperCase());
            } else if (Field.isDouble(columnSpec.getFieldType())
                    || Field.isFloat(columnSpec.getFieldType())) {
                builder.addStatement("object.$L(($T) cursor.getDouble(cursor.getColumnIndex($L)))",
                        Field.setterName(columnSpec.getFieldName()),
                        columnSpec.getFieldType(), columnSpec.getColumnName().toUpperCase());
            } else if (Field.isByteArray(columnSpec.getFieldType())) {
                builder.addStatement("object.$L(cursor.getBlob(cursor.getColumnIndex($L)))",
                        Field.setterName(columnSpec.getFieldName()), columnSpec.getColumnName().toUpperCase());
            } else if (Field.isString(columnSpec.getFieldType())) {
                builder.addStatement("object.$L(cursor.getString(cursor.getColumnIndex($L)))",
                        Field.setterName(columnSpec.getFieldName()), columnSpec.getColumnName().toUpperCase());
            }
        }
        builder.addStatement("return object");
        return builder.build();
    }

    private void addColumnsFields(TypeSpec.Builder builder) {
        for (final ColumnSpec columnSpec : mTableSpec.getColumns()) {
            builder.addField(makeColumnNameField(columnSpec));
        }
    }

    private FieldSpec makeColumnNameField(ColumnSpec columnSpec) {
        return FieldSpec.builder(String.class, columnSpec.getColumnName().toUpperCase())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", columnSpec.getColumnName())
                .build();
    }
}
