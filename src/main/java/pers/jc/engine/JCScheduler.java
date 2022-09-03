package pers.jc.engine;

import java.util.*;

/**
 * 计时器（多线程安全）
 * 任务逻辑运行在游戏线程即单线程中，如果其中有耗时的任务请一定要异步处理
 */
public class JCScheduler {
    private final Queue<ScheduleTask> taskQueue = new PriorityQueue<>((a, b) -> (int) (a.nextRunTime - b.nextRunTime));

    private long currentTime;
    private long nextHandleTime;

    private final Object serviceWaiter = new Object();
    private final Object handleWaiter = new Object();

    private static class ScheduleTask {
        Runnable runnable;
        long interval;
        int repeat;
        long delay;
        long nextRunTime;
        int executeCount;
        boolean isValid;

        void updateNextRunTime() {
            nextRunTime += executeCount == 0 ? delay : interval;
        }
    }

    protected JCScheduler() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    synchronized (serviceWaiter) {
                        JCEngine.gameService.execute(this::handle);
                        serviceWaiter.wait();
                    }
                    synchronized (handleWaiter) {
                        currentTime = System.currentTimeMillis();
                        if (nextHandleTime > 0) {
                            long needWaitTime = nextHandleTime - currentTime;
                            if (needWaitTime > 0) {
                                handleWaiter.wait(needWaitTime);
                            }
                        } else {
                            handleWaiter.wait();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setName(JCScheduler.class.getSimpleName());
        thread.setDaemon(true);
        thread.start();
    }

    private void handle() {
        ScheduleTask task;
        while (true) {
            synchronized (handleWaiter) {
                task = taskQueue.peek();
                if (task == null) break;
                currentTime = System.currentTimeMillis();
                if (currentTime < task.nextRunTime) break;
                task.runnable.run();
                task.updateNextRunTime();
                if (task.repeat >= 0 && task.executeCount > task.repeat) {
                    task.isValid = false;
                }
                if (taskQueue.remove(task) && task.isValid) {
                    taskQueue.add(task);
                }
            }
        }
        synchronized (handleWaiter) {
            ScheduleTask headTask = taskQueue.peek();
            if (headTask != null) {
                nextHandleTime = headTask.nextRunTime;
            } else {
                nextHandleTime = -1;
            }
        }
        synchronized (serviceWaiter) {
            serviceWaiter.notify();
        }
    }

    /**
     * 添加计时器任务（只执行一次）
     * @param runnable 任务逻辑
     * @param delay 延迟多久触发
     */
    public void once(Runnable runnable, long delay) {
        schedule(runnable, 0, 0, delay, null);
    }

    /**
     * 添加计时器任务（循环执行）
     * @param runnable 任务逻辑
     * @param interval 触发时间间隔（毫秒）
     */
    public void loop(Runnable runnable, long interval) {
        schedule(runnable, interval, -1, interval, null);
    }

    /**
     * 添加计时器任务
     * @param runnable 任务逻辑
     * @param interval 触发时间间隔（毫秒）
     * @param repeat  重复执行次数。比如值为99则执行任务100次，值为-1则无限次数执行任务
     * @param delay 首次触发需要延迟多久
     * @param date 开始时间。null则默认当前时间
     */
    public void schedule(Runnable runnable, long interval, int repeat, long delay, Date date) {
        synchronized (handleWaiter) {
            ScheduleTask scheduleTask = new ScheduleTask();
            scheduleTask.runnable = runnable;
            scheduleTask.interval = interval;
            scheduleTask.repeat = repeat;
            scheduleTask.delay = delay;
            scheduleTask.executeCount = 0;
            scheduleTask.isValid = true;
            scheduleTask.nextRunTime = date == null ? System.currentTimeMillis() : date.getTime();
            scheduleTask.updateNextRunTime();
            taskQueue.offer(scheduleTask);
            handleWaiter.notify();
        }
    }

    /**
     * 取消计时器任务
     * @param runnable 任务逻辑
     */
    public void cancel(Runnable runnable) {
        synchronized (handleWaiter) {
            Iterator<ScheduleTask> iterator = taskQueue.iterator();
            ScheduleTask scheduleTask;
            while (iterator.hasNext()) {
                scheduleTask = iterator.next();
                if (scheduleTask.runnable == runnable) {
                    iterator.remove();
                }
            }
            handleWaiter.notify();
        }
    }
}
