package com.bank.backend.one.outers.tools;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bank.backend.one.inners.models.daos.Account;
import com.bank.backend.one.inners.models.daos.AccountType;
import com.bank.backend.one.outers.configurations.JwtConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Date;

@Component
public class JwtTool {

    @Autowired
    private JwtConfiguration jwtConfiguration;

    @Autowired
    private Algorithm algorithm;

    public Mono<String> generateToken(Account account, AccountType accountType, OffsetDateTime expirationTime) {
        return Mono
                .just(JWT
                        .create()
                        .withIssuer(jwtConfiguration.getToken().getIssuer())
                        .withSubject(jwtConfiguration.getToken().getSubject())
                        .withClaim(jwtConfiguration.getToken().getClaim().getAccount(), account.getId().toString())
                        .withClaim(jwtConfiguration.getToken().getClaim().getType(), accountType.getId().toString())
                        .withExpiresAt(Date.from(expirationTime.toInstant()))
                        .withIssuedAt(Date.from(OffsetDateTime.now().toInstant()))
                        .withNotBefore(Date.from(OffsetDateTime.now().toInstant()))
                        .sign(algorithm)
                );
    }

    public Mono<DecodedJWT> verifyToken(String token) {
        return Mono
                .just(JWT
                        .require(algorithm)
                        .withIssuer(jwtConfiguration.getToken().getIssuer())
                        .build()
                )
                .map(verifier -> verifier.verify(token))
                .onErrorResume(e -> Mono.error(new RuntimeException("Token verification is failed.", e)));
    }
}
