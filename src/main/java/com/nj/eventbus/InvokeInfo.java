package com.nj.eventbus;

import java.lang.reflect.Method;

public class InvokeInfo {
    private final Object obj;
    private final Method method;
    private final Object param;

    public InvokeInfo(Object obj, Method method, Object param) {
        this.obj = obj;
        this.method = method;
        this.param = param;
    }

    @Override
    public String toString() {
        return "InvokeInfo [obj=" + obj + ", method=" + method + ", param=" + param + "]";
    }
}
