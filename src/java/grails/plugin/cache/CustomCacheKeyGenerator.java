package grails.plugin.cache;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generate better cache key, compare to {@link org.springframework.cache.interceptor.DefaultKeyGenerator}
 */
public class CustomCacheKeyGenerator implements KeyGenerator {
    public Object generate(Object target, Method method, Object... params) {
        Class<?> objClass = AopProxyUtils.ultimateTargetClass(target);
        List<Object> cacheKey = new ArrayList<Object>();
        cacheKey.add(objClass.getName().intern());
        cacheKey.add(System.identityHashCode(target));
        cacheKey.add(method.toString().intern());
        cacheKey.addAll(Arrays.asList(params));
        System.out.println(">>>> " + method.toString());
        return cacheKey;
    }
}
