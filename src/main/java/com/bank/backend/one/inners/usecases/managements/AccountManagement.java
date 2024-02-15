package com.bank.backend.one.inners.usecases.managements;


import com.bank.backend.one.inners.models.daos.Account;
import com.bank.backend.one.inners.models.dtos.Result;
import com.bank.backend.one.outers.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class AccountManagement {

    @Autowired
    private AccountRepository accountRepository;

    public Mono<Result<Account>> findOneById(UUID id) {
        return accountRepository.findFirstById(id)
                .flatMap(account -> Mono.just(
                                Result.<Account>builder()
                                        .data(account)
                                        .code(200)
                                        .message("Account management findOneById succeed.")
                                        .build()
                        )
                )
                .switchIfEmpty(
                        Mono.just(Result.<Account>builder()
                                .code(404)
                                .message("Account management findOneById failed, account not found.")
                                .build())
                )
                .onErrorReturn(
                        Result.<Account>builder()
                                .code(500)
                                .message("Account management findOneById failed.")
                                .build()
                );
    }

    public Mono<Result<Account>> saveOne(Account saveAccount) {
        return accountRepository.findFirstByEmail(saveAccount.getEmail())
                .map(account -> Result.<Account>builder()
                        .code(409)
                        .data(account)
                        .message("Account management saveOne failed, account already exists by email.")
                        .build()
                )
                .switchIfEmpty(
                        accountRepository.save(saveAccount)
                                .map(account -> {
                                    return Result.<Account>builder()
                                            .data(account)
                                            .code(201)
                                            .message("Account management saveOne succeed.")
                                            .build();
                                })
                )
                .onErrorReturn(
                        Result.<Account>builder()
                                .code(500)
                                .message("Account management saveOne failed.")
                                .build()
                );
    }

    public Mono<Result<Account>> patchOneById(UUID id, Account patchAccount) {
        return accountRepository.findFirstById(id)
                .flatMap(accountRepository::save)
                .map(account -> Result.<Account>builder()
                        .data(account)
                        .code(200)
                        .message("Account management patchOneById succeed.")
                        .build())
                .switchIfEmpty(
                        Mono.just(Result.<Account>builder()
                                .code(404)
                                .message("Account management patchOneById failed, account not found.")
                                .build())
                )
                .onErrorReturn(
                        Result.<Account>builder()
                                .code(500)
                                .message("Account management patchOneById failed.")
                                .build()
                );
    }

    public Mono<Result<Account>> deleteOneById(UUID id) {
        return accountRepository.findFirstById(id)
                .flatMap((account) -> accountRepository.deleteById(account.getId()).thenReturn(account))
                .map(account -> Result.<Account>builder()
                        .data(account)
                        .code(200)
                        .message("Account management deleteOneById succeed.")
                        .build()
                )
                .switchIfEmpty(
                        Mono.just(Result.<Account>builder()
                                .code(404)
                                .message("Account management deleteOneById failed, account not found.")
                                .build())
                )
                .onErrorReturn(
                        Result.<Account>builder()
                                .code(500)
                                .message("Account management deleteOneById failed.")
                                .build()
                );
    }
}
