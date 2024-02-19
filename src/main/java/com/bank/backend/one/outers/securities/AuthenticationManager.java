package com.bank.backend.one.outers.securities;

import com.bank.backend.one.outers.configurations.JwtConfiguration;
import com.bank.backend.one.outers.repositories.SessionRepository;
import com.bank.backend.one.outers.tools.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    private JwtConfiguration jwtConfiguration;
    @Autowired
    private JwtTool jwtTool;
    @Autowired
    private SessionRepository sessionRepository;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono
                .just(authentication)
                .map(Authentication::getPrincipal)
                .flatMap(credentials -> {
                    if (!Objects.isNull(credentials) && !credentials.toString().isBlank() && !credentials.toString().isEmpty()) {
                        return Mono.just(credentials)
                                .map(Object::toString)
                                .flatMap(accessToken -> sessionRepository.getSessionByAccessToken(accessToken)
                                        .flatMap(cachedSession -> jwtTool.verifyToken(cachedSession.getAccessToken())
                                                .filter(decodedJWT -> OffsetDateTime.now().isBefore(cachedSession.getExpiredAt()))
                                                .map(decodedJwt -> (Authentication) new UsernamePasswordAuthenticationToken(
                                                                cachedSession.getAccessToken(),
                                                                null,
                                                                List.of(new SimpleGrantedAuthority(decodedJwt.getClaim(jwtConfiguration.getToken().getClaim().getType()).asString()))
                                                        )
                                                )
                                                .switchIfEmpty(Mono.error(new RuntimeException("Session verification is failed.")))
                                        )
                                        .switchIfEmpty(Mono.error(new RuntimeException("Access token verification is failed.")))
                                )
                                .switchIfEmpty(Mono.error(new RuntimeException("Cached session is not found.")));
                    }
                    return Mono.error(new RuntimeException("Access token is invalid."));
                });
    }
}
