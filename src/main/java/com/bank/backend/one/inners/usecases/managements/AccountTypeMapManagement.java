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
                        .message("AccountTypeMapManagement findOneByAccountId is succeed.")
                        .build()
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMap>builder()
                                .code(404)
                                .message("AccountTypeMapManagement findOneByAccountId is failed, accountTypeMap is not found by accountId.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<AccountTypeMap>builder()
                        .code(500)
                        .message("AccountTypeMapManagement findOneByAccountId is failed.")
                        .build()
                );
    }

    public Mono<Result<AccountTypeMap>> findOneByAccountIdAndAccountTypeName(UUID accountId, String accountTypeName) {
        return accountTypeMapRepository
                .findOneByAccountIdAndAccountTypeName(accountId, accountTypeName)
                .map(accountTypeMap -> Result
                        .<AccountTypeMap>builder()
                        .data(accountTypeMap)
                        .code(200)
                        .message("AccountTypeMapManagement findOneByAccountIdAndAccountTypeName is succeed.")
                        .build()
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMap>builder()
                                .code(404)
                                .message("AccountTypeMapManagement findOneByAccountIdAndAccountTypeName is failed, accountTypeMap is not found by accountId and accountTypeName.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<AccountTypeMap>builder()
                        .code(500)
                        .message("AccountTypeMapManagement findOneByAccountIdAndAccountTypeName is failed.")
                        .build()
                );
    }

    public Mono<Result<AccountTypeMap>> saveOne(AccountTypeMap toSaveAccountTypeMap) {
        return accountTypeMapRepository
                .findOneByAccountIdAndAccountTypeId(toSaveAccountTypeMap.getAccountId(), toSaveAccountTypeMap.getAccountTypeId())
                .flatMap(accountTypeMap -> Mono
                        .just(Result
                                .<AccountTypeMap>builder()
                                .code(409)
                                .data(accountTypeMap)
                                .message("AccountTypeMapManagement saveOne is failed, accountTypeMap already exists by accountId and accountTypeId.")
                                .build()
                        )
                )
                .switchIfEmpty(accountTypeMapRepository
                        .save(toSaveAccountTypeMap)
                        .map(savedAccountTypeMap -> Result
                                .<AccountTypeMap>builder()
                                .data(savedAccountTypeMap)
                                .code(201)
                                .message("AccountTypeMapManagement saveOne is succeed.")
                                .build()
                        )
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<AccountTypeMap>builder()
                                        .code(404)
                                        .message("AccountTypeMapManagement saveOne is failed, accountTypeMap is not saved.")
                                        .build()
                                )
                        )
                )
                .onErrorReturn(Result
                        .<AccountTypeMap>builder()
                        .code(500)
                        .message("AccountTypeMapManagement saveOne is failed.")
                        .build()
                );
    }

    public Mono<Result<AccountTypeMap>> patchOneByAccountIdAndAccountTypeName(UUID accountId, String accountTypeName, AccountTypeMap toPatchAccountTypeMap) {
        return accountTypeMapRepository
                .findOneByAccountIdAndAccountTypeName(accountId, accountTypeName)
                .map(accountTypeMap -> Result
                        .<AccountTypeMap>builder()
                        .code(409)
                        .data(accountTypeMap)
                        .message("AccountTypeMapManagement patchOneByAccountIdAndAccountTypeName is failed, accountTypeMap already exists by accountId and accountTypeName.")
                        .build()
                )
                .switchIfEmpty(accountTypeMapRepository
                        .findOneByAccountIdAndAccountTypeName(accountId, accountTypeName)
                        .flatMap(accountTypeMap -> {
                            accountTypeMap.setId(toPatchAccountTypeMap.getId());
                            accountTypeMap.setAccountId(toPatchAccountTypeMap.getAccountId());
                            accountTypeMap.setAccountTypeId(toPatchAccountTypeMap.getAccountTypeId());
                            accountTypeMap.setUpdatedAt(toPatchAccountTypeMap.getUpdatedAt());
                            accountTypeMap.setCreatedAt(toPatchAccountTypeMap.getCreatedAt());
                            return accountTypeMapRepository.save(accountTypeMap)
                                    .map(savedAccountTypeMap -> Result
                                            .<AccountTypeMap>builder()
                                            .data(savedAccountTypeMap)
                                            .code(200)
                                            .message("AccountTypeMapManagement patchOneByAccountIdAndAccountTypeName is succeed.")
                                            .build()
                                    )
                                    .switchIfEmpty(Mono
                                            .just(Result
                                                    .<AccountTypeMap>builder()
                                                    .code(404)
                                                    .message("AccountTypeMapManagement patchOneByAccountIdAndAccountTypeName is failed, accountTypeMap is not saved.")
                                                    .build()
                                            )
                                    );
                        })
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<AccountTypeMap>builder()
                                        .code(404)
                                        .message("AccountTypeMapManagement patchOneByAccountIdAndAccountTypeName is failed, accountTypeMap is not found by accountId and accountTypeName.")
                                        .build()
                                )

                        )
                )
                .onErrorReturn(Result
                        .<AccountTypeMap>builder()
                        .code(500)
                        .message("AccountTypeMapManagement patchOneByAccountIdAndAccountTypeName is failed.")
                        .build()
                );
    }

    public Mono<Result<AccountTypeMap>> deleteOneByAccountIdAndAccountTypeName(UUID accountId, String accountTypeName) {
        return accountTypeMapRepository
                .findOneByAccountIdAndAccountTypeName(accountId, accountTypeName)
                .flatMap((accountTypeMap) -> accountTypeMapRepository
                        .deleteById(accountTypeMap.getId())
                        .map(deletedAccountTypeMap -> Result
                                .<AccountTypeMap>builder()
                                .data(deletedAccountTypeMap)
                                .code(200)
                                .message("AccountTypeMapManagement deleteOneByAccountIdAndAccountTypeName is succeed.")
                                .build()
                        )
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<AccountTypeMap>builder()
                                        .code(404)
                                        .message("AccountTypeMapManagement deleteOneByAccountIdAndAccountTypeName is failed, accountTypeMap is not deleted.")
                                        .build()
                                )
                        )
                )
                .switchIfEmpty(Mono
                        .just(Result.
                                <AccountTypeMap>builder()
                                .code(404)
                                .message("AccountTypeMapManagement deleteOneByAccountIdAndAccountTypeName is failed, accountTypeMap is not found by id.")
                                .build()
                        )
                )
                .onErrorReturn(Result.
                        <AccountTypeMap>builder()
                        .code(500)
                        .message("AccountTypeMapManagement deleteOneByAccountIdAndAccountTypeName is failed.")
                        .build()
                );
    }

}
