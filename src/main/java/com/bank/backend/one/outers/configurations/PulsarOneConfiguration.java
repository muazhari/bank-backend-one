package com.bank.backend.one.outers.configurations;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
        prefix = "datastore.pulsar.one"
)
@Data
public class PulsarOneConfiguration {
    private String host;
    private PulsarBrokerConfiguration broker;
    private PulsarWebConfiguration web;
}
