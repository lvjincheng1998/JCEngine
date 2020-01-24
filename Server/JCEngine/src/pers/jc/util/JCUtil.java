package pers.jc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JCUtil {
	
	public static void sleep(double second) {
		try {
			Thread.sleep((long)(second * 1000));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static long getTime() {
		return new Date().getTime();
	}

	public static String getTime(String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(new Date());
	}
	
	public static String getTime(long time, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(new Date(time));
	}
}
