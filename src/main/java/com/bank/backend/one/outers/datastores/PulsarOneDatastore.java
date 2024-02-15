package com.bank.backend.one.outers.datastores;

import com.bank.backend.one.outers.configurations.PulsarOneConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.core.DefaultPulsarClientFactory;

@Configuration
public class PulsarOneDatastore {

    @Autowired
    PulsarOneConfiguration pulsarOneConfiguration;

    @Bean
    @Qualifier(
            value = "pulsarOneClientFactory"
    )
    public DefaultPulsarClientFactory pulsarOneClientFactory() {
        String brokerServiceUrl = String.format(
                "pulsar://%s:%s",
                pulsarOneConfiguration.getHost(),
                pulsarOneConfiguration.getBroker().getPort()
        );
        return new DefaultPulsarClientFactory(brokerServiceUrl);
    }

}
