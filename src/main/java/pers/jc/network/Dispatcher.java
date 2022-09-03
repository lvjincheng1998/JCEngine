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
    private static final HashMap<String, SocketTarget> socketTargetMap = new HashMap<>();
    private static final HashMap<String, Method> entityMethodMap = new HashMap<>();

    public static void registerComponent(Class<?> componentClass) throws Exception {
        SocketComponent socketComponent = componentClass.getAnnotation(SocketComponent.class);
        if (socketComponent != null) {
            ComponentLogger componentLogger = new ComponentLogger();
            String componentPath = socketComponent.value().isEmpty() ? componentClass.getName() : socketComponent.value();
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
        } else {
            JCLogger.error("No Component For " + componentClass.getName());
        }
    }

    public static void registerEntity(Class<?> entityClass) {
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
        targetMethod.invoke(webSocketHandler, data.getArgs());
    }

    public static void handleSocketFunction(JCEntity entity, JCData data) {
        Method targetMethod = entityMethodMap.get(data.getFunc());
        if (targetMethod == null) {
            JCLogger.error("SocketFunction<" + data.getFunc() + "> Is Not Exist");
            return;
        }
        SocketFunction socketFunction = targetMethod.getAnnotation(SocketFunction.class);
        Object[] args = data.getArgs();
        Class<?>[] paramTypes = targetMethod.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            args[i] = convertType(args[i], paramTypes[i]);
        }
        JCEngine.gameService.execute(() -> {
            if (!entity.isValid) {
                JCLogger.error("SocketFunction<" + data.getFunc() + "> Call Fail Because It's Entity Is Not Valid");
                return;
            }
            if (socketFunction.auth() && !entity.authed) {
                JCLogger.error("SocketFunction<" + data.getFunc() + "> Invoke Need Entity Authed");
                return;
            }
            try {
                targetMethod.invoke(entity, args);
            } catch (Exception e) {
                JCLogger.error(e.getMessage());
            }
        });
    }

    public static void handleSocketMethod(JCEntity requester, JCData data) {
        SocketTarget socketTarget = socketTargetMap.get(data.getFunc());
        if (socketTarget == null) {
            JCLogger.error("SocketMethod<" + data.getFunc() + "> Is Not Exist");
            return;
        }
        SocketMethod socketMethod = socketTarget.getMethod().getAnnotation(SocketMethod.class);
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
        JCEngine.gameService.execute(() -> {
            if (!requester.isValid) {
                JCLogger.error("SocketMethod<" + data.getFunc() + "> Call Fail Because It's Entity Is Not Valid");
                return;
            }
            if (socketMethod.auth() && !requester.authed) {
                JCLogger.error("SocketMethod<" + data.getFunc() + "> Invoke Need Entity Authed");
                return;
            }
            if (socketMethod.async()) {
                JCEngine.asyncService.execute(() -> {
                    try {
                        socketTarget.getMethod().invoke(socketTarget.getInstance(), castArgs);
                    } catch (Exception e) {
                        JCLogger.error(e.getMessage());
                    }
                });
                return;
            }
            try {
                socketTarget.getMethod().invoke(socketTarget.getInstance(), castArgs);
            } catch (Exception e) {
                JCLogger.error(e.getMessage());
            }
        });
    }

    private static Class<?>[] getArgTypes(Object[] args) {
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < argTypes.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        return argTypes;
    }

    private static Object convertType(Object data, Class<?> type) {
        Class<?> dataClass = data.getClass();
        if (dataClass.equals(type)) {
            return data;
        }
        if (dataClass.equals(JSONObject.class)) {
            return ((JSONObject) data).toJavaObject(type);
        }
        if (dataClass.equals(JSONArray.class)) {
            return ((JSONArray) data).toJavaObject(type);
        }
        if (type.equals(double.class) || type.equals(Double.class)) {
            return Double.parseDouble(data.toString());
        }
        if (type.equals(float.class) || type.equals(Float.class)) {
            return Float.parseFloat(data.toString());
        }
        if (type.equals(String.class)) {
            return data.toString();
        }
        return data;
    }

    private static class ComponentLogger {
        private final StringBuilder stringBuilder = new StringBuilder();

        public void addElement(String methodType, Class<?> componentClass, String targetPath, Method method) {
            String[] parameterNames = new String[method.getParameters().length];
            for (int i = 0; i < parameterNames.length; i++) {
                parameterNames[i] = method.getParameters()[i].getType().getName();
            }
            String content = "Add ";
            content += methodType;
            content += " -Path: ";
            content += targetPath;
            content += " -Target: ";
            content += componentClass.getName();
            content += ".";
            content += method.getName();
            content += "(";
            content += String.join(", ", parameterNames);
            content += ")";
            if (stringBuilder.length() > 0) {
                content += "\n";
            }
            stringBuilder.append(content);
        }

        public void log() {
            if (stringBuilder.length() > 0) {
                JCLogger.info(stringBuilder.toString());
            }
        }
    }
}