package com.bank.backend.one.outers.repositories;


import com.bank.backend.one.inners.models.dtos.Session;
import com.bank.backend.one.outers.tools.JwtTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private ReactiveRedisTemplate<String, String> stringTemplate;

    public Mono<Boolean> setSessionByHashWithExpiration(Session session) {
        return jwtTool
                .verifyToken(session.getAccessToken())
                .flatMap(decodedJWT -> stringTemplate
                        .opsForValue()
                        .set(session.toJsonStringHash(), session.toJsonString(), Duration.between(OffsetDateTime.now().toInstant(), decodedJWT.getExpiresAt().toInstant()))
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
                .flatMap(isSetSessionByHash -> setSessionHashByAccessTokenWithExpiration(session.getAccessToken(), session.toJsonStringHash())
                        .map(isSetSessionHashByAccessToken -> isSetSessionByHash && isSetSessionHashByAccessToken)
                );
    }


    public Mono<Session> getSessionByHash(String hash) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return stringTemplate
                .opsForValue()
                .get(hash)
                .handle((sessionString, sink) -> {
                    try {
                        sink.next(objectMapper.readValue(sessionString, Session.class));
                    } catch (JsonProcessingException e) {
                        sink.error(new RuntimeException(e));
                    }
                });
    }

    public Mono<String> getSessionHashByAccessToken(String accessToken) {
        return stringTemplate
                .opsForValue()
                .get(accessToken);
    }

    public Mono<Session> getSessionByAccessToken(String accessToken) {
        return getSessionHashByAccessToken(accessToken)
                .flatMap(this::getSessionByHash);
    }

    public Mono<Void> deleteSessionByHash(String hash) {
        return stringTemplate
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
        return getSessionHashByAccessToken(accessToken)
                .flatMap(this::deleteSessionByHash)
                .then(deleteSessionHashByAccessToken(accessToken));
    }

    public Mono<Void> deleteSession(Session session) {
        return deleteSessionByHash(session.toJsonStringHash())
                .then(deleteSessionHashByAccessToken(session.getAccessToken()));
    }
}
