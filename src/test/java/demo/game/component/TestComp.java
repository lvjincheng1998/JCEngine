package demo.game.component;

import com.alibaba.fastjson.JSONObject;
import demo.game.Player;
import pers.jc.network.SocketComponent;
import pers.jc.network.SocketMethod;
import pers.jc.network.SocketResponse;
import pers.jc.util.JCLogger;

import java.util.Arrays;

@SocketComponent("testComp")
public class TestComp {

    @SocketMethod
    public void test1(Player player, SocketResponse response, String p1, int p2, JSONObject p3, int[] p4) {
        JCLogger.info("客户端调用了test1", "线程ID:" + Thread.currentThread().getId(),
                "玩家id =", player.id, p1, p2, p3, Arrays.toString(p4));
        response.send("a", 1);
    }

    @SocketMethod(async = true)
    public void test2(Player player, SocketResponse response) {
        JCLogger.info("客户端调用了test2（异步处理）", "线程ID:" + Thread.currentThread().getId(),
                "玩家id =", player.id);
        response.send("b", 2);
    }

    @SocketMethod
    public void test3() {
        JCLogger.info("客户端调用了test3", "线程ID:" + Thread.currentThread().getId());
    }
}
