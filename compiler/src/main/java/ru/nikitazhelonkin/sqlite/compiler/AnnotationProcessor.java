package ru.nikitazhelonkin.sqlite.compiler;

import com.squareup.javapoet.JavaFile;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import ru.nikitazhelonkin.sqlite.annotation.SQLiteColumn;
import ru.nikitazhelonkin.sqlite.annotation.SQLiteObject;

/**
 * Created by nikita on 03.02.17.
 */

@SupportedAnnotationTypes({
        "ru.nikitazhelonkin.sqlite.annotation.SQLiteObject",
        "ru.nikitazhelonkin.sqlite.annotation.SQLiteColumn"})
public class AnnotationProcessor extends AbstractProcessor {

    private final Map<Class<? extends Annotation>, Visitor> mVisitors = new LinkedHashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        mVisitors.put(SQLiteObject.class, new SQLiteObjectVisitor(env));
        mVisitors.put(SQLiteColumn.class, new SQLiteColumnVisitor(env));

        Field.sTypes = env.getTypeUtils();
        Field.sElements = env.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        final Map<TypeElement, TableSpec> specs = new LinkedHashMap<>();
        for (final Map.Entry<Class<? extends Annotation>, Visitor> entry : mVisitors.entrySet()) {
            final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(entry.getKey());
            for (final Element element : elements) {
                try {
                    element.accept(entry.getValue(), specs);
                } catch (Exception e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), element);
                }
            }
        }

        Filer filer = processingEnv.getFiler();
        for (final Map.Entry<TypeElement, TableSpec> entry : specs.entrySet()) {
            final TypeElement typeElement = entry.getKey();
            final TableSpec tableSpec = entry.getValue();
            try {
                final JavaFile javaFile = new TableMaker(tableSpec).brewJavaFile();
                javaFile.writeTo(filer);
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), typeElement);
            }
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
