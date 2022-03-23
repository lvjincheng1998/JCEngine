package pers.jc.network;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SocketMethod {
    /**是否需要认证后才能调用 */
    boolean auth() default true;
    /**是否异步处理该函数 */
    boolean async() default false;
}
