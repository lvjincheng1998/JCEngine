package demo.util;

import pers.jc.util.JCDateFormat;

import java.util.Date;

public class Test_JCDateFormat {

    public static void main(String[] args) {
        System.out.println("===默认格式===");
        JCDateFormat dateFormat = new JCDateFormat();
        output(dateFormat);

        System.out.println("===内置格式1===");
        dateFormat = new JCDateFormat(JCDateFormat.PATTERN_DATE);
        output(dateFormat);

        System.out.println("===内置格式2===");
        output(dateFormat.setPattern(JCDateFormat.PATTERN_DATETIME));
    }

    public static void output(JCDateFormat dateFormat) {
        //格式化13位时间戳数字
        long timestamp = System.currentTimeMillis();
        System.out.println(dateFormat.format(timestamp));
        //格式化10位时间戳数字
        long timestamp10 = timestamp / 1000;
        System.out.println(dateFormat.format(timestamp10));
        //格式化13位时间戳字符串
        String timestampStr = String.valueOf(timestamp);
        System.out.println(dateFormat.format(timestampStr));
        //格式化10位时间戳字符串
        String timestamp10Str = String.valueOf(timestamp / 1000);
        System.out.println(dateFormat.format(timestamp10Str));
        //格式化日期对象
        Date date = new Date(timestamp);
        System.out.println(dateFormat.format(date));
    }
}
