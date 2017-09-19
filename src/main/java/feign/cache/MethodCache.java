package feign.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author karazhanov on 18.09.17.
 */
@Slf4j
class MethodCache {
    private final Method method;
    private final LRUCache<ArrayHolder, DataHolder> cache;
    private final long cacheTime;

    MethodCache(Method method, int size) {
        cache = new LRUCache<>(size);
        this.method = method;
        RestCache restCacheAnnotation = AnnotationUtils.findAnnotation(method, RestCache.class);
        cacheTime = restCacheAnnotation.cacheTime();
        log.info("Enable cache for method "
                + method.getName()
                + " in class "
                + method.getDeclaringClass().getSimpleName()
                + " for " + cacheTime + "ms");
    }

    <T> Object invoke(T realObject, Object[] args) throws Throwable {
        log.info("Invoke cached method " + method.getName());
        log.info("Params = " + Arrays.toString(args));
        ArrayHolder key = new ArrayHolder<>(args);
        DataHolder dataHolder = cache.get(key);
        if (dataHolder != null) {
            if ( dataHolder.isValid() ) {
                log.info("Cached data = " + dataHolder.getData());
                return dataHolder.getData();
            } else {
                log.info("Cached data invalid ");
            }
        }
        Object invoke = method.invoke(realObject, args);
        log.info("Requested data = " + invoke);
        cache.put(key, new DataHolder<>(cacheTime, invoke));
        return invoke;
    }
}
