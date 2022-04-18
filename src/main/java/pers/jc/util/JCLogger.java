package pers.jc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JCLogger {
	private static int level = 1;
	public static final int LEVEL_DEBUG = 0;
	public static final int LEVEL_INFO = 1;
	public static final int LEVEL_WARN = 2;
	public static final int LEVEL_ERROR = 3;
	private static final String LEVEL_DEBUG_SIGN = "[DEBUG]";
	private static final String LEVEL_INFO_SIGN = "[INFO]";
	private static final String LEVEL_WARN_SIGN = "[WARN]";
	private static final String LEVEL_ERROR_SIGN = "[ERROR]";
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	
	public static void setLevel(int level) {
		JCLogger.level = level;
	}
	
	private static void println(Object[] msg, int levelID, String levelSign, boolean showStackTrace) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(levelSign).append(" ");
		stringBuilder.append(simpleDateFormat.format(new Date()));
		stringBuilder.append("\n");
		for (Object str : msg) {
			stringBuilder.append(str).append("\t");
		}
		stringBuilder.append("\n");
		if (showStackTrace) {
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			for (int i = 3; i < stackTraceElements.length; i++) {
				StackTraceElement stackTraceElement = stackTraceElements[i];
				stringBuilder.append("@").append(stackTraceElement.getClassName());
				stringBuilder.append(".").append(stackTraceElement.getMethodName());
				stringBuilder.append("(").append(stackTraceElement.getFileName());
				stringBuilder.append(":").append(stackTraceElement.getLineNumber()).append(")");
				stringBuilder.append("\n");
			}
		}
		String log = stringBuilder.toString();
		if (levelID == LEVEL_ERROR) {
			System.err.print(log);
		} else {
			System.out.print(log);
		}
	}
	
	public static void debug(Object... msg) {
		if (level > LEVEL_DEBUG) {
			return;
		}
		println(msg, LEVEL_DEBUG, LEVEL_DEBUG_SIGN, false);
	}
	
	public static void info(Object... msg) {
		if (level > LEVEL_INFO) {
			return;
		}
		println(msg, LEVEL_INFO, LEVEL_INFO_SIGN, false);
	}
	
	public static void warn(Object... msg) {
		if (level > LEVEL_WARN) {
			return;
		}
		println(msg, LEVEL_WARN, LEVEL_WARN_SIGN, false);
	}
	
	public static void error(Object... msg) {
		if (level > LEVEL_ERROR) {
			return;
		}
		println(msg, LEVEL_ERROR, LEVEL_ERROR_SIGN, false);
	}

	public static void errorStackTrace(Object... msg) {
		if (level > LEVEL_ERROR) {
			return;
		}
		println(msg, LEVEL_ERROR, LEVEL_ERROR_SIGN, true);
	}
}
