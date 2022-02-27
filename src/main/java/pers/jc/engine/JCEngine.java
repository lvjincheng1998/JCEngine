package pers.jc.engine;

import org.reflections.Reflections;
import pers.jc.logic.Director;
import pers.jc.netty.WebSocketServer;
import pers.jc.network.Dispatcher;
import pers.jc.network.SocketComponent;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class JCEngine {
	public static final ExecutorService executorService = Executors.newCachedThreadPool();
	public static final Director director = new Director();
	public static Class<? extends JCEntity> entityClass;
	/**传输内容最大长度 */
	public static int maxContentLength = 64 * 1024;

	public static void addComponent(Class<?> componentClass) {
		try {
			Dispatcher.addComponent(componentClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void scanPackage(String targetPackage) {
		Reflections reflections = new Reflections(targetPackage);
		Set<Class<?>> socketComponents = reflections.getTypesAnnotatedWith(SocketComponent.class);
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

	private static HashMap<Object, Integer> autoIncrementIDs = new HashMap<>();
	private static ReentrantLock autoIncrementIDsLock = new ReentrantLock();

	public static int getAutoIncrementID(Object key) {
		autoIncrementIDsLock.lock();
		Integer autoIncrementID = autoIncrementIDs.get(key);
		if (autoIncrementID == null) {
			autoIncrementID = 1;
		} else {
			autoIncrementID++;
		}
		autoIncrementIDs.put(key, autoIncrementID);
		autoIncrementIDsLock.unlock();
		return autoIncrementID;
	}

	public static void removeKeyOfAutoIncrementID(Object... keys) {
		autoIncrementIDsLock.lock();
		for (Object key : keys) autoIncrementIDs.remove(key);
		autoIncrementIDsLock.unlock();
	}

	public static void main(String[] args) {}
}
