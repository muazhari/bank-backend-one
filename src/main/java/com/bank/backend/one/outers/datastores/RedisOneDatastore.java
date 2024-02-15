package com.bank.backend.one.outers.datastores;

import com.bank.backend.one.outers.configurations.RedisOneConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisOneDatastore {

    @Autowired
    RedisOneConfiguration redisOneConfiguration;

    @Bean
    @Qualifier(
            value = "redisOneConnectionFactory"
    )
    public ReactiveRedisConnectionFactory redisOneConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisOneConfiguration.getHost());
        config.setPort(Integer.parseInt(redisOneConfiguration.getPort()));
        config.setPassword(redisOneConfiguration.getPassword());
        return new LettuceConnectionFactory(config);
    }

}
