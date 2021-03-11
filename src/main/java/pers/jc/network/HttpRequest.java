package pers.jc.network;

import java.util.Map;

public class HttpRequest {
    private String uri;
    private HttpType httpType;
    private Map<String, Object> paramMap;
    public static Object URI_NOT_MATCH = new Object();
    public static Object TYPE_NOT_MATCH = new Object();

    public HttpRequest(String uri, HttpType httpType, Map<String, Object> paramMap) {
        this.uri = uri.split("\\?")[0];
        this.httpType = httpType;
        this.paramMap = paramMap;
    }

    public String getUri() {
        return uri;
    }

    public HttpType getHttpType() {
        return httpType;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }
}
