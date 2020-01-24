package pers.jc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JCLogger {
	private static int level = 1;
	public static int LEVEL_DEBUG = 0;
	public static int LEVEL_INFO = 1;
	public static int LEVEL_WARN = 2;
	public static int LEVEL_ERROR = 3;
	private static String LEVEL_DEBUG_SIGN = "DEBUG";
	private static String LEVEL_INFO_SIGN = "INFO";
	private static String LEVEL_WARN_SIGN = "WARN";
	private static String LEVEL_ERROR_SIGN = "ERROR";
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	
	public static void setLevel(int level) {
		JCLogger.level = level;
	}
	
	private static String getLog(String levelSign, boolean showStackTrace) {
		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
		String out = "[" + levelSign + "] "
				+ simpleDateFormat.format(new Date()) 
				+ (!showStackTrace ? "" : (" "
				+ stackTraceElement.getClassName() + "."
				+ stackTraceElement.getMethodName() + "("
				+ stackTraceElement.getFileName() + ":" 
				+ stackTraceElement.getLineNumber() + ")"));
		return out;
	}
	
	public static void debug(String msg) {
		if (level > LEVEL_DEBUG) {
			return;
		}
		System.out.println(getLog(LEVEL_DEBUG_SIGN, true));
		System.out.println(msg);
	}
	
	public static void info(String msg) {
		if (level > LEVEL_INFO) {
			return;
		}
		System.out.println(getLog(LEVEL_INFO_SIGN, false));
		System.out.println(msg);
	}
	
	public static void warn(String msg) {
		if (level > LEVEL_WARN) {
			return;
		}
		System.out.println(getLog(LEVEL_WARN_SIGN, false));
		System.out.println(msg);
	}
	
	public static void error(String msg) {
		if (level > LEVEL_ERROR) {
			return;
		}
		System.err.println(getLog(LEVEL_ERROR_SIGN, true));
		System.err.println(msg);
	}
}
