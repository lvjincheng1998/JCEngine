package entity;

import pers.jc.engine.JCEntity;
import pers.jc.network.SocketFunction;
import pers.jc.util.JCLogger;

public class Player extends JCEntity {

    @Override
    public void onLoad() {
        JCLogger.info("玩家", this.id, "登录");

        //服务端调用客户端函数
        this.call("testClient", "hello", "i am server", 233);
    }

    @Override
    public void onDestroy() {
        JCLogger.info("玩家", this.id, "退出");
    }

    @SocketFunction
    public void testServer(String a, String b, Integer c) {
        JCLogger.info(a, b, c);
    }
}
