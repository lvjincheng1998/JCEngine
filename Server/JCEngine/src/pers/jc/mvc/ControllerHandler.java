package pers.jc.mvc;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.HashMap;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import pers.jc.engine.JCData;
import pers.jc.engine.JCEntity;
import pers.jc.util.JCLogger;

public class ControllerHandler {
	private static HashMap<String, Class<?>> mapper = new HashMap<>();
	
	public static void init(String controllerPackage) {
		if (controllerPackage == null || controllerPackage.isEmpty()) {
			return;
		}
		for (Class<?> controllerClass : ControllerScanner.getControllerClasses(controllerPackage)) {
			mapper.put(controllerClass.getSimpleName(), controllerClass);
		}
	}
	
	public static JCData handle(JCEntity requestor, JCData data) throws Exception {
		String[] strs = data.getFunc().split("\\.");
		String funcName = strs[1];
		String controllerName = strs[0];
		Class<?> controllerClass = mapper.get(controllerName);
		if (controllerClass == null) {
			JCLogger.error("Controller<" + controllerName + "> is not exist");
			return null;
		}
		Method method = null;
		for (Method m : controllerClass.getMethods()) {
			if (m.getName().equals(funcName)) {
				method = m;
				break;
			}
		}
		if (method == null) {
			JCLogger.error("Controller<" + controllerName + ">.Method<" + funcName +"> is not exist");
			return null;
		}
		Parameter[] parameters = method.getParameters();
		Object[] castArgs = new Object[parameters.length];
		int parameterIndex = 0;
		int argIndex = 0;
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[parameterIndex];
			if (JCEntity.class.isAssignableFrom(parameter.getType())) {
				castArgs[i] = requestor;
			} else {
				Object arg = data.getArgs()[argIndex];
				if (arg.getClass().equals(JSONObject.class)) {
					castArgs[i] = ((JSONObject) arg).toJavaObject(parameter.getType());
				} else if (arg.getClass().equals(JSONArray.class)) {
					castArgs[i] = ((JSONArray) arg).toJavaObject(parameter.getType());
				} else if (
					arg.getClass().equals(BigDecimal.class) &&
					!parameter.getType().equals(BigDecimal.class)	
				) {
					if (
						parameter.getType().equals(double.class) || 
						parameter.getType().equals(Double.class)
					) {
						castArgs[i] = Double.parseDouble(((BigDecimal) arg).toString());
					} else if (
						parameter.getType().equals(float.class) || 
						parameter.getType().equals(Float.class)
					) {
						castArgs[i] = Float.parseFloat(((BigDecimal) arg).toString());
					} else {
						throw new Exception("argument doesn't match type <BigDecimal>");
					}
				} else {
					castArgs[i] = arg;
				}
				argIndex++;
			}
			parameterIndex++;
		}
		Object arg0 = method.invoke(controllerClass.newInstance(), castArgs);
		return new JCData(data.getUuid(), data.getType(), data.getFunc(), new Object[]{arg0});
	}
}
