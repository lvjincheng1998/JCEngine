package pers.jc.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

class Access {
	private final String url;
	private final String username;
	private final String password;
	private final int minIdle;
	private final int maxActive;
	protected ConcurrentLinkedQueue<Connection> pool = new ConcurrentLinkedQueue<>();
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
				closeConnection(pool.poll());
				keepMinIdle();
			}
		}, 0, clearInterval);
	}

	private void keepMinIdle() {
		while (pool.size() < minIdle && activeCount < maxActive) {
			Connection connection = createConnection();
			if (connection != null) {
				addToPool(connection);
			} else {
				break;
			}
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
		if(addActiveCount()){
			try {
				return DriverManager.getConnection(url, username, password);
			} catch (Exception e) {
				subActiveCount();
				e.printStackTrace();
			}
		}
		return null;
	}
	
	protected void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				subActiveCount();
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
		if (minIdle > 0) {
			pool.add(connection);
		} else {
			closeConnection(connection);
		}
	}
}

