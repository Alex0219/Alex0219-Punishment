package de.alex0219.punishment.db;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedisPipeline;

import java.time.Duration;

public class RedisConnector {

    public JedisPool jedisPool;
    public ShardedJedisPipeline pipeline;

    public RedisConnector() {
        buildPoolConfig();
    }

    /**
     * Connects to the redis database with host and port.
     *
     * @param host
     * @param port
     */
    public void connectToRedis(String host, int port) {
        jedisPool = new JedisPool(host, port);
        System.out.println("Connected to redis server at " + host + ":" + port + " using password: no");
    }

    /**
     * Connects to the redis database with host, port and password.
     *
     * @param host
     * @param port
     * @param password
     */
    public void connectToRedis(String host, int port, String password) {
        jedisPool = new JedisPool(host, port);
        jedisPool.getResource().auth(password);
        System.out.println("Connected to redis server at " + host + ":" + port + " using password: yes");
    }

    /**
     * Returns a @{@link Jedis} instance from the Jedis pool.
     *
     * @return
     */
    public Jedis getJedis() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis;
        } finally {
            jedisPool.close();
        }
    }

    /**
     * Builds up the Jedis pool config. The pool balances the system's resources.
     *
     * @return
     */
    private JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }
}
