package pers.jc.engine;

import pers.jc.netty.WebSocketServer;

public class JCEngine {
	public static Class<? extends JCEntity> entityClass; 
	private static int currentId = -1;
	
	public static void main(String[] args) throws Exception {
		JCEngine.boot(9888, "/jce", JCEntity.class);
	}
	
	public static void boot(int port, String path, Class<? extends JCEntity> entityClass) throws Exception {
		JCEngine.entityClass = entityClass;
		WebSocketServer.run(port, path);
	}
	
	public static synchronized int generateId() {
		currentId++;
		return currentId;
	}
}
