package pers.jc.sql.ui;

import java.lang.annotation.*;

@Documented
@Target(value = ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface TableUI {
    String url() default "";
    int width() default 150;
    String align() default "center";
}