package pers.jc.network;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.multipart.FileUpload;
import pers.jc.engine.JCData;
import pers.jc.engine.JCEntity;
import pers.jc.netty.WebSocketHandler;
import pers.jc.util.JCLogger;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.HashMap;

public class Dispatcher {
    private static HashMap<String, HttpTarget> httpTargetMap = new HashMap<>();
    private static HashMap<String, SocketTarget> socketTargetMap = new HashMap<>();
    private static HashMap<String, Method> entityMethodMap = new HashMap<>();

    public static void addComponent(Class<?> componentClass) throws Exception {
        HttpComponent httpComponent = componentClass.getAnnotation(HttpComponent.class);
        SocketComponent socketComponent = componentClass.getAnnotation(SocketComponent.class);
        if (httpComponent != null && socketComponent == null) {
            addHttpComponent(componentClass, httpComponent);
            return;
        }
        if (socketComponent != null && httpComponent == null) {
            addSocketComponent(componentClass, socketComponent);
            return;
        }
        if (socketComponent != null && httpComponent != null) {
            JCLogger.error("Duplicate Component For " + componentClass.getName());
            return;
        }
        if (socketComponent == null && httpComponent == null) {
            JCLogger.error("No Component For " + componentClass.getName());
            return;
        }
    }

    private static void addHttpComponent(Class<?> componentClass, HttpComponent component) throws Exception {
        ComponentLogger componentLogger = new ComponentLogger();
        Object componentInstance = componentClass.newInstance();
        for (Method method : componentClass.getMethods()) {
            HttpGet httpGet = method.getAnnotation(HttpGet.class);
            HttpPost httpPost = method.getAnnotation(HttpPost.class);
            String targetPath = null;
            HttpType httpType = null;
            if (httpGet != null) {
                targetPath = component.value() + httpGet.value();
                httpType = HttpType.GET;
            } else if (httpPost != null) {
                targetPath = component.value() + httpPost.value();
                httpType = HttpType.POST;
            }
            if (targetPath != null && httpType != null) {
                if (httpTargetMap.containsKey(targetPath)) {
                    JCLogger.error("Duplicate HttpTargetPath For <" + targetPath + ">");
                    continue;
                }
                httpTargetMap.put(targetPath, new HttpTarget(componentInstance, method, httpType));
                componentLogger.addElement("HttpMethod", componentClass, targetPath, method, httpType);
            }
        }
        componentLogger.log();
    }

    private static void addSocketComponent(Class<?> componentClass, SocketComponent component) throws Exception {
        ComponentLogger componentLogger = new ComponentLogger();
        String componentPath = component.value().isEmpty() ? componentClass.getName() : component.value();
        Object componentInstance = componentClass.newInstance();
        for (Method method : componentClass.getMethods()) {
            SocketMethod socketMethod = method.getAnnotation(SocketMethod.class);
            if (socketMethod != null) {
                String targetPath = componentPath + "." + method.getName();
                if (httpTargetMap.containsKey(targetPath)) {
                    JCLogger.error("Duplicate SocketTargetPath For <" + targetPath + ">");
                    continue;
                }
                socketTargetMap.put(targetPath, new SocketTarget(componentInstance, method));
                componentLogger.addElement("SocketMethod", componentClass, targetPath, method, null);
            }
        }
        componentLogger.log();
    }

