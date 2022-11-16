package pers.jc.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.function.Consumer;

/**
 * 如果需要开启日志，可修改log4j.properties中的Logger-Level
 */
public class MongoDB {
    private String host = "localhost";
    private int port = 27017;
    private String databaseName = "test";

    public void execute(Consumer<MongoDatabase> consumer) {
        MongoClient mongoClient = new MongoClient( host , port );
        try {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
            consumer.accept(mongoDatabase);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }
}
