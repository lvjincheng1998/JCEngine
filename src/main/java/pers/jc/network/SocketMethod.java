package pers.jc.network;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SocketMethod {
    /**是否需要认证后才能调用 */
    boolean auth() default true;
    boolean async() default false;
}
