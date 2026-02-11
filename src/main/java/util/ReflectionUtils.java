package util;

import java.lang.reflect.Field;

public final class ReflectionUtils {
    public ReflectionUtils() {
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) {
        Field field;
        try {
            field = obj.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
