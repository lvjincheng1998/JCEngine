package pers.jc.engine;

import org.reflections.Reflections;
import pers.jc.netty.WebSocketServer;
import pers.jc.network.Dispatcher;
import pers.jc.network.HttpComponent;
import pers.jc.network.SocketComponent;
import pers.jc.network.SocketMethod;

import java.util.Set;

public class JCEngine {
	public static Class<? extends JCEntity> entityClass;

	public static void addComponent(Class<?> componentClass) {
		try {
			Dispatcher.addComponent(componentClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void scanPackage(String targetPackage) {
		Reflections reflections = new Reflections(targetPackage);
		Set<Class<?>> httpComponents = reflections.getTypesAnnotatedWith(HttpComponent.class);
		Set<Class<?>> socketComponents = reflections.getTypesAnnotatedWith(SocketComponent.class);
		for (Class<?> httpComponent : httpComponents) {
			addComponent(httpComponent);
		}
		for (Class<?> socketComponent : socketComponents) {
			addComponent(socketComponent);
		}
	}

	public static void boot(int port, String path, Class<? extends JCEntity> entityClass) {
		try {
			JCEngine.entityClass = entityClass;
			Dispatcher.addEntityMethod(entityClass);
			WebSocketServer.run(port, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int autoIncrementID;

	public static synchronized int getAutoIncrementID() {
		return ++autoIncrementID;
	}

	public static void main(String[] args) {
		//打包需要一个启动类
	}
}
