package pers.jc.util;

import java.util.UUID;

public class JCUtil {

	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
