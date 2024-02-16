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

    public Mono<Result<AccountTypeMap>> findFirstByAccountId(UUID accountId) {
        return accountTypeMapRepository.findFirstByAccountId(accountId)
                .map(accountTypeMap -> Result.<AccountTypeMap>builder()
                        .data(accountTypeMap)
                        .code(200)
                        .message("AccountTypeMap management findFirstByAccountId succeed.")
                        .build())
                .switchIfEmpty(
                        Mono.just(Result.<AccountTypeMap>builder()
                                .code(404)
                                .message("AccountTypeMap management findFirstByAccountId failed, accountTypeMap not found.")
                                .build())
                )
                .onErrorReturn(
                        Result.<AccountTypeMap>builder()
                                .code(500)
                                .message("AccountTypeMap management findFirstByAccountId failed.")
                                .build()
                );
    }

    public Mono<Result<AccountTypeMap>> findFirstByAccountIdAndAccountTyped(UUID accountId, String accountTypeName) {
        return accountTypeMapRepository.findFirstByAccountIdAndAccountTypeName(accountId, accountTypeName)
                .map(accountTypeMap -> Result.<AccountTypeMap>builder()
                        .data(accountTypeMap)
                        .code(200)
                        .message("AccountTypeMap management findFirstByAccountIdAndAccountTypeName succeed.")
                        .build())
                .switchIfEmpty(
                        Mono.just(Result.<AccountTypeMap>builder()
                                .code(404)
                                .message("AccountTypeMap management findFirstByAccountIdAndAccountTypeName failed, accountTypeMap not found.")
                                .build())
                )
                .onErrorReturn(
                        Result.<AccountTypeMap>builder()
                                .code(500)
                                .message("AccountTypeMap management findFirstByAccountIdAndAccountTypeName failed.")
                                .build()
                );
    }

    public Mono<Result<AccountTypeMap>> saveFirst(AccountTypeMap saveAccountTypeMap) {
        return accountTypeMapRepository.save(saveAccountTypeMap)
                .map(accountTypeMap -> Result.<AccountTypeMap>builder()
                        .data(accountTypeMap)
                        .code(201)
                        .message("AccountTypeMap management saveFirst succeed.")
                        .build()
                )
                .onErrorReturn(
                        Result.<AccountTypeMap>builder()
                                .code(500)
                                .message("AccountTypeMap management saveFirst failed.")
                                .build()
                );
    }

    public Mono<Result<AccountTypeMap>> patchFirstByAccountIdAndAccountTypeName(UUID accountId, String accountTypeName, AccountTypeMap patchAccountTypeMap) {
        return accountTypeMapRepository.findFirstByAccountIdAndAccountTypeName(patchAccountTypeMap.getAccountId(), patchAccountTypeMap.getAccountTypeName())
                .map(accountTypeMap -> Result.<AccountTypeMap>builder()
                        .code(409)
                        .data(accountTypeMap)
                        .message("AccountTypeMap management patchFirstByAccountIdAndAccountTypeName failed, accountTypeMap already exists by accountId and accountTypeName.")
                        .build()
                )
                .switchIfEmpty(
                        accountTypeMapRepository.findFirstByAccountIdAndAccountTypeName(accountId, accountTypeName)
                                .map(accountTypeMap -> {
                                    accountTypeMap.setAccountId(patchAccountTypeMap.getAccountId());
                                    accountTypeMap.setAccountTypeName(patchAccountTypeMap.getAccountTypeName());
                                    accountTypeMap.setUpdatedAt(patchAccountTypeMap.getUpdatedAt());
                                    accountTypeMap.setCreatedAt(patchAccountTypeMap.getCreatedAt());
                                    return accountTypeMap;
                                })
                                .flatMap(accountTypeMap -> accountTypeMapRepository.save(accountTypeMap))
                                .map(accountTypeMap -> Result.<AccountTypeMap>builder()
                                        .data(accountTypeMap)
                                        .code(200)
                                        .message("AccountTypeMap management patchFirstByAccountIdAndAccountTypeName succeed.")
                                        .build()
                                )
                )
                .onErrorReturn(
                        Result.<AccountTypeMap>builder()
                                .code(500)
                                .message("AccountTypeMap management patchFirstByAccountIdAndAccountTypeName failed.")
                                .build()
                );
    }

    public Mono<Result<AccountTypeMap>> deleteFirstByAccountIdAndAccountTypeName(UUID accountId, String accountTypeName) {
        return accountTypeMapRepository.findFirstByAccountIdAndAccountTypeName(accountId, accountTypeName)
                .flatMap((accountTypeMap) -> accountTypeMapRepository.deleteById(accountTypeMap.getId()).thenReturn(accountTypeMap))
                .map(accountTypeMap -> Result.<AccountTypeMap>builder()
                        .data(accountTypeMap)
                        .code(200)
                        .message("AccountTypeMap management deleteFirstByAccountIdAndAccountTypeName succeed.")
                        .build()
                )
                .switchIfEmpty(
                        Mono.just(Result.<AccountTypeMap>builder()
                                .code(404)
                                .message("AccountTypeMap management deleteFirstByAccountIdAndAccountTypeName failed, accountTypeMap not found.")
                                .build())
                )
                .onErrorReturn(
                        Result.<AccountTypeMap>builder()
                                .code(500)
                                .message("AccountTypeMap management deleteFirstByAccountIdAndAccountTypeName failed.")
                                .build()
                );
    }

}
