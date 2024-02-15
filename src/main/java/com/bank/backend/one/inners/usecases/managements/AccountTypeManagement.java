package com.bank.backend.one.inners.usecases.managements;


import com.bank.backend.one.inners.models.daos.AccountType;
import com.bank.backend.one.inners.models.dtos.Result;
import com.bank.backend.one.outers.repositories.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class AccountTypeManagement {

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    public Mono<Result<AccountType>> findOneById(UUID id) {
        return accountTypeRepository.findFirstById(id)
                .map(accountType -> Result.<AccountType>builder()
                        .data(accountType)
                        .code(200)
                        .message("AccountType management findOneById succeed.")
                        .build())
                .switchIfEmpty(
                        Mono.just(Result.<AccountType>builder()
                                .code(404)
                                .message("AccountType management findOneById failed, accountType not found.")
                                .build())
                )
                .onErrorReturn(
                        Result.<AccountType>builder()
                                .code(500)
                                .message("AccountType management findOneById failed.")
                                .build()
                );
    }

    public Mono<Result<AccountType>> saveOne(AccountType saveAccountType) {
        return accountTypeRepository.findFirstByName(saveAccountType.getName())
                .flatMap(accountType -> Mono.just(Result.<AccountType>builder()
                        .code(409)
                        .data(accountType)
                        .message("AccountType management saveOne failed, accountType already exists by name.")
                        .build())
                )
                .switchIfEmpty(
                        accountTypeRepository.save(saveAccountType)
                                .map(accountType -> {
                                    return Result.<AccountType>builder()
                                            .data(accountType)
                                            .code(201)
                                            .message("AccountType management saveOne succeed.")
                                            .build();
                                })
                )
                .onErrorReturn(
                        Result.<AccountType>builder()
                                .code(500)
                                .message("AccountType management saveOne failed.")
                                .build()
                );
    }

    public Mono<Result<AccountType>> patchOneById(UUID id, AccountType patchAccountType) {
        return accountTypeRepository.findFirstById(id)
                .flatMap(accountTypeRepository::save)
                .map(accountType -> Result.<AccountType>builder()
                        .data(accountType)
                        .code(200)
                        .message("AccountType management patchOneById succeed.")
                        .build())
                .switchIfEmpty(
                        Mono.just(Result.<AccountType>builder()
                                .code(404)
                                .message("AccountType management patchOneById failed, accountType not found.")
                                .build())
                )
                .onErrorReturn(
                        Result.<AccountType>builder()
                                .code(500)
                                .message("AccountType management patchOneById failed.")
                                .build()
                );
    }

    public Mono<Result<AccountType>> deleteOneById(UUID id) {
        return accountTypeRepository.findFirstById(id)
                .flatMap((accountType) -> accountTypeRepository.deleteById(accountType.getId()).thenReturn(accountType))
                .map(accountType -> Result.<AccountType>builder()
                        .data(accountType)
                        .code(200)
                        .message("AccountType management deleteOneById succeed.")
                        .build()
                )
                .switchIfEmpty(
                        Mono.just(Result.<AccountType>builder()
                                .code(404)
                                .message("AccountType management deleteOneById failed, accountType not found.")
                                .build())
                )
                .onErrorReturn(
                        Result.<AccountType>builder()
                                .code(500)
                                .message("AccountType management deleteOneById failed.")
                                .build()
                );
    }
}
