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

    public Mono<Result<Account>> findFirstById(UUID id) {
        return accountRepository.findFirstById(id)
                .flatMap(account -> Mono.just(
                                Result.<Account>builder()
                                        .data(account)
                                        .code(200)
                                        .message("Account management findFirstById succeed.")
                                        .build()
                        )
                )
                .switchIfEmpty(
                        Mono.just(Result.<Account>builder()
                                .code(404)
                                .message("Account management findFirstById failed, account not found.")
                                .build())
                )
                .onErrorReturn(
                        Result.<Account>builder()
                                .code(500)
                                .message("Account management findFirstById failed.")
                                .build()
                );
    }

    public Mono<Result<Account>> saveOne(Account saveAccount) {
        return accountRepository.findFirstByEmail(saveAccount.getEmail())
                .map(account -> Result.<Account>builder()
                        .code(409)
                        .data(account)
                        .message("Account management saveFirst failed, account already exists by email.")
                        .build()
                )
                .switchIfEmpty(
                        accountRepository.save(saveAccount)
                                .map(account -> {
                                    return Result.<Account>builder()
                                            .data(account)
                                            .code(201)
                                            .message("Account management saveFirst succeed.")
                                            .build();
                                })
                )
                .onErrorReturn(
                        Result.<Account>builder()
                                .code(500)
                                .message("Account management saveFirst failed.")
                                .build()
                );
    }

    public Mono<Result<Account>> patchFirstById(UUID id, Account patchAccount) {
        return accountRepository.findFirstById(patchAccount.getId())
                .map(account -> Result.<Account>builder()
                        .code(409)
                        .data(account)
                        .message("Account management patchFirstById failed, account already exists by id.")
                        .build()
                )
                .switchIfEmpty(
                        accountRepository.findFirstById(id)
                                .map(account -> {
                                    account.setEmail(patchAccount.getEmail());
                                    account.setPassword(patchAccount.getPassword());
                                    account.setUpdatedAt(patchAccount.getUpdatedAt());
                                    account.setCreatedAt(patchAccount.getCreatedAt());
                                    return account;
                                })
                                .flatMap(accountRepository::save)
                                .map(account -> Result.<Account>builder()
                                        .data(account)
                                        .code(200)
                                        .message("Account management patchFirstById succeed.")
                                        .build()
                                )
                )
                .onErrorReturn(
                        Result.<Account>builder()
                                .code(500)
                                .message("Account management patchFirstById failed.")
                                .build()
                );
    }

    public Mono<Result<Account>> deleteFirstById(UUID id) {
        return accountRepository.findFirstById(id)
                .flatMap((account) -> accountRepository.deleteById(account.getId()).thenReturn(account))
                .map(account -> Result.<Account>builder()
                        .data(account)
                        .code(200)
                        .message("Account management deleteFirstById succeed.")
                        .build()
                )
                .switchIfEmpty(
                        Mono.just(Result.<Account>builder()
                                .code(404)
                                .message("Account management deleteFirstById failed, account not found.")
                                .build())
                )
                .onErrorReturn(
                        Result.<Account>builder()
                                .code(500)
                                .message("Account management deleteFirstById failed.")
                                .build()
                );
    }
}
