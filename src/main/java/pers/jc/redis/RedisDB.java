package pers.jc.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.function.Consumer;

public class RedisDB {
    private final JedisPool jedisPool;

    public RedisDB() {
        jedisPool = new JedisPool(
                new JedisPoolConfig(),
                "127.0.0.1",
                6379,
                2000,
                null,
                0
        );
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