    public static void addEntityMethod(Class<?> entityClass) throws Exception {
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
                logger.addElement("EntityMethod", entityClass, targetPath, method, null);
            }
        }
        logger.log();
    }

    public static Object handleHttpRequest(HttpRequest httpRequest) throws Exception {
        HttpTarget httpTarget = httpTargetMap.get(httpRequest.getUri());
        if (httpTarget == null) {
            JCLogger.error("HttpRequest<" + httpRequest.getUri() + "> Not Match");
            return HttpRequest.URI_NOT_MATCH;
        }
        if (httpRequest.getHttpType() != httpTarget.getHttpType()) {
            JCLogger.error("HttpType<" + httpRequest.getUri() + "> Not Match");
            return HttpRequest.TYPE_NOT_MATCH;
        }
        Parameter[] parameters = httpTarget.getMethod().getParameters();
        Object[] castArgs = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (HttpRequest.class.isAssignableFrom(parameter.getType())) {
                castArgs[i] = httpRequest;
                continue;
            } else {
                Object arg = httpRequest.getParamMap().get(httpTarget.getParameterNames()[i]);
                if (parameter.getType() == String.class) {
                    castArgs[i] = String.valueOf(arg);
                } else if (parameter.getType() == int.class || parameter.getType() == Integer.class) {
                    castArgs[i] = Integer.valueOf((String) arg);
                } else if (parameter.getType() == long.class || parameter.getType() == Long.class) {
                    castArgs[i] = Long.valueOf((String) arg);
                } else if (parameter.getType() == float.class || parameter.getType() == Float.class) {
                    castArgs[i] = Float.valueOf((String) arg);
                } else if (parameter.getType() == double.class || parameter.getType() == Double.class) {
                    castArgs[i] = Double.valueOf((String) arg);
                } else if (parameter.getType() == boolean.class || parameter.getType() == Boolean.class) {
                    castArgs[i] = Boolean.valueOf((String) arg);
                } else if (parameter.getType() == BigDecimal.class) {
                    castArgs[i] = new BigDecimal((String) arg);
                } else if (parameter.getType() == FileUpload.class) {
                    castArgs[i] = arg;
                }
            }
        }
        return httpTarget.getMethod().invoke(httpTarget.getInstance(), castArgs);
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
//        Class<?>[] argTypes = getArgTypes(data.getArgs());
//        Method targetMethod = entity.getClass().getMethod(data.getFunc(), argTypes);
//        if (targetMethod.getAnnotation(SocketFunction.class) == null) {
//            JCLogger.error("SocketFunction<" + data.getFunc() + "> Is Not Exist");
//            return;
//        }
//        entity.getClass().getMethod(data.getFunc(), argTypes).invoke(entity, data.getArgs());
        Method targetMethod = entityMethodMap.get(data.getFunc());
        if (targetMethod == null) {
            JCLogger.error("SocketFunction<" + data.getFunc() + "> Is Not Exist");
            return;
        }
        Object[] args = data.getArgs();
        Class<?>[] paramTypes = targetMethod.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            args[i] = convertType(args[i], paramTypes[i]);
        }
        targetMethod.invoke(entity, args);
    }

    public static JCData handleSocketMethod(JCEntity requester, JCData data) throws Exception {
        if (requester == null || !requester.isValid) {
            JCLogger.error("SocketMethod<" + data.getFunc() + "> Call Fail Because It's Entity Is Not Valid");
            return null;
        }
        SocketTarget socketTarget = socketTargetMap.get(data.getFunc());
        if (socketTarget == null) {
            JCLogger.error("SocketMethod<" + data.getFunc() + "> Is Not Exist");
            return null;
        }
        Parameter[] parameters = socketTarget.getMethod().getParameters();
        Object[] castArgs = new Object[parameters.length];
        int argIndex = 0;
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (JCEntity.class.isAssignableFrom(parameter.getType())) {
                castArgs[i] = requester;
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
                        castArgs[i] = Double.parseDouble(arg.toString());
                    } else if (
                        parameter.getType().equals(float.class) ||
                        parameter.getType().equals(Float.class)
                    ) {
                        castArgs[i] = Float.parseFloat(arg.toString());
                    } else {
                        throw new Exception("Argument Doesn't Match Type <BigDecimal>");
                    }
                } else {
                    castArgs[i] = arg;
                }
                argIndex++;
            }
        }
        Object arg0 = socketTarget.getMethod().invoke(socketTarget.getInstance(), castArgs);
        return new JCData(data.getUuid(), data.getType(), data.getFunc(), new Object[]{arg0});
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

        public void addElement(String methodType, Class<?> componentClass, String targetPath, Method method, HttpType httpType) {
            String[] parameterNames = new String[method.getParameters().length];
            for (int i = 0; i < parameterNames.length; i++) {
                parameterNames[i] = method.getParameters()[i].getType().getName();
            }
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("Add " + methodType);
            contentBuilder.append(" -Path: " + targetPath);
            contentBuilder.append(" -Target: " + componentClass.getName() + "." + method.getName());
            contentBuilder.append("(" + String.join(", ", parameterNames) + ")");
            if (httpType != null) {
                contentBuilder.append(" -Type: " + (httpType == HttpType.GET ? "GET" : "POST"));
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(contentBuilder);
        }

        public void log() {
            JCLogger.info(stringBuilder.toString());
        }
    }
}