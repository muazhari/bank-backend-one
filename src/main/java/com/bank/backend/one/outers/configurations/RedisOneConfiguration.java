package com.bank.backend.one.outers.configurations;


import com.bank.backend.one.inners.models.dtos.Session;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConfigurationProperties(
        prefix = "datastore.redis.one"
)
@Data
public class RedisOneConfiguration {
    private String host;
    private String port;
    private String password;

    @Bean
    public ReactiveRedisTemplate<String, Session> sessionTemplate(ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Session> valueSerializer = new Jackson2JsonRedisSerializer<>(Session.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, Session> builder = RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, Session> context = builder.value(valueSerializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
