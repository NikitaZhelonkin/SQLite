package ru.nikitazhelonkin.sqlite.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.SOURCE)
public @interface Reference {

    String parentTable();

    String parentColumn();

    @Action String onDelete() default NO_ACTION;

    @Action String onUpdate() default NO_ACTION;

    String NO_ACTION = "NO ACTION";

    String RESTRICT = "RESTRICT";

    String SET_NULL = "SET NULL";

    String SET_DEFAULT = "SET DEFAULT";

    String CASCADE = "CASCADE";

    @StringDef({NO_ACTION, RESTRICT, SET_NULL, SET_DEFAULT, CASCADE})
    @interface Action {
    }
}
