package pers.jc.util;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JCLogger {
	private static int level = 1;
	public static final int LEVEL_DEBUG = 0;
	public static final int LEVEL_INFO = 1;
	public static final int LEVEL_WARN = 2;
	public static final int LEVEL_ERROR = 3;
	private static final String LEVEL_DEBUG_SIGN = "DEBUG";
	private static final String LEVEL_INFO_SIGN = "INFO";
	private static final String LEVEL_WARN_SIGN = "WARN";
	private static final String LEVEL_ERROR_SIGN = "ERROR";
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

	private static File logCatalog;
	private static File logFile;
	private static int logFileID;
	private static long logFileLength;
	private static long logOutputInterval;
	private static boolean logWriterStart;
	private static final ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<>();

	public static void setLogCatalog(String catalog) {
		setLogCatalog(catalog, 4 * 1024 * 1024, 1000);
	}
	
	public static void setLogCatalog(String catalog, long fileLength, long outputInterval) {
		try {
			logCatalog = new File(catalog);
			if (!logCatalog.exists() && !logCatalog.mkdirs()) {
				throw new Exception();
			}
			createLogFile();
			logFileLength = fileLength;
			logOutputInterval = outputInterval;
			createLogWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized static int nextLogFileID() {
		logFileID++;
		return logFileID;
	}
	
	private static void createLogFile() throws Exception {
		String logFileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss_").format(new Date()) + nextLogFileID() + ".log";
		String logFilePath = logCatalog.getPath() + File.separator + logFileName;
		logFile = new File(logFilePath);
		if (!logFile.exists() && !logFile.createNewFile()) {
			throw new Exception();
		}
	}
	
	private static void createLogWriter() {
		new Thread(() -> {
			logWriterStart = true;
			while (true) {
				try {
					Thread.sleep(logOutputInterval);
					String log;
					FileWriter fileWriter = new FileWriter(logFile, true);
					while ((log = logQueue.poll()) != null) {
						if (logFile.length() >= logFileLength) {
							fileWriter.close();
							createLogFile();
							fileWriter = new FileWriter(logFile, true);
						}
						fileWriter.write(log);
					}
					fileWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public static void setLevel(int level) {
		JCLogger.level = level;
	}
	
	private static String getLog(Object[] msg, String levelSign, boolean showStackTrace) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[").append(levelSign).append("] ");
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
		if (logWriterStart) {
			logQueue.add(log);
		}
		return log;
	}
	
	public static void debug(Object... msg) {
		if (level > LEVEL_DEBUG) {
			return;
		}
		System.out.print(getLog(msg, LEVEL_DEBUG_SIGN, false));
	}
	
	public static void info(Object... msg) {
		if (level > LEVEL_INFO) {
			return;
		}
		System.out.print(getLog(msg, LEVEL_INFO_SIGN, false));
	}
	
	public static void warn(Object... msg) {
		if (level > LEVEL_WARN) {
			return;
		}
		System.out.print(getLog(msg, LEVEL_WARN_SIGN, false));
	}
	
	public static void error(Object... msg) {
		if (level > LEVEL_ERROR) {
			return;
		}
		System.err.print(getLog(msg, LEVEL_ERROR_SIGN, false));
	}

	public static void errorStackTrace(Object... msg) {
		if (level > LEVEL_ERROR) {
			return;
		}
		System.err.print(getLog(msg, LEVEL_ERROR_SIGN, true));
	}
}
