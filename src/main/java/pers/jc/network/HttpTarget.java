package pers.jc.network;

import org.springframework.core.DefaultParameterNameDiscoverer;
import java.lang.reflect.Method;

public class HttpTarget {
    private static DefaultParameterNameDiscoverer defaultParameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private Object instance;
    private Method method;
    private HttpType httpType;
    private String[] parameterNames;

    public HttpTarget(Object instance, Method method, HttpType httpType) {
        this.instance = instance;
        this.method = method;
        this.httpType = httpType;
        this.parameterNames = defaultParameterNameDiscoverer.getParameterNames(this.method);
    }

    public Object getInstance() {
        return instance;
    }

    public Method getMethod() {
        return method;
    }

    public HttpType getHttpType() {
        return httpType;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }
}
