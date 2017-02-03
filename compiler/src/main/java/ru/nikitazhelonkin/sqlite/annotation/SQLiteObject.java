package ru.nikitazhelonkin.sqlite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nikita on 03.02.17.
 */

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface SQLiteObject {

    String value();
}
