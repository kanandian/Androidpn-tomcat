package org.androidpn.server.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisUtil {

    private static RedisUtil redisUtil = null;

    private JedisPool jedisPool = null;

    private RedisUtil() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //设置最大连接数
        jedisPoolConfig.setMaxTotal(30);
        //设置最大空闲连接数
        jedisPoolConfig.setMaxIdle(10);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "localhost", 6379);
    }

    public static RedisUtil getInstance() {
        if (redisUtil == null) {
            synchronized (RedisUtil.class) {
                if (redisUtil == null) {
                    redisUtil = new RedisUtil();
                }
            }
        }
        return redisUtil;
    }

    public void set(long userId, String userName) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(userName, String.valueOf(userId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(List<Long> userIdList, List<String> userNameList) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            for (int i=0;i<userIdList.size();i++) {
                Long userId = userIdList.get(i);
                String userName = userNameList.get(i);
                jedis.set(userName, String.valueOf(userId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long get(String userName) {
        long userId = 0;
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();

            userId = Long.parseLong(jedis.get(userName));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return userId;
    }

    public List<Long> get(List<String> userNameList) {
        List<Long> userIdList = new ArrayList<Long>(userNameList.size());
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();

            for (String userName : userNameList) {
                long userId = Long.parseLong(jedis.get(userName));
                userIdList.add(userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return userIdList;
    }

    public void clearAll() {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();

            Set<String> keys = jedis.keys("*");
            for (String key : keys) {
                jedis.del(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


}
