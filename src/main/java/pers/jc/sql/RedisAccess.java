package pers.jc.sql;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Map;
import java.util.function.Consumer;

public class RedisAccess {
    private final String host;
    private final int port;
    private final int timeout;
    private final String password;
    private final int database;
    private final JedisPool jedisPool;

    public RedisAccess(Map<String, Object> config) {
        Object tempValue;

        tempValue = config.get("host");
        host = tempValue == null ? "127.0.0.1" : (String)tempValue;

        tempValue = config.get("port");
        port = tempValue == null ? 6379 : (int)tempValue;

        tempValue = config.get("timeout");
        timeout = tempValue == null ? 2000 : (int)tempValue;

        tempValue = config.get("password");
        password = tempValue == null ? null : (String) tempValue;

        tempValue = config.get("database");
        database = tempValue == null ? 0 : (int)tempValue;

        jedisPool = new JedisPool(new JedisPoolConfig(), host, port, timeout, password, database);
    }

    public void execute(Consumer<Jedis> consumer) {
        Jedis jedis = jedisPool.getResource();
        try {
            consumer.accept(jedis);
        } catch (JedisConnectionException e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }
}
