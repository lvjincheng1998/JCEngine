package pers.jc.network;

import java.lang.reflect.Method;

public class SocketTarget {
    private Object instance;
    private Method method;

    public SocketTarget(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    public Object getInstance() {
        return instance;
    }

    public Method getMethod() {
        return method;
    }
}
