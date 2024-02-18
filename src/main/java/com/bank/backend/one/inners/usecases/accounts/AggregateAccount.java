package com.bank.backend.one.inners.usecases.accounts;

import com.bank.backend.one.inners.models.daos.Account;
import com.bank.backend.one.inners.models.daos.AccountTypeMap;
import com.bank.backend.one.inners.models.dtos.Result;
import com.bank.backend.one.inners.models.dtos.aggregates.AccountTypeMapAggregate;
import com.bank.backend.one.inners.models.dtos.requests.accounts.DeleteOneAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.FindOneAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.PatchOneAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.SaveOneAccountRequest;
import com.bank.backend.one.outers.repositories.AccountRepository;
import com.bank.backend.one.outers.repositories.AccountTypeMapRepository;
import com.bank.backend.one.outers.repositories.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AggregateAccount {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountTypeRepository accountTypeRepository;
    @Autowired
    private AccountTypeMapRepository accountTypeMapRepository;


    public Mono<Result<AccountTypeMapAggregate>> findOneByAccountId(FindOneAccountRequest request) {
        return accountTypeRepository
                .findOneByName(request.getTypeName())
                .flatMap(accountType -> {
                    return accountTypeMapRepository
                            .findOneByAccountIdAndAccountTypeName(request.getAccountId(), accountType.getName())
                            .flatMap(accountTypeMap -> {
                                return accountRepository
                                        .findOneById(request.getAccountId())
                                        .map(account -> Result
                                                .<AccountTypeMapAggregate>builder()
                                                .data(AccountTypeMapAggregate
                                                        .builder()
                                                        .account(account)
                                                        .accountType(accountType)
                                                        .build()
                                                )
                                                .code(200)
                                                .message("AdminAccount findOneByAccountId is succeed.")
                                                .build()
                                        );
                            })
                            .switchIfEmpty(Mono
                                    .just(Result
                                            .<AccountTypeMapAggregate>builder()
                                            .code(404)
                                            .message("AdminAccount findOneByAccountId is failed, accountTypeMap not found.")
                                            .build()
                                    )
                            );
                })
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMapAggregate>builder()
                                .code(404)
                                .message("AdminAccount findOneByAccountId is failed, accountType not found.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<AccountTypeMapAggregate>builder()
                        .code(500)
                        .message("AdminAccount findOneByAccountId is failed.")
                        .build()
                );
    }

    public Mono<Result<AccountTypeMapAggregate>> saveOne(SaveOneAccountRequest request) {
        return accountTypeRepository
                .findOneByName("admin")
                .flatMap(accountType -> {
                    return accountRepository
                            .findOneByEmail(request.getEmail())
                            .map(account -> Result
                                    .<AccountTypeMapAggregate>builder()
                                    .code(409)
                                    .data(AccountTypeMapAggregate
                                            .builder()
                                            .account(account)
                                            .accountType(accountType)
                                            .build()
                                    )
                                    .message("AdminAccount saveOne is failed, account already exists by email.")
                                    .build()
                            )
                            .switchIfEmpty(
                                    accountRepository
                                            .save(Account
                                                    .builder()
                                                    .id(UUID.randomUUID())
                                                    .email(request.getEmail())
                                                    .password(request.getPassword())
                                                    .createdAt(OffsetDateTime.now())
                                                    .updatedAt(OffsetDateTime.now())
                                                    .build()
                                            )
                                            .flatMap(account -> {
                                                        return accountTypeMapRepository
                                                                .save(AccountTypeMap
                                                                        .builder()
                                                                        .id(UUID.randomUUID())
                                                                        .accountId(account.getId())
                                                                        .accountTypeId(accountType.getId())
                                                                        .createdAt(OffsetDateTime.now())
                                                                        .updatedAt(OffsetDateTime.now())
                                                                        .build()
                                                                ).map(accountTypeMap -> Result
                                                                        .<AccountTypeMapAggregate>builder()
                                                                        .data(AccountTypeMapAggregate
                                                                                .builder()
                                                                                .account(account)
                                                                                .accountType(accountType)
                                                                                .build()
                                                                        )
                                                                        .code(201)
                                                                        .message("AdminAccount saveOne is succeed.")
                                                                        .build()
                                                                );
                                                    }
                                            )
                            );
                })
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMapAggregate>builder()
                                .code(404)
                                .message("AdminAccount saveOne is failed, accountType not found.")
                                .build()
                        )
                )
                .onErrorReturn(
                        Result
                                .<AccountTypeMapAggregate>builder()
                                .code(500)
                                .message("AdminAccount saveOne is failed.")
                                .build()
                );
    }

    public Mono<Result<AccountTypeMapAggregate>> patchOneByAccountId(PatchOneAccountRequest request) {
        return accountTypeRepository
                .findOneByName(request.getTypeName())
                .flatMap(accountType -> accountTypeMapRepository.findOneByAccountIdAndAccountTypeName(request.getAccountId(), accountType.getName())
                        .flatMap(accountTypeMap -> accountRepository
                                .findOneByEmail(request.getEmail())
                                .map(account -> Result
                                        .<AccountTypeMapAggregate>builder()
                                        .code(409)
                                        .data(
                                                AccountTypeMapAggregate
                                                        .builder()
                                                        .account(account)
                                                        .accountType(accountType)
                                                        .build()
                                        )
                                        .message("AdminAccount patchOneByAccountId is failed, account already exists by email.")
                                        .build()
                                )
                                .switchIfEmpty(
                                        accountRepository
                                                .findOneById(accountTypeMap.getAccountId())
                                                .map(account -> {
                                                    account.setEmail(request.getEmail());
                                                    account.setPassword(request.getPassword());
                                                    account.setUpdatedAt(OffsetDateTime.now());
                                                    return account;
                                                })
                                                .flatMap(accountRepository::save)
                                                .map(account -> Result
                                                        .<AccountTypeMapAggregate>builder()
                                                        .data(AccountTypeMapAggregate
                                                                .builder()
                                                                .account(account)
                                                                .accountType(accountType)
                                                                .build()
                                                        )
                                                        .code(200)
                                                        .message("AdminAccount patchOneByAccountId is succeed.")
                                                        .build()
                                                )
                                )
                        )
                        .switchIfEmpty(
                                Mono
                                        .just(
                                                Result
                                                        .<AccountTypeMapAggregate>builder()
                                                        .code(404)
                                                        .message("AdminAccount patchOneByAccountId is failed, accountTypeMap not found.")
                                                        .build()

                                        )
                        )
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMapAggregate>builder()
                                .code(404)
                                .message("AdminAccount patchOneByAccountId is failed, accountType not found.")
                                .build()
                        )
                )
                .onErrorReturn(
                        Result
                                .<AccountTypeMapAggregate>builder()
                                .code(500)
                                .message("AdminAccount patchOneByAccountId is failed.")
                                .build()
                );
    }

    public Mono<Result<AccountTypeMapAggregate>> deleteOneByAccountId(DeleteOneAccountRequest request) {
        return accountTypeRepository
                .findOneByName(request.getTypeName())
                .flatMap(accountType -> accountTypeMapRepository
                        .findOneByAccountIdAndAccountTypeName(request.getAccountId(), accountType.getName())
                        .flatMap(accountTypeMap -> accountRepository
                                .findOneById(accountTypeMap.getAccountId())
                                .flatMap(account -> accountTypeMapRepository.deleteById(accountTypeMap.getAccountId()).thenReturn(account))
                                .flatMap(account -> accountRepository.deleteById(account.getId()).thenReturn(account))
                                .map(account -> Result
                                        .<AccountTypeMapAggregate>builder()
                                        .data(AccountTypeMapAggregate
                                                .builder()
                                                .account(account)
                                                .accountType(accountType)
                                                .build()
                                        )
                                        .code(200)
                                        .message("AdminAccount deleteOneByAccountId is succeed.")
                                        .build()
                                )
                                .switchIfEmpty(Mono
                                        .just(Result
                                                .<AccountTypeMapAggregate>builder()
                                                .code(404)
                                                .message("AdminAccount deleteOneByAccountId is failed, account not found.")
                                                .build()
                                        )

                                )
                        )
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<AccountTypeMapAggregate>builder()
                                        .code(404)
                                        .message("AdminAccount deleteOneByAccountId is failed, accountTypeMap not found.")
                                        .build()
                                )
                        )
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMapAggregate>builder()
                                .code(404)
                                .message("AdminAccount deleteOneByAccountId is failed, accountType not found.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<AccountTypeMapAggregate>builder()
                        .code(500)
                        .message("AdminAccount deleteOneByAccountId is failed.")
                        .build()
                );
    }
}
