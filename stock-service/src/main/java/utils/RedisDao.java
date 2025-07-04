package utils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedisTemplateLockExecutor;

import cn.hutool.json.JSONUtil;
import config.RedisDbTypeEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisDao {

	@Resource
    @Qualifier("defaultRedisTemplate")
    private RedisTemplate<String, Object> defaultRedisTemplate;
    
	@Resource
    @Qualifier("ipRedisTemplate")
    private RedisTemplate<String, Object> ipRedisTemplate;
    
	@Resource
    @Qualifier("stockARedisTemplate")
    private RedisTemplate<String, Object> stockARedisTemplate;
    
	@Resource
    @Qualifier("stockHkRedisTemplate")
    private RedisTemplate<String, Object> stockHkRedisTemplate;
    
	@Resource
    @Qualifier("stockUsRedisTemplate")
    private RedisTemplate<String, Object> stockUsRedisTemplate;
	
	@Resource
    @Qualifier("stockApiRedisTemplate")
    private RedisTemplate<String, Object> stockApiRedisTemplate;

    @Value("${lock4j.expire}")
    private long expire;

    @Value("${lock4j.acquire-timeout}")
    private long timeout;

    @Value("${lock4j.lock-key-prefix}")
    private String keyPrefix;

    public boolean set(RedisDbTypeEnum type, String key, String string) {
    	RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate(type);
        try {
            redisTemplate.opsForValue().set(key, string);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    // 存一个对象
    public boolean setString(RedisDbTypeEnum type, String key, String string) {
    	RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate(type);
        try {
            redisTemplate.opsForValue().set(key, string);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    // 存一个对象,并设置生命周期
    public boolean setString(RedisDbTypeEnum type, String key, String string, long time, TimeUnit timeUnit) {
    	RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate(type);
        try {
            redisTemplate.opsForValue().set(key, string, time, timeUnit);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    // 存一个对象
    public boolean setBean(RedisDbTypeEnum type, String key, Object value) {
    	RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate(type);
        try {
        	redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    // 存一个对象
    public boolean setBean(RedisDbTypeEnum type, String key, Object value, boolean flage) {
    	RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate(type);
        try {
            redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    // 存一个对象
    public boolean setBean(RedisDbTypeEnum type, String key, Object value, long time, TimeUnit timeUnit) {
    	RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate(type);
        try {
        	redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, timeUnit);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    // 返回指定对象
    public String getStr(RedisDbTypeEnum type, String key) {
    	RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate(type);
        // 判断key是不是空
        if (StringUtils.isNotBlank(key)) {
            // 判断是否能从redis取出值
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj != null) {
                return (String) obj;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // 返回指定对象
    public <T> T getBean(RedisDbTypeEnum type, String key, Class<T> beanClass) {
        // 判断key是不是空
        if (StringUtils.isNotBlank(key)) {
        	RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate(type);
            // 判断是否能从redis取出值
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj != null && obj != "") {
                if (beanClass.isInstance(String.class)) {
                    return (T) obj;
                } else {
                    return JSONUtil.toBean(JSONUtil.toJsonStr(obj), beanClass);
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // 返回集合对象
    public <T> List<T> getBeanList(RedisDbTypeEnum type, String key, Class<T> beanClass) {
        String s = getStr(type, key);
        List<T> ts = JSONUtil.toList(s, beanClass);
        return ts;
//        if (ts.size() == 0) {
//            return null;
//        } else {
//            return ts;
//        }
    }

    // 删除缓存
    public void del(RedisDbTypeEnum type, String... key) {
        if (key != null && key.length > 0) {
        	RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate(type);
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    // 模糊查询key
    public Set<String> keys(RedisDbTypeEnum type, String s) {
    	RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate(type);
        Set<String> keys = redisTemplate.keys(s);
        return keys;
    }

    // 设置生命周期
    public void expire(RedisDbTypeEnum type, String key, long time, TimeUnit timeUnit) {
    	RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate(type);
        redisTemplate.expire(key, time, timeUnit);
    }

    @Resource
    private LockTemplate lockTemplate;

    /**
     * 加锁
     *
     * @param key         redis的key
     * @param expireTime  key的有效时间
     * @param waitTimeout 等待获取锁超时时间
     * @return 有值, 加锁成功, 没有值加锁失败
     */
    public LockInfo lock(String key, long expireTime, long waitTimeout) {
        key = keyPrefix + "_" + key;
        return lockTemplate.lock(key, expireTime, waitTimeout, RedisTemplateLockExecutor.class);
    }

    /**
     * 加锁,使用环境变量配置的默认时间
     *
     * @param key redis的key
     * @return 有值, 加锁成功, 没有值加锁失败
     */
    public LockInfo lockDefaultTime(String key) {
        key = keyPrefix + "_" + key;
        return lockTemplate.lock(key, expire, timeout, RedisTemplateLockExecutor.class);
    }

    // 释放锁

    /**
     * 释放锁
     *
     * @param lockInfo 调用lock方法 返回获取到的对象
     * @return
     */
    public boolean releaseLock(LockInfo lockInfo) {
        return lockTemplate.releaseLock(lockInfo);
    }
    
    private RedisTemplate<String, Object> getRedisTemplate(RedisDbTypeEnum type) {
    	switch(type) {
	    	case DEFAULT:
	    	default:
	    		return this.defaultRedisTemplate;
	    	case IP:
	    		return ipRedisTemplate;
	    	case STOCK_A:
	    		return stockARedisTemplate;
	    	case STOCK_HK:
	    		return stockHkRedisTemplate;
	    	case STOCK_US:
	    		return stockUsRedisTemplate;
	    	case STOCK_API:
	    		return stockApiRedisTemplate;
    	}
    }
    
    /**
     * 获取缓存过期时间
     * @param type
     * @param key
     * @param timeUnit
     * @return
     */
    public Long getExpire(RedisDbTypeEnum type, String key,TimeUnit timeUnit) {
    	RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate(type);
        // 判断key是不是空
        if (StringUtils.isNotBlank(key)) {
            // 判断是否能从redis取出值
            Long expire = redisTemplate.getExpire(key,timeUnit);
            if (expire != null) {
                return expire;
            } else {
                return null;
            }
        } else {
            return null;
        }
	}

}
