package com.bank.backend.one.outers.datastores;

import com.bank.backend.one.outers.configurations.PostgresOneConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
public class PostgresOneDatastore {

    @Autowired
    PostgresOneConfiguration postgresOneConfiguration;

    @Bean
    @Qualifier(
            value = "postgresOneConnectionFactory"
    )
    public ConnectionFactory postgresOneConnectionFactory() {
        String url = String.format(
                "r2dbc:postgresql://%s:%s@%s:%s/%s",
                postgresOneConfiguration.getUser(),
                postgresOneConfiguration.getPassword(),
                postgresOneConfiguration.getHost(),
                postgresOneConfiguration.getPort(),
                postgresOneConfiguration.getDatabase()
        );
        return ConnectionFactories.get(url);
    }

    @Bean
    @Qualifier(
            value = "postgresOneDatabaseClient"
    )
    public DatabaseClient postgresOneDatabaseClient(
            @Qualifier(
                    value = "postgresOneConnectionFactory"
            )
            ConnectionFactory connectionFactory
    ) {
        return DatabaseClient.create(connectionFactory);
    }

}
