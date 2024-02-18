package com.bank.backend.one.inners.usecases.authentications.registers;


import com.bank.backend.one.inners.models.daos.Account;
import com.bank.backend.one.inners.models.daos.AccountTypeMap;
import com.bank.backend.one.inners.models.dtos.Result;
import com.bank.backend.one.inners.models.dtos.requests.authentications.registers.RegisterByEmailAndPasswordRequest;
import com.bank.backend.one.inners.models.dtos.responses.authentications.registers.RegisterByEmailAndPasswordResponse;
import com.bank.backend.one.outers.repositories.AccountRepository;
import com.bank.backend.one.outers.repositories.AccountTypeMapRepository;
import com.bank.backend.one.outers.repositories.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class RegisterAuthentication {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountTypeRepository accountTypeRepository;
    @Autowired
    private AccountTypeMapRepository accountTypeMapRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Mono<Result<RegisterByEmailAndPasswordResponse>> registerByEmailAndPassword(RegisterByEmailAndPasswordRequest request) {
        return accountRepository
                .findOneByEmail(request.getEmail())
                .map(account -> Result
                        .<RegisterByEmailAndPasswordResponse>builder()
                        .code(409)
                        .data(RegisterByEmailAndPasswordResponse
                                .builder()
                                .account(account)
                                .build()
                        )
                        .message("RegisterAuthentication registerByEmailAndPassword is failed, account already exists by email.")
                        .build()
                )
                .switchIfEmpty(accountTypeRepository
                        .findOneByName(request.getTypeName())
                        .flatMap(accountType -> accountRepository
                                .save(Account
                                        .builder()
                                        .id(UUID.randomUUID())
                                        .email(request.getEmail())
                                        .password(passwordEncoder.encode(request.getPassword()))
                                        .createdAt(OffsetDateTime.now())
                                        .updatedAt(OffsetDateTime.now())
                                        .build()
                                )
                                .flatMap(account -> accountTypeMapRepository
                                        .save(
                                                AccountTypeMap
                                                        .builder()
                                                        .id(UUID.randomUUID())
                                                        .accountId(account.getId())
                                                        .accountTypeId(accountType.getId())
                                                        .createdAt(OffsetDateTime.now())
                                                        .updatedAt(OffsetDateTime.now())
                                                        .build()
                                        )
                                        .map(accountTypeMap -> Result
                                                .<RegisterByEmailAndPasswordResponse>builder()
                                                .code(201)
                                                .message("RegisterAuthentication registerByEmailAndPassword is succeed.")
                                                .data(RegisterByEmailAndPasswordResponse
                                                        .builder()
                                                        .account(account)
                                                        .accountType(accountType)
                                                        .build()
                                                )
                                                .build()
                                        )
                                )
                        )
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<RegisterByEmailAndPasswordResponse>builder()
                                        .code(404)
                                        .message("RegisterAuthentication registerByEmailAndPassword is failed, accountTypeMap not found by accountId and accountTypeName.")
                                        .build()
                                )
                        )
                )
                .onErrorReturn(Result
                        .<RegisterByEmailAndPasswordResponse>builder()
                        .code(500)
                        .message("RegisterAuthentication registerByEmailAndPassword is failed.")
                        .build()
                );
    }
}
