package com.nj.eventbus;

import java.util.HashMap;
import java.util.Map;

class Util {
    /**
     * 基本数据类型转包装类，用于对比。
     */
    private static final Map<Class<?>, Class<?>> primitiveTypeWrapperClass = new HashMap<Class<?>, Class<?>>() {
        {
            put(byte.class, Byte.class);
            put(short.class, Short.class);
            put(int.class, Integer.class);
            put(long.class, Long.class);
            put(float.class, Float.class);
            put(double.class, Double.class);
            put(boolean.class, Boolean.class);
            put(char.class, Character.class);
        }
    };

    public static Class<?> getWrapperClass(Class<?> cls) {
        if (cls.isPrimitive() && primitiveTypeWrapperClass.containsKey(cls)) {
            return primitiveTypeWrapperClass.get(cls);
        }
        return cls;
    }
}
