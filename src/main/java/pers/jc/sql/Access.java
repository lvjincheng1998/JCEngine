package pers.jc.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

class Access {
	private String driver;
	private String host;
	private int port;
	private String url;
	private String username;
	private String password;
	private String database;
	private int minIdle;
	private int maxActive;
	private long clearInterval;
	protected ConcurrentLinkedQueue<Connection> pool = new ConcurrentLinkedQueue<>();
	private volatile int activeCount = 0;
	
	protected Access(Map<String, Object> config) {
		Object driver = config.get("driver");
		this.driver = (driver == null) ? "com.mysql.cj.jdbc.Driver" : (String) driver;
		
		Object host = config.get("host");
		this.host = (host == null) ? "127.0.0.1" : (String) host;
		
		Object port = config.get("port");
		this.port = (port == null) ? 3306 : (int) port;
		
		Object username = config.get("username");
		this.username = (username == null) ? "root" : (String) username;
		
		Object password = config.get("password");
		this.password = (password == null) ? "123456" : (String) password;
		
		Object database = config.get("database");
		this.database = (database == null) ? "test" : (String) database;
		
		Object minIdle = config.get("minIdle");
		this.minIdle = (minIdle == null) ? 5 : (int) minIdle;
		
		Object maxActive = config.get("maxActive");
		this.maxActive = (maxActive == null) ? 20 : (int) maxActive;
		
		Object clearInterval = config.get("clearInterval");
		this.clearInterval = (clearInterval == null) ? 3000 : (long) clearInterval;
		
		this.url = "jdbc:mysql://" + this.host + ':' + this.port + '/' + this.database
			+ "?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false";
		
		try {
			Class.forName(this.driver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		keepMinIdle();
		
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(this.clearInterval);
					closeConnection(pool.poll());
					keepMinIdle();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
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
			while((connection = pool.poll()) == null){}
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

	private synchronized boolean subActiveCount() {
		if (activeCount > 0) {
			activeCount--;
			return true;
		}
		return false;
	}
	
	protected void addToPool(Connection connection) {
		if (minIdle > 0) {
			pool.add(connection);
		} else {
			closeConnection(connection);
		}
	}
}

