package ru.nikitazhelonkin.sqlite.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;


public class DaoMaker {
    private final TableSpec mTableSpec;

    DaoMaker(TableSpec spec) {
        mTableSpec = spec;
    }

    JavaFile brewJavaFile() {
        final TypeElement originElement = mTableSpec.getOriginElement();
        final ClassName originClass = ClassName.get(originElement);
        final TypeSpec.Builder spec = TypeSpec.classBuilder(getClassName().simpleName())
                .addOriginatingElement(originElement)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(makeConstructor())
                .superclass(baseDao(originClass));

        return JavaFile.builder(originClass.packageName(), spec.build())
                .addFileComment("Generated code from SQLite. Do not modify!")
                .skipJavaLangImports(true)
                .build();
    }

    private MethodSpec makeConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("ru.nikitazhelonkin.sqlite", "SQLiteDatabaseProvider"), "provider")
                .addStatement("super(provider, new $T())", mTableSpec.getClassName())
                .build();
    }



    private TypeName baseDao(ClassName originClass) {
        return ParameterizedTypeName.get( ClassName.get("ru.nikitazhelonkin.sqlite", "BaseDao"), originClass);
    }

    private ClassName getClassName() {
        final ClassName originClassName = ClassName.get(mTableSpec.getOriginElement());
        return ClassName.get(originClassName.packageName(), originClassName.simpleName() + "Dao");
    }
}