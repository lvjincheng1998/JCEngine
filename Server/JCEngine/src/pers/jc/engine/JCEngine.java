package pers.jc.engine;

import java.util.concurrent.ConcurrentHashMap;
import pers.jc.netty.WebSocketServer;
import pers.jc.util.JCLogger;

public class JCEngine {
	public static Class<? extends JCEntity> entityClass; 
	public static ConcurrentHashMap<Integer, JCEntity> entities = new ConcurrentHashMap<>();
	private static int currentId = -1;
	
	public static void bootQuickly(Class<? extends JCEntity> entityClass) throws Exception {
		JCEngine.boot(9888, "/jce", entityClass);
	}
	
	public static void boot(int port,String path,Class<? extends JCEntity> entityClass) throws Exception {
		JCEngine.entityClass = entityClass;
		JCLogger.info("JCEngine Config Loaded");
		WebSocketServer.run(port, path);
	}
	
	public static synchronized int generateId() {
		currentId++;
		return currentId;
	}
	
	public static JCEntity findEntity(int id) {
		return entities.get(id);
	}
	
	public static void main(String[] args) throws Exception {
		JCEngine.bootQuickly(JCEntity.class);
	}
}
