package ru.nikitazhelonkin.sqlite.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import ru.nikitazhelonkin.sqlite.Column;
import ru.nikitazhelonkin.sqlite.IContentValues;
import ru.nikitazhelonkin.sqlite.ISQLiteCursor;
import ru.nikitazhelonkin.sqlite.ISQLiteDatabase;
import ru.nikitazhelonkin.sqlite.Table;
import ru.nikitazhelonkin.sqlite.TableBuilder;
import ru.nikitazhelonkin.sqlite.annotation.Reference;


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
            StringBuilder referenceBuilder = new StringBuilder();
            Reference reference = columnSpec.getReference();

            if (reference != null) {
                referenceBuilder.append(reference.parentTable())
                        .append("(").append(reference.parentColumn()).append(")")
                        .append(" ON UPDATE ").append(reference.onUpdate())
                        .append(" ON DELETE ").append(reference.onDelete());
            }
            tableBuilder.add(Column.create(
                    columnSpec.getColumnName(),
                    columnSpec.getColumnType(),
                    columnSpec.isPrimaryKey(),
                    columnSpec.isAutoincrement(),
                    columnSpec.isUnique(),
                    columnSpec.isNotNull())
                    .references(referenceBuilder.toString()));
        }
        builder.addStatement("db.execSQL($S)", tableBuilder.toSql());
        List<IndexSpec> indexSpecList = mTableSpec.getIndices();

        for (IndexSpec indexSpec : indexSpecList) {
            builder.addStatement("db.execSQL($S)", indexSpec.toSql(mTableSpec.getTableName()));
        }
        return builder.build();
    }

    private MethodSpec makeBindValuesMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("bindValues")
                .addAnnotation(Override.class)
                .addParameter(IContentValues.class, "values")
                .addParameter(ClassName.get(mTableSpec.getOriginElement()), "object")
                .addModifiers(Modifier.PUBLIC);
        for (final ColumnSpec columnSpec : mTableSpec.getColumns()) {
            TypeMirror converterClass = columnSpec.getConverterClazz();
            if (converterClass != null) {
                builder.addStatement("values.put($L, new $T().serialize(object.$L()))",
                        columnSpec.getColumnName().toUpperCase(),
                        converterClass,
                        Field.getterName(columnSpec.getFieldName(), columnSpec.getFieldType()));
            } else {
                builder.addStatement("values.put($L, object.$L())",
                        columnSpec.getColumnName().toUpperCase(),
                        Field.getterName(columnSpec.getFieldName(), columnSpec.getFieldType()));
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

        switch (mTableSpec.getDeserialization()) {
            case METHOD:
                createWithMethods(builder, originClass);
                break;
            case CONSTRUCTOR:
                createWithConstructor(builder, originClass);
                break;
        }
        return builder.build();
    }

    private void createWithMethods(MethodSpec.Builder builder, final ClassName originClass) {
        builder.addStatement("final $1T object = new $1T()", originClass);
        for (final ColumnSpec columnSpec : mTableSpec.getColumns()) {
            TypeMirror converterClass = columnSpec.getConverterClazz();
            TypeMirror fieldType = columnSpec.getFieldType();
            if(converterClass!=null){
                builder.addStatement("object.$L(new $T().deserialize(cursor.$L(cursor.getColumnIndex($L))))",
                        Field.setterName(columnSpec.getFieldName(), fieldType), converterClass,  cursorGetMethodName(fieldType),
                        columnSpec.getColumnName().toUpperCase());
            }else if(Field.isBoolean(columnSpec.getFieldType())) {
                builder.addStatement("object.$L(($T) (cursor.getInt(cursor.getColumnIndex($L)) == 1))",
                        Field.setterName(columnSpec.getFieldName(), fieldType), fieldType,
                        columnSpec.getColumnName().toUpperCase());
            }else {
                builder.addStatement("object.$L(($T) cursor.$L(cursor.getColumnIndex($L)))",
                        Field.setterName(columnSpec.getFieldName(), fieldType), fieldType, cursorGetMethodName(fieldType),
                        columnSpec.getColumnName().toUpperCase());
            }

        }
        builder.addStatement("return object");
    }

    private void createWithConstructor(MethodSpec.Builder builder, final ClassName originClass) {
        List<Object> objects = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder("return new $T(");
        objects.add(originClass);
        for (int i = 0; i < mTableSpec.getColumns().size(); i++) {
            stringBuilder.append("\n");
            final ColumnSpec columnSpec = mTableSpec.getColumns().get(i);
            TypeMirror converterClass = columnSpec.getConverterClazz();
            TypeMirror fieldType = columnSpec.getFieldType();

            if(converterClass!=null){
                stringBuilder.append("new $T().deserialize(cursor.$L(cursor.getColumnIndex($L)))");
                objects.add(converterClass);
                objects.add(cursorGetMethodName(fieldType));
                objects.add(columnSpec.getColumnName().toUpperCase());
            }else if(Field.isBoolean(columnSpec.getFieldType())) {
                stringBuilder.append("($T) (cursor.getInt(cursor.getColumnIndex($L) == 1))");
                objects.add(fieldType);
                objects.add(columnSpec.getColumnName().toUpperCase());
            }else {
                stringBuilder.append("($T) cursor.$L(cursor.getColumnIndex($L))");
                objects.add(fieldType);
                objects.add(cursorGetMethodName(fieldType));
                objects.add(columnSpec.getColumnName().toUpperCase());
            }
            if (i != mTableSpec.getColumns().size() - 1) stringBuilder.append(",");
        }
        stringBuilder.append(")");
        builder.addStatement(stringBuilder.toString(), (Object[]) objects.toArray());
    }

    private String cursorGetMethodName(TypeMirror typeMirror){
        if (Field.isLong(typeMirror)
                || Field.isInt(typeMirror)
                || Field.isShort(typeMirror)
                || Field.isBoolean(typeMirror)) {
            return "getLong";
        } else if (Field.isDouble(typeMirror)
                || Field.isFloat(typeMirror)) {
            return "getDouble";
        } else if (Field.isByteArray(typeMirror)) {
            return "getBlob";
        } else {
            return "getString";
        }
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
