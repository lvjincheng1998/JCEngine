package pers.jc.netty;

import java.util.*;

public class HeartBeatHandler {
    public static final HeartBeatHandler ins = new HeartBeatHandler();
    private static final long checkInterval = 30 * 1000;
    private final HashSet<WebSocketHandler> wsSet = new HashSet<>();
    private final Deque<WebSocketHandler> wsRemoveQueue = new LinkedList<>();

    private HeartBeatHandler() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                checkHeartBeat();
            }
        }, checkInterval, checkInterval);
    }

    public synchronized void addEntity(WebSocketHandler ws) {
        wsSet.add(ws);
    }

    private synchronized void checkHeartBeat() {
        long currentTime = System.currentTimeMillis();
        for (WebSocketHandler ws: wsSet) {
            if (currentTime - ws.heartBeatTimeRecord > checkInterval) {
                ws.die();
                wsRemoveQueue.addLast(ws);
            }
        }
        while (!wsRemoveQueue.isEmpty()) {
            wsSet.remove(wsRemoveQueue.removeFirst());
        }
    }
}
