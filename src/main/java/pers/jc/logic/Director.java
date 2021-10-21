package pers.jc.logic;

import java.util.Timer;
import java.util.TimerTask;

public class Director {
    public final Scheduler scheduler = new Scheduler();
    public final RequestHandler requestHandler = new RequestHandler();
    public final CallbackHandler callbackHandler = new CallbackHandler();

    public Director() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loopCheck();
            }
        }).start();
    }

    private void loopCheck() {
        scheduler.update();
        requestHandler.update();
        callbackHandler.update();
    }
}
