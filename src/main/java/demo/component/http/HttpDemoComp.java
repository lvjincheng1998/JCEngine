package demo.component.http;

import demo.entity.result.RequestResult;
import pers.jc.network.HttpComponent;
import pers.jc.network.HttpGet;
import pers.jc.network.HttpPost;
import pers.jc.util.JCLogger;

@HttpComponent("/httpDemo")
public class HttpDemoComp {

    @HttpGet("/test1")
    public String test1(int a, long b, float c, double d) {
        JCLogger.info(a, b, c, d);
        return "test1_ok";
    }

    @HttpPost("/test2")
    public RequestResult test2(Integer a, Long b, Float c, Double d) {
        RequestResult requestResult = new RequestResult();
        JCLogger.info(a, b, c, d);
        requestResult.setMsg("test2_ok");
        return requestResult;
    }
}
