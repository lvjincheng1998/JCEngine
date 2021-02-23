package pers.jc.sql.ui;

import java.lang.annotation.*;

@Documented
@Target(value = ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface ColumnUI {
    String field() default "";
    String title() default "";
    int width() default 150;
    String align() default "center";
}
