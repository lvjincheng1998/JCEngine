package pers.jc.sql;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.Map;

/**
 * 如果需要开启日志，可修改log4j.properties中的Logger-Level
 */
public class MongodbAccess {
    private final String host;
    private final int port;
    private final String database;
    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    public MongodbAccess(Map<String, Object> config) {
        Object tempValue;

        tempValue = config.get("host");
        host = tempValue == null ? "127.0.0.1" : (String)tempValue;

        tempValue = config.get("port");
        port = tempValue == null ? 27017 : (int)tempValue;

        tempValue = config.get("database");
        database = tempValue == null ? "test" : (String)tempValue;

        mongoClient = new MongoClient(host, port);
        mongoDatabase = mongoClient.getDatabase(database);
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }
}
