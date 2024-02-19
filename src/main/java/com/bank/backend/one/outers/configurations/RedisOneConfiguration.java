package com.bank.backend.one.outers.configurations;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
        prefix = "datastore.redis.one"
)
@Data
public class RedisOneConfiguration {
    private String host;
    private String port;
    private String password;
}
