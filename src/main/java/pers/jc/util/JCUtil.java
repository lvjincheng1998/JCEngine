package pers.jc.util;

import java.util.UUID;

public class JCUtil {

	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static boolean isLinux() {
		return System.getProperties().getProperty("os.name").startsWith("Linux");
	}
}
