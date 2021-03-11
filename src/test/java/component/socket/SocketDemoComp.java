package component.socket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import entity.Player;
import pers.jc.network.SocketComponent;
import pers.jc.network.SocketMethod;
import pers.jc.util.JCLogger;

@SocketComponent("socketDemo")
public class SocketDemoComp {

    @SocketMethod
    public String test(
        Player player, // 调用者实体对象，可选接收
        String a, int b, long c, float d, double e, boolean f, JSONObject g, JSONArray h
    ) {
        JCLogger.info(player.id, a, b, c, d, e, f, g, h);
        return "可返回任意类型数据";
    }
}
