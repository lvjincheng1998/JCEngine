package pers.jc.engine;

import net.sf.json.JSONObject;

public class JCData {
	public static int TYPE_EVENT = 0;
	public static int TYPE_FUNCTION = 1;
	
	private int id;
	private int type;
	private String func;
	private Object args[];
	
	public JCData() {}
	
	public JCData(int id, int type, String func, Object[] args) {
		this.id = id;
		this.type = type;
		this.func = func;
		this.args = args;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		return (JCData) JSONObject.toBean(JSONObject.fromObject(text), JCData.class);
	}
	
	public String stringify() {
		return JSONObject.fromObject(this).toString();
	}
}
