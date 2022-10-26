package pers.jc.network;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import pers.jc.engine.JCData;

public class SocketRequest {
    private final JCData data;

    public SocketRequest(JCData data) {
        this.data = data;
    }

    public Object getArg(int index) {
        return data.getArgs()[index];
    }

    public <T> T getArg(int index, Class<T> type) {
        Object data = this.data.getArgs()[index];
        Class<?> dataClass = data.getClass();
        Object arg = data;
        if (dataClass.equals(type)) {
            //默认
        }
        else if (dataClass.equals(JSONObject.class)) {
            arg = ((JSONObject) data).toJavaObject(type);
        }
        else if (dataClass.equals(JSONArray.class)) {
            arg = ((JSONArray) data).toJavaObject(type);
        }
        else if (type.equals(double.class) || type.equals(Double.class)) {
            arg = Double.parseDouble(data.toString());
        }
        else if (type.equals(float.class) || type.equals(Float.class)) {
            arg = Float.parseFloat(data.toString());
        }
        else if (type.equals(String.class)) {
            arg = data.toString();
        }
        return (T) arg;
    }
}
