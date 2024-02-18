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
        return accountRepository
                .findOneById(id)
                .flatMap(account -> Mono
                        .just(
                                Result
                                        .<Account>builder()
                                        .data(account)
                                        .code(200)
                                        .message("Account management findOneById is succeed.")
                                        .build()
                        )
                )
                .switchIfEmpty(
                        Mono
                                .just(Result
                                        .<Account>builder()
                                        .code(404)
                                        .message("Account management findOneById is failed, account not found.")
                                        .build()
                                )
                )
                .onErrorReturn(
                        Result
                                .<Account>builder()
                                .code(500)
                                .message("Account management findOneById is failed.")
                                .build()
                );
    }

    public Mono<Result<Account>> saveOne(Account saveAccount) {
        return accountRepository
                .findOneByEmail(saveAccount.getEmail())
                .map(account -> Result
                        .<Account>builder()
                        .code(409)
                        .data(account)
                        .message("Account management saveOne is failed, account already exists by email.")
                        .build()
                )
                .switchIfEmpty(
                        accountRepository
                                .save(saveAccount)
                                .map(account -> {
                                    return Result
                                            .<Account>builder()
                                            .data(account)
                                            .code(201)
                                            .message("Account management saveOne is succeed.")
                                            .build();
                                })
                )
                .onErrorReturn(
                        Result
                                .<Account>builder()
                                .code(500)
                                .message("Account management saveOne is failed.")
                                .build()
                );
    }

    public Mono<Result<Account>> patchOneById(UUID id, Account patchAccount) {
        return accountRepository
                .findOneById(patchAccount.getId())
                .map(account -> Result
                        .<Account>builder()
                        .code(409)
                        .data(account)
                        .message("Account management patchOneById is failed, account already exists by id.")
                        .build()
                )
                .switchIfEmpty(accountRepository
                        .findOneById(id)
                        .map(account -> {
                            account.setEmail(patchAccount.getEmail());
                            account.setPassword(patchAccount.getPassword());
                            account.setUpdatedAt(patchAccount.getUpdatedAt());
                            account.setCreatedAt(patchAccount.getCreatedAt());
                            return account;
                        })
                        .flatMap(accountRepository::save)
                        .map(account -> Result
                                .<Account>builder()
                                .data(account)
                                .code(200)
                                .message("Account management patchOneById is succeed.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<Account>builder()
                        .code(500)
                        .message("Account management patchOneById is failed.")
                        .build()
                );
    }

    public Mono<Result<Account>> deleteOneById(UUID id) {
        return accountRepository
                .findOneById(id)
                .flatMap((account) -> accountRepository.deleteById(account.getId()).thenReturn(account))
                .map(account -> Result
                        .<Account>builder()
                        .data(account)
                        .code(200)
                        .message("Account management deleteOneById is succeed.")
                        .build()
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<Account>builder()
                                .code(404)
                                .message("Account management deleteOneById is failed, account not found.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<Account>builder()
                        .code(500)
                        .message("Account management deleteOneById is failed.")
                        .build()
                );
    }
}
