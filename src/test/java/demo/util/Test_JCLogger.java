package demo.util;

import pers.jc.util.JCLogger;

public class Test_JCLogger {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("===level-默认===");
        log();

        Thread.sleep(100);

        System.out.println("===level-debug===");
        JCLogger.setLevel(JCLogger.LEVEL_DEBUG);
        log();

        Thread.sleep(100);

        System.out.println("===level-info===");
        JCLogger.setLevel(JCLogger.LEVEL_INFO);
        log();

        Thread.sleep(100);

        System.out.println("===level-warn===");
        JCLogger.setLevel(JCLogger.LEVEL_WARN);
        log();

        Thread.sleep(100);

        System.out.println("===level-error===");
        JCLogger.setLevel(JCLogger.LEVEL_ERROR);
        log();
    }

    public static void log() {
        JCLogger.debug("debug");
        JCLogger.info("info");
        JCLogger.warn("warn");
        JCLogger.error("error");
        JCLogger.errorStackTrace("errorStackTrace");
    }
}
