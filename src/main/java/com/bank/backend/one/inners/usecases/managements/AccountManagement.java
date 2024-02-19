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
                .map(account -> Result
                        .<Account>builder()
                        .data(account)
                        .code(200)
                        .message("AccountManagement findOneById is succeed.")
                        .build()
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<Account>builder()
                                .code(404)
                                .message("AccountManagement findOneById is failed, account is not found by id.")
                                .build()
                        )
                )
                .onErrorReturn(
                        Result
                                .<Account>builder()
                                .code(500)
                                .message("AccountManagement findOneById is failed.")
                                .build()
                );
    }

    public Mono<Result<Account>> saveOne(Account toSaveAccount) {
        return accountRepository
                .findOneByEmail(toSaveAccount.getEmail())
                .map(account -> Result
                        .<Account>builder()
                        .code(409)
                        .data(account)
                        .message("AccountManagement saveOne is failed, account already exists by email.")
                        .build()
                )
                .switchIfEmpty(
                        accountRepository
                                .save(toSaveAccount)
                                .map(savedAccount -> Result
                                        .<Account>builder()
                                        .data(savedAccount)
                                        .code(201)
                                        .message("AccountManagement saveOne is succeed.")
                                        .build()
                                )
                                .switchIfEmpty(Mono
                                        .just(Result
                                                .<Account>builder()
                                                .code(404)
                                                .message("AccountManagement saveOne is failed, account is not saved.")
                                                .build()
                                        )
                                )
                )
                .onErrorReturn(
                        Result
                                .<Account>builder()
                                .code(500)
                                .message("AccountManagement saveOne is failed.")
                                .build()
                );
    }

    public Mono<Result<Account>> patchOneById(UUID id, Account toPatchAccount) {
        return accountRepository
                .findOneById(id)
                .map(account -> Result
                        .<Account>builder()
                        .code(409)
                        .data(account)
                        .message("AccountManagement patchOneById is failed, account already exists by id.")
                        .build()
                )
                .switchIfEmpty(accountRepository
                        .findOneById(id)
                        .flatMap(account -> {
                            account.setId(toPatchAccount.getId());
                            account.setEmail(toPatchAccount.getEmail());
                            account.setPassword(toPatchAccount.getPassword());
                            account.setUpdatedAt(toPatchAccount.getUpdatedAt());
                            account.setCreatedAt(toPatchAccount.getCreatedAt());
                            return accountRepository
                                    .save(account)
                                    .map(savedAccount -> Result
                                            .<Account>builder()
                                            .data(savedAccount)
                                            .code(200)
                                            .message("AccountManagement patchOneById is succeed.")
                                            .build()
                                    )
                                    .switchIfEmpty(Mono
                                            .just(Result
                                                    .<Account>builder()
                                                    .code(404)
                                                    .message("AccountManagement patchOneById is failed, account is not saved.")
                                                    .build()
                                            )
                                    );
                        })
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<Account>builder()
                                        .code(404)
                                        .message("AccountManagement patchOneById is failed, account is not found by id.")
                                        .build()
                                )
                        )
                )
                .onErrorReturn(Result
                        .<Account>builder()
                        .code(500)
                        .message("AccountManagement patchOneById is failed.")
                        .build()
                );
    }

    public Mono<Result<Account>> deleteOneById(UUID id) {
        return accountRepository
                .findOneById(id)
                .flatMap((account) -> accountRepository
                        .deleteOneById(account.getId())
                        .map(deletedAccount -> Result
                                .<Account>builder()
                                .data(deletedAccount)
                                .code(200)
                                .message("AccountManagement deleteOneById is succeed.")
                                .build()
                        )
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<Account>builder()
                                        .code(404)
                                        .message("AccountManagement deleteOneById is failed, account is not deleted.")
                                        .build()
                                )
                        )
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<Account>builder()
                                .code(404)
                                .message("AccountManagement deleteOneById is failed, account is not found by id.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<Account>builder()
                        .code(500)
                        .message("AccountManagement deleteOneById is failed.")
                        .build()
                );
    }
}
