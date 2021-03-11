package component.http;

import entity.result.RequestResult;
import pers.jc.network.HttpComponent;
import pers.jc.network.HttpGet;
import pers.jc.network.HttpPost;
import pers.jc.util.JCLogger;

@HttpComponent("/httpDemo")
public class HttpDemoComp {

    @HttpGet("/test1")
    public String test1(String text) {
        JCLogger.info(text);
        return "返回内容";
    }

    @HttpPost("/test2")
    public RequestResult test2(int a, long b, float c, double d, boolean e) {
        JCLogger.info(a, b, c, d, e);
        //返回一个对象，最终序列化为JSON对象
        RequestResult requestResult = new RequestResult();
        requestResult.setCode(0);
        requestResult.setData("内容");
        requestResult.setMsg("消息");
        return requestResult;
    }
}
