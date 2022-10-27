package demo.game;

import pers.jc.engine.JCEntity;
import pers.jc.network.SocketFunction;
import pers.jc.util.JCLogger;

import java.util.HashMap;
import java.util.Map;

public class Player extends JCEntity {
    Map<Integer, Player> playerMap = new HashMap<>();

    @Override
    public void onLoad() {
        JCLogger.info("玩家登录", "线程ID:" + Thread.currentThread().getId(), "id =", id);
        playerMap.put(id, this);
        JCLogger.info("在线玩家数量 =", playerMap.size());

        //调用客户端的方法(args可传多个参数)
        call("showMessage1", "msg from server");
        call("showMessage2", "msg from server", 12345);
        call("TestComp.doSomeThing", "test do some thing");
    }

    @Override
    public void onDestroy() {
        JCLogger.info("玩家退出", "线程ID:" + Thread.currentThread().getId(), "id =", id);
        playerMap.remove(id, this);
        JCLogger.info("在线玩家数量 =", playerMap.size());
    }

    @SocketFunction
    public void hello(int a, long b, float c, double d, boolean e, String f) {
        JCLogger.info("hello函数被客户端调用", "线程ID:" + Thread.currentThread().getId(), "id =", id,
                "\n收到的参数:", a, b, c, d, e, f);
    }
}
