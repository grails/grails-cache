package grails.plugin.cache.util;

import grails.util.GrailsNameUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

public class ClassUtils {

    public static Object getPropertyOrFieldValue(Object object, String propertyOrFieldName) {
        final String getterName = GrailsNameUtils
                .getGetterName(propertyOrFieldName);
        final Class<? extends Object> objectClass = object.getClass();
        try {
            final Method method = objectClass.getMethod(getterName, new Class[0]);
            if (method != null) {
                ReflectionUtils.makeAccessible(method);
                return method.invoke(object, new Object[0]);
            }
        } catch (Exception e) {
        }
        try {
            final Field field = ReflectionUtils.findField(objectClass, propertyOrFieldName);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                return field.get(object);
            }
        } catch (Exception e) {
        }
        return null;
    }
}
