package pers.jc.logic;

public class Director {
    public final Scheduler scheduler = new Scheduler();
    public final RequestHandler requestHandler = new RequestHandler();
    public final CallbackHandler callbackHandler = new CallbackHandler();

    public Director() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
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
