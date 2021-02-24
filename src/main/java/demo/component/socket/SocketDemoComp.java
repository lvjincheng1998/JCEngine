package demo.component.socket;

import com.alibaba.fastjson.JSONObject;
import demo.entity.Player;
import demo.entity.result.RequestResult;
import pers.jc.network.SocketComponent;
import pers.jc.network.SocketMethod;
import pers.jc.util.JCLogger;

@SocketComponent("socketDemo")
public class SocketDemoComp {

    @SocketMethod
    public String test1(Player player, String a) {
        JCLogger.info(player.id, a);
        return "test1_ok";
    }

    @SocketMethod
    public RequestResult test2(Player player, JSONObject a) {
        RequestResult requestResult = new RequestResult();
        JCLogger.info(player.id, a);
        requestResult.setMsg("test2_ok");
        return requestResult;
    }

    @SocketMethod
    public String test3(int a, long b, float c, double d) {
        JCLogger.info(a, b, c, d);
        return "test3_ok";
    }

    @SocketMethod
    public RequestResult test4(Integer a, Long b, Float c, Double d) {
        RequestResult requestResult = new RequestResult();
        JCLogger.info(a, b, c, d);
        requestResult.setMsg("test4_ok");
        return requestResult;
    }
}
