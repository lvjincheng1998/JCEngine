package pers.jc.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class JCDateFormat {
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATETIME_SSS = "yyyy-MM-dd HH:mm:ss SSS";

    private final Map<String, SimpleDateFormat> formatMap = new HashMap<>();
    private String curPattern;
    private SimpleDateFormat curFormat;

    public JCDateFormat() {
        setPattern(PATTERN_DATETIME_SSS);
    }

    public JCDateFormat(String pattern) {
        setPattern(pattern);
    }

    public JCDateFormat setPattern(String pattern) {
        if (pattern.equals(curPattern)) return this;
        curPattern = pattern;
        curFormat = formatMap.get(pattern);
        if (curFormat == null) {
            curFormat = new SimpleDateFormat(pattern);
            formatMap.put(pattern, curFormat);
        }
        return this;
    }

    public String format(Object obj) {
        if (obj instanceof Number) {
            String str = obj.toString();
            if (str.length() == 10) {
                return curFormat.format(Long.parseLong(str) * 1000);
            }
        } else if (obj instanceof String) {
            String str = (String) obj;
            if (str.length() == 13) {
                return curFormat.format(Long.valueOf(str));
            } else if (str.length() == 10) {
                return curFormat.format(Long.parseLong(str) * 1000);
            }
        }
        return curFormat.format(obj);
    }
}
