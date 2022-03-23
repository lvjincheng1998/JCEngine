package pers.jc.network;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import pers.jc.engine.JCData;
import pers.jc.engine.JCEngine;
import pers.jc.engine.JCEntity;
import pers.jc.netty.WebSocketHandler;
import pers.jc.util.JCLogger;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

public class Dispatcher {
    private static HashMap<String, SocketTarget> socketTargetMap = new HashMap<>();
    private static HashMap<String, Method> entityMethodMap = new HashMap<>();

    public static void addComponent(Class<?> componentClass) throws Exception {
        SocketComponent socketComponent = componentClass.getAnnotation(SocketComponent.class);
        if (socketComponent != null) {
            addSocketComponent(componentClass, socketComponent);
        } else {
            JCLogger.error("No Component For " + componentClass.getName());
        }
    }

    private static void addSocketComponent(Class<?> componentClass, SocketComponent component) throws Exception {
        ComponentLogger componentLogger = new ComponentLogger();
        String componentPath = component.value().isEmpty() ? componentClass.getName() : component.value();
        Object componentInstance = componentClass.newInstance();
        for (Method method : componentClass.getMethods()) {
            SocketMethod socketMethod = method.getAnnotation(SocketMethod.class);
            if (socketMethod != null) {
                String targetPath = componentPath + "." + method.getName();
                if (socketTargetMap.containsKey(targetPath)) {
                    JCLogger.error("Duplicate SocketTargetPath For <" + targetPath + ">");
                    continue;
                }
                socketTargetMap.put(targetPath, new SocketTarget(componentInstance, method));
                componentLogger.addElement("SocketMethod", componentClass, targetPath, method);
            }
        }
        componentLogger.log();
    }

    public static void addEntityMethod(Class<?> entityClass) {
        ComponentLogger logger = new ComponentLogger();
        for (Method method : entityClass.getMethods()) {
            SocketFunction socketFunction = method.getAnnotation(SocketFunction.class);
            if (socketFunction != null) {
                String targetPath = method.getName();
                if (entityMethodMap.containsKey(targetPath)) {
                    JCLogger.error("Duplicate EntityMethodPath For <" + targetPath + ">");
                    continue;
                }
                entityMethodMap.put(targetPath, method);
                logger.addElement("EntityMethod", entityClass, targetPath, method);
            }
        }
        logger.log();
    }

    public static void handleSocketEvent(WebSocketHandler webSocketHandler, JCData data) throws Exception {
        Class<?>[] argTypes = getArgTypes(data.getArgs());
        Method targetMethod = webSocketHandler.getClass().getMethod(data.getFunc(), argTypes);
        if (targetMethod.getAnnotation(SocketEvent.class) == null) {
            JCLogger.error("SocketEvent<" + data.getFunc() + "> Is Not Exist");
            return;
        }
        webSocketHandler.getClass().getMethod(data.getFunc(), argTypes).invoke(webSocketHandler, data.getArgs());
    }

    public static void handleSocketFunction(JCEntity entity, JCData data) throws  Exception {
        if (entity == null || !entity.isValid) {
            JCLogger.error("SocketFunction<" + data.getFunc() + "> Call Fail Because It's Entity Is Not Valid");
            return;
        }
        Method targetMethod = entityMethodMap.get(data.getFunc());
        if (targetMethod == null) {
            JCLogger.error("SocketFunction<" + data.getFunc() + "> Is Not Exist");
            return;
        }
        SocketFunction socketFunction = targetMethod.getAnnotation(SocketFunction.class);
        if (socketFunction.auth() && !entity.authed) {
            JCLogger.error("SocketFunction<" + data.getFunc() + "> Invoke Need Entity Authed");
            return;
        }
        Object[] args = data.getArgs();
        Class<?>[] paramTypes = targetMethod.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            args[i] = convertType(args[i], paramTypes[i]);
        }
        targetMethod.invoke(entity, args);
    }

    public static boolean checkAndAsyncDoneSocketMethod(JCEntity requester, JCData data) {
        SocketTarget socketTarget = socketTargetMap.get(data.getFunc());
        SocketMethod socketMethod = socketTarget.getMethod().getAnnotation(SocketMethod.class);
        if (socketMethod.async()) {
            JCEngine.executorService.execute(() -> {
                try {
                    handleSocketMethod(requester, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        return false;
    }

    public static void handleSocketMethod(JCEntity requester, JCData data) throws Exception {
        if (requester == null || !requester.isValid) {
            JCLogger.error("SocketMethod<" + data.getFunc() + "> Call Fail Because It's Entity Is Not Valid");
            return;
        }
        SocketTarget socketTarget = socketTargetMap.get(data.getFunc());
        if (socketTarget == null) {
            JCLogger.error("SocketMethod<" + data.getFunc() + "> Is Not Exist");
            return;
        }
        SocketMethod socketMethod = socketTarget.getMethod().getAnnotation(SocketMethod.class);
        if (socketMethod.auth() && !requester.authed) {
            JCLogger.error("SocketMethod<" + data.getFunc() + "> Invoke Need Entity Authed");
            return;
        }
        Parameter[] parameters = socketTarget.getMethod().getParameters();
        Object[] castArgs = new Object[parameters.length];
        int argIndex = 0;
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (JCEntity.class.isAssignableFrom(parameter.getType())) {
                castArgs[i] = requester;
            } else if (SocketResponse.class.isAssignableFrom(parameter.getType())) {
                castArgs[i] = new SocketResponse(requester, data);
            } else {
                Object arg = data.getArgs()[argIndex];
                castArgs[i] = convertType(arg, parameter.getType());
                argIndex++;
            }
        }
        socketTarget.getMethod().invoke(socketTarget.getInstance(), castArgs);
    }

    private static Class<?>[] getArgTypes(Object[] args) {
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < argTypes.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        return argTypes;
    }

    private static Object convertType(Object data, Class<?> type) {
        Class dataClass = data.getClass();
        if (dataClass.equals(type)) {
            return data;
        }
        if (dataClass.equals(JSONObject.class) && !type.equals(JSONObject.class)) {
            return ((JSONObject) data).toJavaObject(type);
        }
        if (dataClass.equals(JSONArray.class) && !type.equals(JSONArray.class)) {
            return ((JSONArray) data).toJavaObject(type);
        }
        if (type.equals(double.class) || type.equals(Double.class)) {
            return Double.parseDouble(data.toString());
        }
        if (type.equals(float.class) || type.equals(Float.class)) {
            return Float.parseFloat(data.toString());
        }
        if (type.equals(String.class) && !dataClass.equals(String.class)) {
            return data.toString();
        }
        return data;
    }

    private static class ComponentLogger {
        private StringBuilder stringBuilder = new StringBuilder();

        public void addElement(String methodType, Class<?> componentClass, String targetPath, Method method) {
            String[] parameterNames = new String[method.getParameters().length];
            for (int i = 0; i < parameterNames.length; i++) {
                parameterNames[i] = method.getParameters()[i].getType().getName();
            }
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("Add " + methodType);
            contentBuilder.append(" -Path: " + targetPath);
            contentBuilder.append(" -Target: " + componentClass.getName() + "." + method.getName());
            contentBuilder.append("(" + String.join(", ", parameterNames) + ")");
            if (stringBuilder.length() > 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(contentBuilder);
        }

        public void log() {
            if (stringBuilder.length() > 0) {
                JCLogger.info(stringBuilder.toString());
            }
        }
    }
}