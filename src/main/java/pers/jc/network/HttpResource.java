package pers.jc.network;

import java.util.HashMap;
import java.util.HashSet;

public class HttpResource {
    private String uri;

    public HttpResource(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    private static final HashMap<String, String> contentTypeMap = new HashMap<>();
    private static final HashSet<String> byteTypeSet = new HashSet<>();

    static {
        contentTypeMap.put(".html", "text/html; charset=UTF-8");
        contentTypeMap.put(".js", "application/x-javascript");
        contentTypeMap.put(".css", "text/css; charset=UTF-8");
        contentTypeMap.put(".txt", "text/plain; charset=UTF-8");
        contentTypeMap.put(".xml", "text/xml; charset=UTF-8");
        contentTypeMap.put(".json", "application/json");
        contentTypeMap.put(".gif", "image/gif");
        contentTypeMap.put(".jpg", "image/jpeg");
        contentTypeMap.put(".png", "image/png");
        contentTypeMap.put(".ico", "image/x-icon");
        contentTypeMap.put(".woff", "application/font-woff");
        contentTypeMap.put(".woff2", "application/font-woff");
        contentTypeMap.put(".svg", "text/xml; charset=UTF-8");
        contentTypeMap.put(".eot", "text/plain");
        contentTypeMap.put(".ttf", "text/plain");
        byteTypeSet.add(".gif");
        byteTypeSet.add(".jpg");
        byteTypeSet.add(".png");
        byteTypeSet.add(".ico");
        byteTypeSet.add(".woff");
        byteTypeSet.add(".woff2");
        byteTypeSet.add(".eot");
        byteTypeSet.add(".ttf");
    }

    private static String getSuffix(String uri) {
        if (uri == null) return null;
        int lastPointIndex = uri.lastIndexOf(".");
        if (lastPointIndex == -1) return null;
        return uri.substring(lastPointIndex);
    }

    public static String getContentType(String uri) {
        String suffix = getSuffix(uri);
        if (suffix == null) return null;
        return contentTypeMap.get(suffix);
    }

    public static boolean check(String uri) {
        return getContentType(uri) != null;
    }

    public static boolean isByteType(String uri) {
        String suffix = getSuffix(uri);
        if (suffix == null) return false;
        return byteTypeSet.contains(suffix);
    }
}
