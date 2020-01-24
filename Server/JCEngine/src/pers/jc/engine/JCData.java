package pers.jc.engine;

import net.sf.json.JSONObject;

public class JCData {
	private String funcName;
	private Object args[];
	
	public JCData() {}
	
	public JCData(String funcName, Object[] args) {
		this.funcName = funcName;
		this.args = args;
	}
	
	public String getFuncName() {
		return funcName;
	}
	
	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}
	
	public Object[] getArgs() {
		return args;
	}
	
	public void setArgs(Object[] args) {
		this.args = args;
	}
	
	public static JCData parse(String msg) {
		return (JCData) JSONObject.toBean(JSONObject.fromObject(msg), JCData.class);
	}
	
	public String stringify() {
		return JSONObject.fromObject(this).toString();
	}
}
