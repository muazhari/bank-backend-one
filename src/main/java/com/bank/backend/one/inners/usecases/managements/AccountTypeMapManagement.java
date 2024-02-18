package com.bank.backend.one.inners.usecases.managements;


import com.bank.backend.one.inners.models.daos.AccountTypeMap;
import com.bank.backend.one.inners.models.dtos.Result;
import com.bank.backend.one.outers.repositories.AccountTypeMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class AccountTypeMapManagement {

    @Autowired
    private AccountTypeMapRepository accountTypeMapRepository;

    public Mono<Result<AccountTypeMap>> findOneByAccountId(UUID accountId) {
        return accountTypeMapRepository
                .findOneByAccountId(accountId)
                .map(accountTypeMap -> Result
                        .<AccountTypeMap>builder()
                        .data(accountTypeMap)
                        .code(200)
                        .message("AccountTypeMap management findOneByAccountId is succeed.")
                        .build()
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMap>builder()
                                .code(404)
                                .message("AccountTypeMap management findOneByAccountId is failed, accountTypeMap not found.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<AccountTypeMap>builder()
                        .code(500)
                        .message("AccountTypeMap management findOneByAccountId is failed.")
                        .build()
                );
    }

    public Mono<Result<AccountTypeMap>> findOneByAccountIdAndAccountTyped(UUID accountId, String accountTypeName) {
        return accountTypeMapRepository
                .findOneByAccountIdAndAccountTypeName(accountId, accountTypeName)
                .map(accountTypeMap -> Result
                        .<AccountTypeMap>builder()
                        .data(accountTypeMap)
                        .code(200)
                        .message("AccountTypeMap management findOneByAccountIdAndAccountTypeName is succeed.")
                        .build()
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMap>builder()
                                .code(404)
                                .message("AccountTypeMap management findOneByAccountIdAndAccountTypeName is failed, accountTypeMap not found.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<AccountTypeMap>builder()
                        .code(500)
                        .message("AccountTypeMap management findOneByAccountIdAndAccountTypeName is failed.")
                        .build()
                );
    }

    public Mono<Result<AccountTypeMap>> saveOne(AccountTypeMap toSaveAccountTypeMap) {
        return accountTypeMapRepository
                .save(toSaveAccountTypeMap)
                .map(accountTypeMap -> Result
                        .<AccountTypeMap>builder()
                        .data(accountTypeMap)
                        .code(201)
                        .message("AccountTypeMap management saveOne is succeed.")
                        .build()
                )
                .onErrorReturn(Result
                        .<AccountTypeMap>builder()
                        .code(500)
                        .message("AccountTypeMap management saveOne is failed.")
                        .build()
                );
    }

    public Mono<Result<AccountTypeMap>> patchOneByAccountIdAndAccountTypeName(UUID accountId, String accountTypeName, AccountTypeMap toPatchAccountTypeMap) {
        return accountTypeMapRepository
                .findOneByAccountIdAndAccountTypeName(toPatchAccountTypeMap.getAccountId(), accountTypeName)
                .map(accountTypeMap -> Result
                        .<AccountTypeMap>builder()
                        .code(409)
                        .data(accountTypeMap)
                        .message("AccountTypeMap management patchOneByAccountIdAndAccountTypeName is failed, accountTypeMap already exists by accountId and accountTypeName.")
                        .build()
                )
                .switchIfEmpty(
                        accountTypeMapRepository
                                .findOneByAccountIdAndAccountTypeName(accountId, accountTypeName)
                                .map(accountTypeMap -> {
                                    accountTypeMap.setAccountId(toPatchAccountTypeMap.getAccountId());
                                    accountTypeMap.setAccountTypeId(toPatchAccountTypeMap.getAccountTypeId());
                                    accountTypeMap.setUpdatedAt(toPatchAccountTypeMap.getUpdatedAt());
                                    accountTypeMap.setCreatedAt(toPatchAccountTypeMap.getCreatedAt());
                                    return accountTypeMap;
                                })
                                .flatMap(accountTypeMap -> accountTypeMapRepository.save(accountTypeMap))
                                .map(accountTypeMap -> Result
                                        .<AccountTypeMap>builder()
                                        .data(accountTypeMap)
                                        .code(200)
                                        .message("AccountTypeMap management patchOneByAccountIdAndAccountTypeName is succeed.")
                                        .build()
                                )
                )
                .onErrorReturn(Result
                        .<AccountTypeMap>builder()
                        .code(500)
                        .message("AccountTypeMap management patchOneByAccountIdAndAccountTypeName is failed.")
                        .build()
                );
    }

    public Mono<Result<AccountTypeMap>> deleteOneByAccountIdAndAccountTypeName(UUID accountId, String accountTypeName) {
        return accountTypeMapRepository
                .findOneByAccountIdAndAccountTypeName(accountId, accountTypeName)
                .flatMap((accountTypeMap) -> accountTypeMapRepository.deleteById(accountTypeMap.getId()).thenReturn(accountTypeMap))
                .map(accountTypeMap -> Result
                        .<AccountTypeMap>builder()
                        .data(accountTypeMap)
                        .code(200)
                        .message("AccountTypeMap management deleteOneByAccountIdAndAccountTypeName is succeed.")
                        .build()
                )
                .switchIfEmpty(Mono
                        .just(
                                Result.
                                        <AccountTypeMap>builder()
                                        .code(404)
                                        .message("AccountTypeMap management deleteOneByAccountIdAndAccountTypeName is failed, accountTypeMap not found.")
                                        .build()
                        )
                )
                .onErrorReturn(Result.
                        <AccountTypeMap>builder()
                        .code(500)
                        .message("AccountTypeMap management deleteOneByAccountIdAndAccountTypeName is failed.")
                        .build()
                );
    }

}
