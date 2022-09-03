package pers.jc.netty;

import java.util.*;

public class HeartBeatHandler {
    public static final HeartBeatHandler ins = new HeartBeatHandler();
    private static final long checkInterval = 30 * 1000;
    private final HashSet<WebSocketHandler> wsSet = new HashSet<>();

    private HeartBeatHandler() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                checkHeartBeat();
            }
        }, checkInterval, checkInterval);
    }

    public synchronized void addEntity(WebSocketHandler ws) {
        ws.heartBeatTimeRecord = System.currentTimeMillis();
        wsSet.add(ws);
    }

    private synchronized void checkHeartBeat() {
        long currentTime = System.currentTimeMillis();
        Iterator<WebSocketHandler> iterator = wsSet.iterator();
        WebSocketHandler ws;
        while (iterator.hasNext()) {
            ws = iterator.next();
            if (currentTime - ws.heartBeatTimeRecord > checkInterval) {
                ws.die();
                iterator.remove();
            }
        }
    }
}
