package ru.nikitazhelonkin.sqlite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nikita on 03.02.17.
 */

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface SQLiteColumn {

    String TEXT = "TEXT";
    String INTEGER = "INTEGER";
    String REAL = "REAL";
    String BLOB = "BLOB";

    String value() default "";

    String type() default "";

    boolean primaryKey() default false;

    boolean autoincrement() default false;

    boolean unique() default false;

    boolean notnull() default false;

    String references() default "";

}
