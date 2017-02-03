package ru.nikitazhelonkin.sqlite.compiler;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import java.util.Date;

import javax.lang.model.element.Element;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by nikita on 03.02.17.
 */

public class FieldType {

    static volatile Types sTypes;

    static volatile Elements sElements;

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

    static boolean isDate(TypeMirror type) {
        return isAssignable(type, Date.class);
    }

    static boolean isNullable(TypeMirror type) {
        return TypeKind.DECLARED == type.getKind() || TypeKind.ARRAY == type.getKind();
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


}
