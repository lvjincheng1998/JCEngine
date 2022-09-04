package pers.jc.engine;

import org.reflections.Reflections;
import pers.jc.netty.WebSocketServer;
import pers.jc.network.Dispatcher;
import pers.jc.network.SocketComponent;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class JCEngine {
	/**
	 * 异步服务-线程池
	 * 一般用来执行耗时的任务，比如操作文件、操作数据库等
	 * 该服务负责执行的任务有：SocketMethod(async=true)
	 */
	public static final ExecutorService asyncService = Executors.newCachedThreadPool();
	/**
	 * 游戏服务-单线程
	 * 用于执行游戏逻辑，无需关心线程安全问题
	 * 该服务负责执行的任务有：SocketFunction, SocketMethod(async=false)
	 */
	public static final ExecutorService gameService = Executors.newSingleThreadExecutor();
	/**
	 * 计时器
	 * 逻辑部分在gameService中执行
	 */
	public static final JCScheduler scheduler = new JCScheduler();
	/**自定义的玩家通信实体类 */
	public static Class<? extends JCEntity> entityClass;
	/**传输内容最大长度（字节） */
	public static int maxContentLength = 64 * 1024;
	/**
	 * JCEntity默认是否已认证（默认Yes）
	 * JCEntity认证后才能调用服务端的方法
	 */
	public static boolean defaultAuthValue = true;

	public static void registerComponent(Class<?> componentClass) {
		try {
			Dispatcher.registerComponent(componentClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void scanPackage(String targetPackage) {
		Reflections reflections = new Reflections(targetPackage);
		Set<Class<?>> socketComponents = reflections.getTypesAnnotatedWith(SocketComponent.class);
		for (Class<?> socketComponent : socketComponents) {
			registerComponent(socketComponent);
		}
	}

	public static void boot(int port, String path, Class<? extends JCEntity> entityClass) {
		try {
			JCEngine.entityClass = entityClass;
			Dispatcher.registerEntity(entityClass);
			WebSocketServer.run(port, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final HashMap<Object, Integer> autoIncrementIDs = new HashMap<>();
	private static final ReentrantLock autoIncrementIDsLock = new ReentrantLock();

	/**
	 * 获取自增ID
	 * @param key 类型键
	 * @return 自增ID
	 */
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

	/**
	 * 删除自增ID
	 * @param keys 类型键（可填多个）
	 */
	public static void delAutoIncrementID(Object... keys) {
		autoIncrementIDsLock.lock();
		for (Object key : keys) autoIncrementIDs.remove(key);
		autoIncrementIDsLock.unlock();
	}
}
