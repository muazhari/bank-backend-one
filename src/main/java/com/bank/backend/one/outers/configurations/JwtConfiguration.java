package com.bank.backend.one.outers.configurations;


import com.auth0.jwt.algorithms.Algorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfiguration {
    private JwtToken token;

    @Bean
    public Algorithm algorithm() {
        return Algorithm.HMAC256(token.getSecret());
    }
}
