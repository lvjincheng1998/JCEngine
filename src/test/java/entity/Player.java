package entity;

import io.netty.util.internal.ConcurrentSet;
import pers.jc.engine.JCEntity;
import pers.jc.network.SocketFunction;
import pers.jc.util.JCLogger;

/**
 * 玩家实体类 (客户端连接服务端时自动创建)
 */
public class Player extends JCEntity {
    /**玩家实体集合 */
    public static ConcurrentSet<Player> playerSet = new ConcurrentSet<>();

    @Override
    public void onLoad() {
        playerSet.add(this);
        JCLogger.info("玩家", this.id, "登录", "---目前在线人数", playerSet.size());
    }

    @Override
    public void onDestroy() {
        playerSet.remove(this);
        JCLogger.info("玩家", this.id, "退出", "---目前在线人数", playerSet.size());
    }

    public void callAll(String func, Object... args) {
        for (Player player : playerSet) {
            player.call(func, args);
        }
    }

    public void callOthers(String func, Object... args) {
        for (Player player : playerSet) {
            if (player == this) {
                continue;
            }
            player.call(func, args);
        }
    }

    public void callOther(int otherID, String func, Object... args) {
        for (Player player : playerSet) {
            if (player.id == otherID) {
                player.call(func, args);
                break;
            }
        }
    }

    @SocketFunction
    public void test() {
        JCLogger.info("test");
    }
}
