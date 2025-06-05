package com.example.urlshortner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//@Configuration: is a class level annotation which indicates that an object is a source of bean definition.
//@Configuration declares beans through @Bean annotated method
//Instantiated beans have a singleton scope by default.
@Configuration
public class RedisConfig {

    //  @Value annotation is used to Inject values from Spring’s environment,
    //  typically from application.properties or application.yml file.
    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${spring.redis.database:0}")
    private int redisDatabase;

    //  @Bean annotation is used to indicate that a method instantiates, configures, and initializes a new object to be
    //  managed by Spring IoC (Inversion of Control) container.
    //  IoC is a process in which an object defines its dependencies without creating them.
    @Bean
    //  JedisConnectionFactory. This factory is responsible for creating connections to the Redis server.
    //  Jedis is a popular Java client library for Redis.
    //  return factory;: Returns the configured factory, making it available as a Spring bean named jedisConnectionFactory
    public RedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setDatabase(redisDatabase);

        if (redisPassword != null && !redisPassword.trim().isEmpty()) {
            config.setPassword(redisPassword);
        }

        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        factory.afterPropertiesSet();
        return factory;
    }

    //  Defines a bean that takes a RedisConnectionFactory (the one created above will be automatically
    //  injected by Spring) as a parameter. The template is configured to handle String keys and Object values.
    //  return template;: Returns the configured template, making it available as a Spring bean named redisTemplate.
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Use JSON serializer for values
        // Creates a serializer that converts Java objects to and from JSON format using the Jackson library.
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.setDefaultSerializer(jsonSerializer);
        template.afterPropertiesSet();

        return template;
    }
}
