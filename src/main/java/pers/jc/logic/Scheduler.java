package pers.jc.logic;

import java.util.LinkedList;

/**
 * 1、不要在多线程环境中直接使用该类的接口
 * 2、如果要在别的线程调用该类的接口，建议借用CallbackHandler类中的offerCallback方法来调用Scheduler类的接口。
 */
public class Scheduler {
    private LinkedList<ScheduleInfo> toAdds = new LinkedList<>();
    private LinkedList<ScheduleInfo> toRuns = new LinkedList<>();

    public void scheduleOnce(Runnable runnable, long delay) {
        schedule(runnable, 0, 0, delay);
    }

    public void schedule(Runnable runnable, long interval) {
        schedule(runnable, interval, -1, interval);
    }

    public void schedule(Runnable runnable, long interval, int repeat) {
        schedule(runnable, interval, repeat, interval);
    }

    public void schedule(Runnable runnable, long interval, int repeat, long delay) {
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.runnable = runnable;
        scheduleInfo.interval = interval;
        scheduleInfo.repeat = repeat;
        scheduleInfo.delay = delay;
        scheduleInfo.executeCount = 0;
        scheduleInfo.isValid = true;
        scheduleInfo.calculateNextRunTime();
        toAdds.add(scheduleInfo);
    }

    public void unSchedule(Runnable runnable) {
        for (ScheduleInfo scheduleInfo : toAdds) {
            if (scheduleInfo.runnable == runnable) scheduleInfo.isValid = false;
        }
        for (ScheduleInfo scheduleInfo : toRuns) {
            if (scheduleInfo.runnable == runnable) scheduleInfo.isValid = false;
        }
    }

    protected void update() {
        long currentTimeMillis = System.currentTimeMillis();
        toRuns.addAll(toAdds);
        toAdds.clear();
        toRuns.forEach(scheduleInfo -> {
            while (currentTimeMillis >= scheduleInfo.nextRunTime) {
                if (!scheduleInfo.isValid) return;
                try {
                    scheduleInfo.runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                scheduleInfo.calculateNextRunTime();
                scheduleInfo.executeCount++;
                if (scheduleInfo.repeat >= 0) {
                    if (scheduleInfo.executeCount > scheduleInfo.repeat) {
                        scheduleInfo.isValid = false;
                    }
                }
            }
        });
        toRuns.removeIf(scheduleInfo -> !scheduleInfo.isValid);
    }

    private class ScheduleInfo {
        //用户参数
        Runnable runnable;
        long interval;
        int repeat;
        long delay;
        //运行参数
        long nextRunTime;
        int executeCount;
        boolean isValid;
        //method
        public void calculateNextRunTime() {
            if (nextRunTime == 0) {
                nextRunTime = System.currentTimeMillis();
            }
            long nextInterval = executeCount == 0 ? delay : interval;
            nextRunTime += nextInterval;
        }
    }
}
