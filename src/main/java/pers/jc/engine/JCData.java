package pers.jc.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JCData {
	public static int TYPE_EVENT = 0;
	public static int TYPE_FUNCTION = 1;
	public static int TYPE_METHOD = 2;
	
	private String uuid;
	private int type;
	private String func;
	private Object[] args;
	
	public JCData() {}
	
	public JCData(String uuid, int type, String func, Object[] args) {
		this.uuid = uuid;
		this.type = type;
		this.func = func;
		this.args = args;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public static JCData parse(String text) {
		return JSON.parseObject(text, JCData.class);
	}
	
	public String stringify() {
		return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
	}
}
