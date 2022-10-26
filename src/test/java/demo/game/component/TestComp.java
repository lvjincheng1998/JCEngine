package demo.game.component;

import com.alibaba.fastjson.JSONObject;
import demo.game.Player;
import pers.jc.network.SocketComponent;
import pers.jc.network.SocketMethod;
import pers.jc.network.SocketRequest;
import pers.jc.network.SocketResponse;
import pers.jc.util.JCLogger;

import java.util.Arrays;

@SocketComponent("testComp")
public class TestComp {

    @SocketMethod
    public void test1(Player player, SocketResponse response, String p1, int p2, JSONObject p3, int[] p4) {
        JCLogger.info("test1函数被客户端调用", "线程ID:" + Thread.currentThread().getId(),
                "id =", player.id, "\n收到的参数:", p1, p2, p3, Arrays.toString(p4));
        response.send("a", 1);
    }

    @SocketMethod
    public void test2(Player player, SocketResponse response, SocketRequest request) {
        String p1 = request.getArg(0, String.class);
        int p2 = request.getArg(1, Integer.class);
        JSONObject p3 = request.getArg(2, JSONObject.class);
        int[] p4 = request.getArg(3, int[].class);
        JCLogger.info("test2函数被客户端调用", "线程ID:" + Thread.currentThread().getId(),
                "id =", player.id, "\n收到的参数:", p1, p2, p3, Arrays.toString(p4));
        response.send("a", 1);
    }
}
