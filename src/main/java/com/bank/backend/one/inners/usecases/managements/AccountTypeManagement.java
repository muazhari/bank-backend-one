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

    public Mono<Result<AccountType>> findFirstByName(String name) {
        return accountTypeRepository.findFirstByName(name)
                .map(accountType -> Result.<AccountType>builder()
                        .data(accountType)
                        .code(200)
                        .message("AccountType management findFirstByName succeed.")
                        .build())
                .switchIfEmpty(
                        Mono.just(Result.<AccountType>builder()
                                .code(404)
                                .message("AccountType management findFirstByName failed, accountType not found.")
                                .build())
                )
                .onErrorReturn(
                        Result.<AccountType>builder()
                                .code(500)
                                .message("AccountType management findFirstByName failed.")
                                .build()
                );
    }

    public Mono<Result<AccountType>> saveFirst(AccountType saveAccountType) {
        return accountTypeRepository.findFirstByName(saveAccountType.getName())
                .flatMap(accountType -> Mono.just(Result.<AccountType>builder()
                        .code(409)
                        .data(accountType)
                        .message("AccountType management saveFirst failed, accountType already exists by name.")
                        .build())
                )
                .switchIfEmpty(
                        accountTypeRepository.save(saveAccountType)
                                .map(accountType -> {
                                    return Result.<AccountType>builder()
                                            .data(accountType)
                                            .code(201)
                                            .message("AccountType management saveFirst succeed.")
                                            .build();
                                })
                )
                .onErrorReturn(
                        Result.<AccountType>builder()
                                .code(500)
                                .message("AccountType management saveFirst failed.")
                                .build()
                );
    }

    public Mono<Result<AccountType>> patchFirstByName(String name, AccountType patchAccountType) {
        return accountTypeRepository.findFirstByName(patchAccountType.getName())
                .map(accountType -> Result.<AccountType>builder()
                        .code(409)
                        .data(accountType)
                        .message("AccountType management patchFirstByName failed, accountType already exists by name.")
                        .build()
                )
                .switchIfEmpty(
                        accountTypeRepository.findFirstByName(name)
                                .map(accountType -> {
                                    accountType.setName(patchAccountType.getName());
                                    accountType.setUpdatedAt(patchAccountType.getUpdatedAt());
                                    accountType.setCreatedAt(patchAccountType.getCreatedAt());
                                    return accountType;
                                })
                                .flatMap(accountTypeRepository::save)
                                .map(accountType -> Result.<AccountType>builder()
                                        .data(accountType)
                                        .code(200)
                                        .message("AccountType management patchFirstByName succeed.")
                                        .build()
                                )
                )
                .onErrorReturn(
                        Result.<AccountType>builder()
                                .code(500)
                                .message("AccountType management patchFirstByName failed.")
                                .build()
                );
    }

    public Mono<Result<AccountType>> deleteFirstByName(String name) {
        return accountTypeRepository.findFirstByName(name)
                .flatMap((accountType) -> accountTypeRepository.deleteByName(accountType.getName()).thenReturn(accountType))
                .map(accountType -> Result.<AccountType>builder()
                        .data(accountType)
                        .code(200)
                        .message("AccountType management deleteFirstByName succeed.")
                        .build()
                )
                .switchIfEmpty(
                        Mono.just(Result.<AccountType>builder()
                                .code(404)
                                .message("AccountType management deleteFirstByName failed, accountType not found.")
                                .build())
                )
                .onErrorReturn(
                        Result.<AccountType>builder()
                                .code(500)
                                .message("AccountType management deleteFirstByName failed.")
                                .build()
                );
    }
}
