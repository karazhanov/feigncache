package feign.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author karazhanov on 15.09.17.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestCache {
    /*
    *  Time in ms for store data in cache
    *  Default value = 1000ms = 1s
    */
    long cacheTime() default 1000;
}
