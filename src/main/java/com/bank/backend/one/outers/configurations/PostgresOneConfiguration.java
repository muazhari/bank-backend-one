package com.bank.backend.one.outers.configurations;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
        prefix = "datastore.postgres.one"
)
@Data
public class PostgresOneConfiguration {
    private String host;
    private String port;
    private String user;
    private String password;
    private String database;
}
