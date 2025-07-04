package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {
	
	@Value("${spring.redis.host}")
	private String host;
	
	@Value("${spring.redis.port}")
	private int port;
	
	@Value("${spring.redis.password}")
	private String password;
	
    @Bean
    public RedisTemplate<String, Object> defaultRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        setRedisTemplate(redisTemplate);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
    @Bean
    public RedisTemplate<String, Object> ipRedisTemplate() {
    	RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    	setRedisTemplate(redisTemplate);
    	RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
    	config.setPassword(password);
    	config.setDatabase(RedisDbTypeEnum.IP.getType());
    	LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(config);
    	lettuceConnectionFactory.afterPropertiesSet();
    	redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> stockARedisTemplate() {
    	RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    	setRedisTemplate(redisTemplate);
    	RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
    	config.setPassword(password);
    	config.setDatabase(RedisDbTypeEnum.STOCK_A.getType());
    	LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(config);
    	lettuceConnectionFactory.afterPropertiesSet();
    	redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> stockHkRedisTemplate() {
    	RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    	setRedisTemplate(redisTemplate);
    	RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
    	config.setPassword(password);
    	config.setDatabase(RedisDbTypeEnum.STOCK_HK.getType());
    	LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(config);
    	lettuceConnectionFactory.afterPropertiesSet();
    	redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        return redisTemplate;
    }
    
    @Bean
    public RedisTemplate<String, Object> stockUsRedisTemplate() {
    	RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    	setRedisTemplate(redisTemplate);
    	RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
    	config.setPassword(password);
    	config.setDatabase(RedisDbTypeEnum.STOCK_US.getType());
    	LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(config);
    	lettuceConnectionFactory.afterPropertiesSet();
    	redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        return redisTemplate;
    }
    
    @Bean
    public RedisTemplate<String, Object> stockApiRedisTemplate() {
    	RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    	setRedisTemplate(redisTemplate);
    	RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
    	config.setPassword(password);
    	config.setDatabase(RedisDbTypeEnum.STOCK_API.getType());
    	LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(config);
    	lettuceConnectionFactory.afterPropertiesSet();
    	redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        return redisTemplate;
    }
    
    
    private void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
    	Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        //在spring-boot-starter-web包里
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        
        //如果不配置Serializer，那么存储的时候缺省使用String，如果用User类型存储，那么会提示错误User can't cast to String！
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 开启事务
        redisTemplate.setEnableTransactionSupport(false);
    }

}
