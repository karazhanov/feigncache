package feign.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author karazhanov on 15.09.17.
 */
@Slf4j
public class RestClientCache<T> implements InvocationHandler {

    private final Class<?>[] interfaces;
    private final T realObject;
    private Map<Method, MethodCache> cache;

    private RestClientCache(T realObject) {
        this.realObject = realObject;
        interfaces = realObject.getClass().getInterfaces();
        initCachedMethods();
    }

    @SuppressWarnings("unchecked")
    public static <T> T wrap(T target) {
        log.info("Enable caching for " + Arrays.toString(target.getClass().getInterfaces()));
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new RestClientCache(target));
    }

    private void initCachedMethods() {
        cache = new HashMap<>();
        Arrays.stream(interfaces)
                .map(Class::getMethods)
                .flatMap(Arrays::stream)
                .filter(filterObjectMethods())
                .filter(method -> AnnotationUtils.findAnnotation(method, RestCache.class) != null)
                .filter(method -> !method.getReturnType().equals(Void.TYPE))
                .forEach(method -> cache.put(method, new MethodCache(method, 50)));
    }

    private Predicate<Method> filterObjectMethods() {
        return method -> {
            if ("equals".equals(method.getName())) {
                return false;
            } else if ("hashCode".equals(method.getName())) {
                return false;
            } else if ("toString".equals(method.getName())) {
                return false;
            }
            return true;
        };
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("equals".equals(method.getName())) {
            try {
                Object otherHandler =
                        args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                return realObject.equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if ("hashCode".equals(method.getName())) {
            return realObject.hashCode();
        } else if ("toString".equals(method.getName())) {
            return realObject.toString();
        }
        if(cache.containsKey(method)) {
            return cache.get(method).invoke(realObject, args);
        }
        return method.invoke(realObject, args);
    }
}
