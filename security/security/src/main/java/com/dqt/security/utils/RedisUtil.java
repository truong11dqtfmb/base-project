package com.dqt.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.expire-time}")
    private Long REDIS_EXPIRE_TIME;

    public <T> T get(String key, Class<T> entityClass) {
        try {
            Object o = redisTemplate.opsForValue().get(key);
            ObjectMapper mapper = new ObjectMapper();
            T value = mapper.readValue(o.toString(), entityClass);
            log.info("Get key: {} - value: {}", key, value);
            return value;
        } catch (Exception e) {
            log.error("Exception ", e);
            return null;
        }
    }

    public Object get(String key) {
        String formatKey = formatRedisKey(key);
        try {
            Object object = redisTemplate.opsForValue().get(formatKey);
            log.info("Get key: {} - value: {}", key, object);
            return object;
        } catch (RuntimeException e) {
            log.error("get data from redis key: {} has exception: {}",
                    formatKey, e.getMessage(), e);
            return null;
        }
    }

    public void set(String key, Object object, Long ttl) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(object);
            redisTemplate.opsForValue().set(key, jsonValue, ttl, TimeUnit.SECONDS);
            log.info("Set key: {} - value: {} - with time to live: {}", key, object, ttl);
        } catch (Exception e) {
            log.error("Exception ", e);
        }
    }

    public void set(String key, Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(object);
            redisTemplate.opsForValue().set(key, jsonValue, REDIS_EXPIRE_TIME, TimeUnit.SECONDS);
            log.info("Set key: {} - value: {} - with time to live: {}", key, object, REDIS_EXPIRE_TIME);
        } catch (Exception e) {
            log.error("Exception ", e);
        }
    }

    public void put(String key, Object object, long ttl, TimeUnit timeUnit) {
        String formatKey = formatRedisKey(key);
        try {
            redisTemplate.opsForValue().set(formatKey, object, ttl, timeUnit);
            log.info("Set key: {} - value: {} - time to live: {} - time util: {}", key, object, ttl, timeUnit);
        } catch (RuntimeException e) {
            log.error("put data 2 redis key: {} has exception: {}", formatKey, e.getMessage(), e);
        }
    }

    public Object getContentId(String key) {
        String formatKey = formatRedisKeySimplePush(key);
        try {
            Object contentId = redisTemplate.opsForValue().get(formatKey);
            log.info("Get contentId: {}", contentId);
            return contentId;
        } catch (RuntimeException e) {
            log.error("get data from redis key: {} has exception: {}",
                    formatKey, e.getMessage(), e);
            return null;
        }
    }


    public Boolean lock(String key, String value, long timeout, TimeUnit unit) {
        key = formatRedisKey(key);
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        log.info("Lock key: {}, in {} milliseconds isLock: {}", key, timeout, isLock);
        return isLock;
    }


    public Boolean release(String key) {
        String formatKey = formatRedisKey(key);
        redisTemplate.delete(formatKey);
        return true;
    }

    private String formatRedisKey(String key) {
        return key;
    }

    private String formatRedisKeySimplePush(String key) {
        return key;
    }


    public Boolean hasKey(String key) {
        String formatKey = formatRedisKey(key);
        return redisTemplate.hasKey(formatKey);
    }

    public Long getValueIncrement(String key) {
        String formatKey = formatRedisKey(key);
        RedisAtomicLong counter = new RedisAtomicLong(formatKey, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        log.info("Get value increment: {}", counter.incrementAndGet());
        return counter.get();
    }


    public Long increment(String key) {
        return increment(key, null);
    }

    public Long increment(String key, Long expire) {
        String formatKey = formatRedisKey(key);
        if (expire == null) {
            expire = REDIS_EXPIRE_TIME;
        }

        RedisAtomicLong counter = new RedisAtomicLong(formatKey, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        Long value = counter.incrementAndGet();
        redisTemplate.expire(formatKey, expire, TimeUnit.MILLISECONDS);
        return value;
    }


    public Long decrement(String key) {
        return decrement(key, null);
    }

    public Long decrement(String key, Long expire) {
        String formatKey = formatRedisKey(key);
        RedisAtomicLong counter = new RedisAtomicLong(formatKey, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        Long value = counter.decrementAndGet();
        if (expire == null) {
            expire = REDIS_EXPIRE_TIME;
        }
        redisTemplate.expire(formatKey, expire, TimeUnit.MILLISECONDS);
        return value;
    }


    public void decrement(String... keys) {
        if (keys == null) {
            return;
        }
        for (String key : keys) {
            if (key == null || StringUtils.isEmpty(key)) {
                continue;
            }
            decrement(key);
        }
    }


    public void decrement(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        for (String key : keys) {
            if (key == null || StringUtils.isEmpty(key)) {
                continue;
            }
            decrement(key);
        }
    }

}

