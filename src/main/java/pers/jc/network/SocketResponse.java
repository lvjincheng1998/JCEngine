package pers.jc.network;

import pers.jc.engine.JCData;
import pers.jc.engine.JCEntity;

public class SocketResponse {
    private JCEntity entity;
    private JCData data;

    public SocketResponse(JCEntity entity, JCData data) {
        this.entity = entity;
        this.data = data;
    }

    public void send(Object... args) {
        JCData response = new JCData(data.getUuid(), data.getType(), data.getFunc(), args);
        entity.channel.writeAndFlush(response.stringify());
    }
}
