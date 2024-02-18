package com.bank.backend.one.inners.usecases.managements;


import com.bank.backend.one.inners.models.daos.AccountType;
import com.bank.backend.one.inners.models.dtos.Result;
import com.bank.backend.one.outers.repositories.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class AccountTypeManagement {

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    public Mono<Result<AccountType>> findOneByName(String name) {
        return accountTypeRepository
                .findOneByName(name)
                .map(accountType -> Result
                        .<AccountType>builder()
                        .data(accountType)
                        .code(200)
                        .message("AccountType management findOneByName is succeed.")
                        .build()
                )
                .switchIfEmpty(
                        Mono
                                .just(Result
                                        .<AccountType>builder()
                                        .code(404)
                                        .message("AccountType management findOneByName is failed, accountType not found.")
                                        .build()
                                )
                )
                .onErrorReturn(
                        Result
                                .<AccountType>builder()
                                .code(500)
                                .message("AccountType management findOneByName is failed.")
                                .build()
                );
    }

    public Mono<Result<AccountType>> saveOne(AccountType saveAccountType) {
        return accountTypeRepository
                .findOneByName(saveAccountType.getName())
                .flatMap(accountType -> Mono
                        .just(
                                Result
                                        .<AccountType>builder()
                                        .code(409)
                                        .data(accountType)
                                        .message("AccountType management saveOne is failed, accountType already exists by name.")
                                        .build()
                        )
                )
                .switchIfEmpty(
                        accountTypeRepository
                                .save(saveAccountType)
                                .map(accountType -> {
                                    return Result
                                            .<AccountType>builder()
                                            .data(accountType)
                                            .code(201)
                                            .message("AccountType management saveOne is succeed.")
                                            .build();
                                })
                )
                .onErrorReturn(
                        Result
                                .<AccountType>builder()
                                .code(500)
                                .message("AccountType management saveOne is failed.")
                                .build()
                );
    }

    public Mono<Result<AccountType>> patchOneByName(String name, AccountType patchAccountType) {
        return accountTypeRepository
                .findOneByName(patchAccountType.getName())
                .map(accountType -> Result
                        .<AccountType>builder()
                        .code(409)
                        .data(accountType)
                        .message("AccountType management patchOneByName is failed, accountType already exists by name.")
                        .build()
                )
                .switchIfEmpty(
                        accountTypeRepository
                                .findOneByName(name)
                                .map(accountType -> {
                                    accountType.setName(patchAccountType.getName());
                                    accountType.setUpdatedAt(patchAccountType.getUpdatedAt());
                                    accountType.setCreatedAt(patchAccountType.getCreatedAt());
                                    return accountType;
                                })
                                .flatMap(accountTypeRepository::save)
                                .map(accountType -> Result
                                        .<AccountType>builder()
                                        .data(accountType)
                                        .code(200)
                                        .message("AccountType management patchOneByName is succeed.")
                                        .build()
                                )
                )
                .onErrorReturn(
                        Result
                                .<AccountType>builder()
                                .code(500)
                                .message("AccountType management patchOneByName is failed.")
                                .build()
                );
    }

    public Mono<Result<AccountType>> deleteOneByName(String name) {
        return accountTypeRepository
                .findOneByName(name)
                .flatMap((accountType) -> accountTypeRepository.deleteByName(accountType.getName()).thenReturn(accountType))
                .map(accountType -> Result
                        .<AccountType>builder()
                        .data(accountType)
                        .code(200)
                        .message("AccountType management deleteOneByName is succeed.")
                        .build()
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountType>builder()
                                .code(404)
                                .message("AccountType management deleteOneByName is failed, accountType not found.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<AccountType>builder()
                        .code(500)
                        .message("AccountType management deleteOneByName is failed.")
                        .build()
                );
    }
}
