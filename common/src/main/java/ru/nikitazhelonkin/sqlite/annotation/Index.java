package ru.nikitazhelonkin.sqlite.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.SOURCE)
public @interface Index {

    String[] value();

    String name() default "";

    boolean unique() default false;
}
