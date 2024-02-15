package com.bank.backend.one.inners.usecases.authentications.registers;


import com.bank.backend.one.inners.models.daos.Account;
import com.bank.backend.one.inners.models.dtos.Result;
import com.bank.backend.one.inners.models.dtos.requests.authentications.registers.RegisterByEmailAndPasswordRequest;
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

    public Mono<Result<Account>> registerByEmailAndPassword(RegisterByEmailAndPasswordRequest request) {
        return accountRepository.findFirstByEmail(request.getEmail())
                .map(account -> Result.<Account>builder()
                        .code(409)
                        .data(account)
                        .message("Register authentication registerByEmailAndPassword failed, account already exists by email.")
                        .build()
                )
                .switchIfEmpty(
                        accountTypeRepository.findFirstById(request.getAccountTypeId())
                                .flatMap(accountType -> accountRepository.save(
                                                Account.builder()
                                                        .id(UUID.randomUUID())
                                                        .email(request.getEmail())
                                                        .password(passwordEncoder.encode(request.getPassword()))
                                                        .createdAt(OffsetDateTime.now())
                                                        .updatedAt(OffsetDateTime.now())
                                                        .build()
                                        )
                                )
                                .map(account -> Result.<Account>builder()
                                        .data(account)
                                        .code(201)
                                        .message("Register authentication registerByEmailAndPassword succeed.")
                                        .build()
                                )
                                .switchIfEmpty(
                                        Mono.just(
                                                Result.<Account>builder()
                                                        .code(404)
                                                        .message("Register authentication registerByEmailAndPassword failed, accountType not found.")
                                                        .build()
                                        )
                                )
                )
                .onErrorReturn(
                        Result.<Account>builder()
                                .code(500)
                                .message("Register authentication registerByEmailAndPassword failed.")
                                .build()
                );
    }
}
