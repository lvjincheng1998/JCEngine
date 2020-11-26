package pers.jc.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class JCUtil {

	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	public static int getTimestamp10() {
		return (int) (System.currentTimeMillis() / 1000);
	}

	public static String getDatetimeStr(String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(new Date());
	}
	
	public static String getDatetimeStr(long timestamp13, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(new Date(timestamp13));
	}

	public static String getDatetimeStr(int timestamp10) {
		return getDatetimeStr((long)timestamp10 * 1000,"yyyy-MM-dd hh:mm:ss");
	}

	public static String getDatetimeStr() {
		return getDatetimeStr("yyyy-MM-dd hh:mm:ss");
	}

	public static String getDateStr() {
		return getDatetimeStr("yyyy-MM-dd");
	}
}
