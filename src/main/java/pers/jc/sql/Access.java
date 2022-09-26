package pers.jc.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

class Access {
	private final String url;
	private final String username;
	private final String password;
	private final int minIdle;
	private final int maxActive;
	private final int maxAlive;
	protected ConcurrentLinkedQueue<Connection> pool = new ConcurrentLinkedQueue<>();
	protected ConcurrentHashMap<Connection, Long> connCreateTimeMap = new ConcurrentHashMap<>();
	private volatile int activeCount = 0;
	
	protected Access(Map<String, Object> config) {
		Object tempValue;

		tempValue = config.get("driver");
		String driver = (tempValue == null) ? "com.mysql.cj.jdbc.Driver" : (String) tempValue;

		tempValue = config.get("host");
		String host = (tempValue == null) ? "127.0.0.1" : (String) tempValue;

		tempValue = config.get("port");
		int port = (tempValue == null) ? 3306 : (int) tempValue;

		tempValue = config.get("username");
		username = (tempValue == null) ? "root" : (String) tempValue;
		
		tempValue = config.get("password");
		password = (tempValue == null) ? "123456" : (String) tempValue;

		tempValue = config.get("database");
		String database = (tempValue == null) ? "test" : (String) tempValue;
		
		tempValue = config.get("minIdle");
		minIdle = (tempValue == null) ? 5 : (int) tempValue;
		
		tempValue = config.get("maxActive");
		maxActive = (tempValue == null) ? 20 : (int) tempValue;

		tempValue = config.get("maxAlive");
		maxAlive = (tempValue == null) ? 10 * 60 * 1000 : (int) tempValue;
		
		tempValue = config.get("clearInterval");
		long clearInterval = (tempValue == null) ? 3000 : (long) tempValue;
		
		url = "jdbc:mysql://" + host + ':' + port + '/' + database
			+ "?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false";
		
		try {
			Class.forName(driver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				addToPool(pool.poll());
				keepMinIdle();
			}
		}, 0, clearInterval);
	}

	private void keepMinIdle() {
		while (pool.size() < minIdle && activeCount < maxActive) {
			Connection connection = createConnection();
			if (connection == null) break;
			addToPool(connection);
		}
	}
	
	protected Connection getConnection() {
		Connection connection = pool.poll();
		if (connection != null) {
			return connection;
		}
		connection = createConnection();
		if (connection != null) {
			return connection;
		}
		if (minIdle > 0) {
			do {
				connection = pool.poll();
			} while (connection == null);
			return connection;
		}
		return null;
	}
	
	private Connection createConnection() {
		Connection connection = null;
		if(addActiveCount()){
			try {
				connection = DriverManager.getConnection(url, username, password);
			} catch (Exception e) {
				subActiveCount();
				e.printStackTrace();
			}
			if (connection != null) {
				connCreateTimeMap.put(connection, System.currentTimeMillis());
			}
		}
		return connection;
	}
	
	protected void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				subActiveCount();
				connCreateTimeMap.remove(connection);
			}
		}
	}

	private synchronized boolean addActiveCount() {
		if (activeCount < maxActive) {
			activeCount++;
			return true;
		}
		return false;
	}

	private synchronized void subActiveCount() {
		if (activeCount > 0) {
			activeCount--;
		}
	}
	
	protected void addToPool(Connection connection) {
		if (connection == null) return;
		if (minIdle > 0) {
			Long createTime = connCreateTimeMap.get(connection);
			if (createTime != null && System.currentTimeMillis() - createTime < maxAlive) {
				pool.add(connection);
				return;
			}
		}
		closeConnection(connection);
	}
}

