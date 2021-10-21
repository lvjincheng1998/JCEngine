package pers.jc.logic;

import java.util.LinkedList;

public class Scheduler {
    private long currentTimeMillis = 0;
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
        scheduleInfo.lastTime = currentTimeMillis;
        scheduleInfo.executeCount = 0;
        scheduleInfo.isValid = true;
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
        currentTimeMillis = System.currentTimeMillis();
        toRuns.addAll(toAdds);
        toAdds.clear();
        toRuns.forEach(scheduleInfo -> {
            if (!scheduleInfo.isValid) return;
            long deltaTime = currentTimeMillis - scheduleInfo.lastTime;
            long interval = (scheduleInfo.executeCount == 0 ? scheduleInfo.delay : scheduleInfo.interval);
            if (deltaTime >= interval) {
                try {
                    scheduleInfo.runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                scheduleInfo.lastTime = currentTimeMillis;
                scheduleInfo.executeCount++;
                if (scheduleInfo.repeat >= 0) {
                    if (scheduleInfo.executeCount - 1 >= scheduleInfo.repeat) {
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
        long lastTime;
        int executeCount;
        boolean isValid;
    }
}
