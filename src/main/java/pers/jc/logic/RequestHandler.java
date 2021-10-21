package pers.jc.logic;

import pers.jc.engine.JCData;
import pers.jc.engine.JCEntity;
import pers.jc.network.Dispatcher;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RequestHandler {
    private ConcurrentLinkedQueue<Request> requestQueue = new ConcurrentLinkedQueue();

    public void offerRequest(JCEntity entity, JCData data) {
        Request request = new Request();
        request.entity = entity;
        request.data = data;
        requestQueue.offer(request);
    }

    protected void update() {
        while (true) {
            Request request = requestQueue.poll();
            if (request == null) break;
            try {
                JCEntity entity = request.entity;
                JCData data = request.data;
                if (data.getType() == JCData.TYPE_FUNCTION) {
                    Dispatcher.handleSocketFunction(entity, data);
                } else if (data.getType() == JCData.TYPE_METHOD) {
                    JCData resData = Dispatcher.handleSocketMethod(entity, data);
                    if (resData != null) {
                        entity.channel.writeAndFlush(resData.stringify());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Request {
        JCEntity entity;
        JCData data;
    }
}
