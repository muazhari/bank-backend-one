package com.bank.backend.one.inners.usecases.authentications.logins;


import com.bank.backend.one.inners.models.dtos.Result;
import com.bank.backend.one.inners.models.dtos.Session;
import com.bank.backend.one.inners.models.dtos.requests.authentications.logins.LoginByEmailAndPasswordRequest;
import com.bank.backend.one.inners.models.dtos.responses.authentications.logins.LoginByEmailAndPasswordResponse;
import com.bank.backend.one.outers.repositories.AccountRepository;
import com.bank.backend.one.outers.repositories.AccountTypeMapRepository;
import com.bank.backend.one.outers.repositories.AccountTypeRepository;
import com.bank.backend.one.outers.repositories.SessionRepository;
import com.bank.backend.one.outers.tools.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class LoginAuthentication {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountTypeRepository accountTypeRepository;
    @Autowired
    private AccountTypeMapRepository accountTypeMapRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTool jwtTool;

    public Mono<Result<LoginByEmailAndPasswordResponse>> loginByEmailAndPassword(LoginByEmailAndPasswordRequest request) {
        return accountRepository.findOneByEmail(request.getEmail())
                .flatMap(account -> {
                    if (passwordEncoder.matches(request.getPassword(), account.getPassword())) {
                        return accountTypeMapRepository
                                .findOneByAccountIdAndAccountTypeName(account.getId(), request.getTypeName())
                                .flatMap(accountTypeMap -> accountTypeRepository
                                        .findOneById(accountTypeMap.getAccountTypeId())
                                        .flatMap(accountType -> jwtTool.
                                                generateToken(
                                                        account,
                                                        accountType,
                                                        OffsetDateTime.now().plusMinutes(10)
                                                )
                                                .flatMap(accessToken -> Mono
                                                        .just(Session
                                                                .builder()
                                                                .accessToken(accessToken)
                                                                .refreshToken(UUID.randomUUID().toString())
                                                                .expiredAt(OffsetDateTime.now().plusDays(1))
                                                                .build()
                                                        )
                                                        .flatMap(session -> sessionRepository
                                                                .setSession(session)
                                                                .filter(isSet -> isSet.equals(true))
                                                                .map(isSet -> Result
                                                                        .<LoginByEmailAndPasswordResponse>builder()
                                                                        .code(200)
                                                                        .message("LoginAuthentication loginByEmailAndPassword is succeed.")
                                                                        .data(LoginByEmailAndPasswordResponse
                                                                                .builder()
                                                                                .account(account)
                                                                                .accountType(accountType)
                                                                                .session(session)
                                                                                .build()
                                                                        )
                                                                        .build()
                                                                )
                                                                .switchIfEmpty(Mono
                                                                        .just(Result
                                                                                .<LoginByEmailAndPasswordResponse>builder()
                                                                                .code(500)
                                                                                .message("LoginAuthentication loginByEmailAndPassword is failed, session is not set.")
                                                                                .build()
                                                                        )
                                                                )
                                                        )
                                                )
                                                .switchIfEmpty(Mono
                                                        .just(Result
                                                                .<LoginByEmailAndPasswordResponse>builder()
                                                                .code(500)
                                                                .message("LoginAuthentication loginByEmailAndPassword is failed, accessToken is not generated.")
                                                                .build()
                                                        )
                                                )
                                        )
                                        .switchIfEmpty(Mono
                                                .just(Result
                                                        .<LoginByEmailAndPasswordResponse>builder()
                                                        .code(404)
                                                        .message("LoginAuthentication LoginByEmailAndPasswordRequest is failed, accountType is not found by accountTypeId.")
                                                        .build()
                                                )
                                        )
                                )
                                .switchIfEmpty(Mono
                                        .just(Result
                                                .<LoginByEmailAndPasswordResponse>builder()
                                                .code(404)
                                                .message("LoginAuthentication LoginByEmailAndPasswordRequest is failed, accountTypeMap is not found by accountId and accountTypeName.")
                                                .build()
                                        )
                                );
                    }
                    return Mono
                            .just(Result
                                    .<LoginByEmailAndPasswordResponse>builder()
                                    .code(401)
                                    .message("LoginAuthentication LoginByEmailAndPasswordRequest is failed, password is incorrect.")
                                    .build()
                            );
                })
                .switchIfEmpty(Mono
                        .just(Result
                                .<LoginByEmailAndPasswordResponse>builder()
                                .code(404)
                                .message("LoginAuthentication LoginByEmailAndPasswordRequest is failed, account is not found by email.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<LoginByEmailAndPasswordResponse>builder()
                        .code(500)
                        .message("LoginAuthentication LoginByEmailAndPasswordRequest is failed.")
                        .build()
                );
    }
}
