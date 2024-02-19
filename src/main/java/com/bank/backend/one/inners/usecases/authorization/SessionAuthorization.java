package com.bank.backend.one.inners.usecases.authorization;


import com.bank.backend.one.inners.models.dtos.Result;
import com.bank.backend.one.inners.models.dtos.Session;
import com.bank.backend.one.outers.configurations.JwtConfiguration;
import com.bank.backend.one.outers.repositories.AccountRepository;
import com.bank.backend.one.outers.repositories.AccountTypeRepository;
import com.bank.backend.one.outers.repositories.SessionRepository;
import com.bank.backend.one.outers.tools.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class SessionAuthorization {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private JwtTool jwtTool;
    @Autowired
    private JwtConfiguration jwtConfiguration;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountTypeRepository accountTypeRepository;

    public Mono<Result<Session>> refreshAccessToken(Session session) {
        return sessionRepository
                .getSessionByHash(session.toJsonStringHash())
                .filter(cachedSession -> cachedSession.equals(session))
                .flatMap(cachedSession -> jwtTool
                        .decodeToken(cachedSession.getAccessToken())
                        .flatMap(decodedJwt -> accountRepository
                                .findOneById(decodedJwt.getClaim(jwtConfiguration.getToken().getClaim().getAccount()).as(UUID.class))
                                .flatMap(account -> accountTypeRepository
                                        .findOneById(decodedJwt.getClaim(jwtConfiguration.getToken().getClaim().getType()).as(UUID.class))
                                        .flatMap(accountType -> jwtTool
                                                .generateToken(
                                                        account,
                                                        accountType,
                                                        OffsetDateTime.now().plusMinutes(10)
                                                )
                                                .flatMap(accessToken -> Mono
                                                        .just(Session
                                                                .builder()
                                                                .accessToken(accessToken)
                                                                .refreshToken(cachedSession.getRefreshToken())
                                                                .expiredAt(cachedSession.getExpiredAt())
                                                                .build()
                                                        ).flatMap(newSession -> sessionRepository
                                                                .setSession(newSession)
                                                                .filter(isSet -> isSet.equals(true))
                                                                .then(sessionRepository.deleteSession(cachedSession))
                                                                .then(Mono
                                                                        .just(Result
                                                                                .<Session>builder()
                                                                                .code(200)
                                                                                .data(newSession)
                                                                                .message("SessionAuthorization refreshAccessToken is succeed.")
                                                                                .build()
                                                                        )
                                                                )
                                                                .switchIfEmpty(Mono
                                                                        .just(Result
                                                                                .<Session>builder()
                                                                                .code(500)
                                                                                .message("SessionAuthorization refreshAccessToken is failed, session is not set.")
                                                                                .build()
                                                                        )
                                                                )
                                                        )
                                                )
                                                .switchIfEmpty(Mono
                                                        .just(Result
                                                                .<Session>builder()
                                                                .code(500)
                                                                .message("SessionAuthorization refreshAccessToken is failed, accessToken is not generated.")
                                                                .build()
                                                        )
                                                )
                                        )
                                        .switchIfEmpty(Mono
                                                .just(Result
                                                        .<Session>builder()
                                                        .code(404)
                                                        .message("SessionAuthorization refreshAccessToken is failed, accountType is not found by id.")
                                                        .build()
                                                )
                                        )
                                )
                                .switchIfEmpty(Mono
                                        .just(Result
                                                .<Session>builder()
                                                .code(404)
                                                .message("SessionAuthorization refreshAccessToken is failed, account is not found by id.")
                                                .build()
                                        )
                                )
                        ).switchIfEmpty(Mono
                                .just(Result
                                        .<Session>builder()
                                        .code(404)
                                        .message("SessionAuthorization refreshAccessToken is failed, decoding accessToken is failed.")
                                        .build()
                                )
                        )
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<Session>builder()
                                .code(404)
                                .message("SessionAuthorization refreshAccessToken is failed, session is not found.")
                                .build()
                        )
                );
    }
}
