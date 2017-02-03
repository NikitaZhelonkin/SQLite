package ru.nikitazhelonkin.sqlite.compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import ru.nikitazhelonkin.sqlite.Column;

/**
 * Created by nikita on 03.02.17.
 */

class ColumnSpec {

    private static final Pattern PREFIX_NAMING = Pattern.compile("^m([A-Z][a-zA-Z0-9]*)$");

    final String fieldName;

    final TypeMirror fieldType;

    final String columnName;

    final String columnDef;

    final boolean isPrimaryKey;

    ColumnSpec(Element field, String columnName, String columnDef) {
        this(field, columnName, columnDef, false);
    }

    ColumnSpec(Element field, String columnName, String columnDef, boolean primaryKey) {
        this.fieldName = field.getSimpleName().toString();
        this.fieldType = field.asType();
        this.columnName = columnName;
        this.columnDef = columnDef;
        this.isPrimaryKey = primaryKey;
    }


    public static String columnName(String fieldName) {
        return underscore(removePrefix(fieldName));
    }

    public static String setterName(String fieldName) {
        return "set" + capitalize(removePrefix(fieldName));
    }

    public static String getterName(String fieldName) {
        return "get" + capitalize(removePrefix(fieldName));
    }

    private static String removePrefix(String fieldName) {
        final Matcher matcher = PREFIX_NAMING.matcher(fieldName);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return fieldName;
    }

    private static String underscore(String value) {
        return value.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
    }

    private static String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

}
