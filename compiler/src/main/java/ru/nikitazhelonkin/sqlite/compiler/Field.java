package ru.nikitazhelonkin.sqlite.compiler;


import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by nikita on 03.02.17.
 */

class Field {

    private static final Pattern PREFIX_NAMING = Pattern.compile("^m([A-Z][a-zA-Z0-9]*)$");

    private static volatile Types sTypes;

    private static volatile Elements sElements;

    public static void defineTypesAndElemens(Types types, Elements elements) {
        sTypes = types;
        sElements = elements;
    }

    static boolean isLong(TypeMirror type) {
        try {
            return TypeKind.LONG == type.getKind()
                    || TypeKind.LONG == unbox(type).getKind();
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    static boolean isInt(TypeMirror type) {
        try {
            return TypeKind.INT == type.getKind()
                    || TypeKind.INT == unbox(type).getKind();
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    static boolean isShort(TypeMirror type) {
        try {
            return TypeKind.SHORT == type.getKind()
                    || TypeKind.SHORT == unbox(type).getKind();
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    static boolean isDouble(TypeMirror type) {
        try {
            return TypeKind.DOUBLE == type.getKind()
                    || TypeKind.DOUBLE == unbox(type).getKind();
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    static boolean isFloat(TypeMirror type) {
        try {
            return TypeKind.FLOAT == type.getKind()
                    || TypeKind.FLOAT == unbox(type).getKind();
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    static boolean isByteArray(TypeMirror type) {
        return TypeKind.ARRAY == type.getKind() && TypeKind.BYTE == ((ArrayType) type).getComponentType().getKind();
    }

    static boolean isString(TypeMirror type) {
        return isAssignable(type, String.class);
    }

    static boolean isAssignable(TypeMirror type, Class<?> clazz) {
        return sTypes.isAssignable(type, sElements.getTypeElement(clazz.getCanonicalName()).asType());
    }

    private static PrimitiveType unbox(TypeMirror typeMirror) {
        try {
            return sTypes.unboxedType(typeMirror);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Not a boxed primitive type", e);
        }
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
