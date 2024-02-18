package com.bank.backend.one.outers.repositories;


import com.bank.backend.one.inners.models.dtos.Session;
import com.bank.backend.one.outers.tools.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;

@Repository
public class SessionRepository {
    @Autowired
    JwtTool jwtTool;
    @Autowired
    private ReactiveRedisTemplate<String, Session> sessionTemplate;
    @Autowired
    private ReactiveRedisTemplate<String, String> stringTemplate;

    public Mono<Boolean> setSessionByHashWithExpiration(Session session) {
        return jwtTool
                .verifyToken(session.getAccessToken())
                .flatMap(decodedJWT -> sessionTemplate
                        .opsForValue()
                        .set(String.valueOf(session.hashCode()), session, Duration.between(OffsetDateTime.now().toInstant(), decodedJWT.getExpiresAt().toInstant()))
                );
    }

    public Mono<Boolean> setSessionHashByAccessTokenWithExpiration(String accessToken, String hash) {
        return jwtTool
                .verifyToken(accessToken)
                .flatMap(decodedJWT -> stringTemplate
                        .opsForValue()
                        .set(accessToken, hash, Duration.between(OffsetDateTime.now().toInstant(), decodedJWT.getExpiresAt().toInstant()))
                );
    }

    public Mono<Boolean> setSession(Session session) {
        return setSessionByHashWithExpiration(session)
                .flatMap(isSetSessionByHash -> setSessionHashByAccessTokenWithExpiration(session.getAccessToken(), String.valueOf(session.hashCode()))
                        .map(isSetSessionHashByAccessToken -> isSetSessionByHash && isSetSessionHashByAccessToken)
                );
    }


    public Mono<Session> getSessionByHash(String hash) {
        return sessionTemplate
                .opsForValue()
                .get(hash);
    }

    public Mono<Session> getSessionByAccessToken(String accessToken) {
        return stringTemplate
                .opsForValue()
                .get(accessToken)
                .flatMap(this::getSessionByHash);
    }

    public Mono<Void> deleteSessionByHash(String hash) {
        return sessionTemplate
                .opsForValue()
                .delete(hash)
                .then();
    }

    public Mono<Void> deleteSessionHashByAccessToken(String accessToken) {
        return stringTemplate
                .opsForValue()
                .delete(accessToken)
                .then();
    }

    public Mono<Void> deleteSessionByAccessToken(String accessToken) {
        return getSessionByAccessToken(accessToken)
                .flatMap(session -> deleteSessionByHash(String.valueOf(session.hashCode())))
                .then(deleteSessionHashByAccessToken(accessToken));
    }

    public Mono<Void> deleteSession(Session session) {
        return deleteSessionByHash(String.valueOf(session.hashCode()))
                .then(deleteSessionHashByAccessToken(session.getAccessToken()));
    }
}
