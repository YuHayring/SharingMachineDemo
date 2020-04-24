package cn.hayring.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class RedisMap implements Map<String, Serializable> {

    private static RedisTemplate redisTemplate;

    private static HashOperations operations;

    private final String keyInRedis;


    public RedisMap(String key) {
        this.keyInRedis = key;
    }

    @Override
    public int size() {
        return operations.size(keyInRedis).intValue();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return operations.hasKey(this.keyInRedis, key);
    }

    @Override
    @Deprecated
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Serializable get(Object key) {
        return (Serializable) operations.get(this.keyInRedis, key);
    }

    @Override
    public Serializable put(String key, Serializable value) {
        Serializable oldVal = null;
        if (operations.hasKey(this.keyInRedis, key)) {
            oldVal = (Serializable) operations.get(this.keyInRedis, key);
        }
        operations.put(this.keyInRedis, key, value);
        return oldVal;
    }

    @Override
    public Serializable remove(Object key) {
        return null;//(Long) operations.delete(this.keyInRedis, key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Serializable> m) {
        operations.putAll(this.keyInRedis, m);
    }

    @Override
    public void clear() {
        redisTemplate.delete(keyInRedis);
    }

    @Override
    public Set<String> keySet() {
        return operations.keys(keyInRedis);
    }

    @Override
    public Collection<Serializable> values() {
        return operations.values(keyInRedis);
    }

    @Override
    @Deprecated
    public Set<Entry<String, Serializable>> entrySet() {
        return null;
    }


    @Autowired
    public static void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisMap.redisTemplate = redisTemplate;
        operations = redisTemplate.opsForHash();
    }
}
