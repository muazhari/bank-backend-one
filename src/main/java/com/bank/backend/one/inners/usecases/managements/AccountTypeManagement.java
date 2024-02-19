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
                        .message("AccountTypeManagement findOneByName is succeed.")
                        .build()
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountType>builder()
                                .code(404)
                                .message("AccountTypeManagement findOneByName is failed, accountType is not found by name.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<AccountType>builder()
                        .code(500)
                        .message("AccountTypeManagement findOneByName is failed.")
                        .build()
                );
    }

    public Mono<Result<AccountType>> saveOne(AccountType toSaveAccountType) {
        return accountTypeRepository
                .findOneByName(toSaveAccountType.getName())
                .flatMap(accountType -> Mono
                        .just(Result
                                .<AccountType>builder()
                                .code(409)
                                .data(accountType)
                                .message("AccountTypeManagement saveOne is failed, accountType already exists by name.")
                                .build()
                        )
                )
                .switchIfEmpty(
                        accountTypeRepository
                                .save(toSaveAccountType)
                                .map(savedAccountType -> Result
                                        .<AccountType>builder()
                                        .data(savedAccountType)
                                        .code(201)
                                        .message("AccountTypeManagement saveOne is succeed.")
                                        .build()
                                )
                                .switchIfEmpty(Mono
                                        .just(Result
                                                .<AccountType>builder()
                                                .code(404)
                                                .message("AccountTypeManagement saveOne is failed, accountType is not saved.")
                                                .build()
                                        )
                                )
                )
                .onErrorReturn(
                        Result
                                .<AccountType>builder()
                                .code(500)
                                .message("AccountTypeManagement saveOne is failed.")
                                .build()
                );
    }

    public Mono<Result<AccountType>> patchOneByName(String name, AccountType toPatchAccountType) {
        return accountTypeRepository
                .findOneByName(name)
                .map(accountType -> Result
                        .<AccountType>builder()
                        .code(409)
                        .data(accountType)
                        .message("AccountTypeManagement patchOneByName is failed, accountType already exists by name.")
                        .build()
                )
                .switchIfEmpty(accountTypeRepository
                        .findOneByName(name)
                        .flatMap(accountType -> {
                            accountType.setId(toPatchAccountType.getId());
                            accountType.setName(toPatchAccountType.getName());
                            accountType.setUpdatedAt(toPatchAccountType.getUpdatedAt());
                            accountType.setCreatedAt(toPatchAccountType.getCreatedAt());
                            return accountTypeRepository
                                    .save(accountType)
                                    .map(savedAccountType -> Result
                                            .<AccountType>builder()
                                            .data(savedAccountType)
                                            .code(200)
                                            .message("AccountTypeManagement patchOneByName is succeed.")
                                            .build()
                                    )
                                    .switchIfEmpty(Mono
                                            .just(Result
                                                    .<AccountType>builder()
                                                    .code(404)
                                                    .message("AccountTypeManagement patchOneByName is failed, accountType is not saved.")
                                                    .build()
                                            )
                                    );
                        })
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<AccountType>builder()
                                        .code(404)
                                        .message("AccountTypeManagement patchOneByName is failed, accountType is not found by name.")
                                        .build()
                                )
                        )
                )
                .onErrorReturn(Result
                        .<AccountType>builder()
                        .code(500)
                        .message("AccountTypeManagement patchOneByName is failed.")
                        .build()
                );
    }

    public Mono<Result<AccountType>> deleteOneByName(String name) {
        return accountTypeRepository
                .findOneByName(name)
                .flatMap((accountType) -> accountTypeRepository
                        .deleteByName(accountType.getName())
                        .map(deletedAccountType -> Result
                                .<AccountType>builder()
                                .data(deletedAccountType)
                                .code(200)
                                .message("AccountTypeManagement deleteOneByName is succeed.")
                                .build()
                        )
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<AccountType>builder()
                                        .code(404)
                                        .message("AccountTypeManagement deleteOneByName is failed, accountType is not deleted.")
                                        .build()
                                )
                        )
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountType>builder()
                                .code(404)
                                .message("AccountTypeManagement deleteOneByName is failed, accountType is not found by name.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<AccountType>builder()
                        .code(500)
                        .message("AccountTypeManagement deleteOneByName is failed.")
                        .build()
                );
    }
}
