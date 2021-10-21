package pers.jc.logic;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CallbackHandler {
    private ConcurrentLinkedQueue<Runnable> callbacks = new ConcurrentLinkedQueue<>();

    public void offerCallback(Runnable runnable) {
        callbacks.offer(runnable);
    }

    protected void update() {
        while (true) {
            Runnable runnable = callbacks.poll();
            if (runnable == null) break;
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
