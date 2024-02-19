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
                .flatMap(accountType -> accountTypeMapRepository
                        .findOneByAccountIdAndAccountTypeName(request.getAccountId(), accountType.getName())
                        .flatMap(accountTypeMap -> accountRepository
                                .findOneById(accountTypeMap.getAccountId())
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
                                )
                                .switchIfEmpty(Mono
                                        .just(Result
                                                .<AccountTypeMapAggregate>builder()
                                                .code(404)
                                                .message("AdminAccount findOneByAccountId is failed, account is not found by accountId.")
                                                .build()
                                        )
                                )
                        )
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<AccountTypeMapAggregate>builder()
                                        .code(404)
                                        .message("AdminAccount findOneByAccountId is failed, accountTypeMap is not found by accountId and accountTypeName.")
                                        .build()
                                )
                        )
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMapAggregate>builder()
                                .code(404)
                                .message("AdminAccount findOneByAccountId is failed, accountType is not found by accountTypeName.")
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
                .findOneByName(request.getTypeName())
                .flatMap(accountType -> accountRepository
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
                        .switchIfEmpty(Mono
                                .just(Account
                                        .builder()
                                        .id(UUID.randomUUID())
                                        .email(request.getEmail())
                                        .password(request.getPassword())
                                        .createdAt(OffsetDateTime.now())
                                        .updatedAt(OffsetDateTime.now())
                                        .build()
                                )
                                .flatMap(toSaveAccount -> accountRepository
                                        .save(toSaveAccount)
                                        .flatMap(savedAccount -> accountTypeMapRepository
                                                .save(AccountTypeMap
                                                        .builder()
                                                        .id(UUID.randomUUID())
                                                        .accountId(savedAccount.getId())
                                                        .accountTypeId(accountType.getId())
                                                        .createdAt(OffsetDateTime.now())
                                                        .updatedAt(OffsetDateTime.now())
                                                        .build()
                                                )
                                                .map(savedAccountTypeMap -> Result
                                                        .<AccountTypeMapAggregate>builder()
                                                        .data(AccountTypeMapAggregate
                                                                .builder()
                                                                .account(savedAccount)
                                                                .accountType(accountType)
                                                                .build()
                                                        )
                                                        .code(201)
                                                        .message("AdminAccount saveOne is succeed.")
                                                        .build()
                                                )
                                                .switchIfEmpty(Mono
                                                        .just(Result
                                                                .<AccountTypeMapAggregate>builder()
                                                                .code(500)
                                                                .message("AdminAccount saveOne is failed, accountTypeMap not saved.")
                                                                .build()
                                                        )

                                                )
                                        )
                                        .switchIfEmpty(Mono
                                                .just(Result
                                                        .<AccountTypeMapAggregate>builder()
                                                        .code(404)
                                                        .message("AdminAccount saveOne is failed, account is not saved.")
                                                        .build()
                                                )

                                        )
                                )
                        )
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<AccountTypeMapAggregate>builder()
                                        .code(404)
                                        .message("AdminAccount saveOne is failed, accountType is not found by email.")
                                        .build()
                                )
                        )
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMapAggregate>builder()
                                .code(404)
                                .message("AdminAccount saveOne is failed, accountType is not found by accountTypeName.")
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
                .flatMap(accountType -> accountTypeMapRepository
                        .findOneByAccountIdAndAccountTypeName(request.getAccountId(), accountType.getName())
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
                                .switchIfEmpty(accountRepository
                                        .findOneById(accountTypeMap.getAccountId())
                                        .flatMap(account -> {
                                            account.setEmail(request.getEmail());
                                            account.setPassword(request.getPassword());
                                            account.setUpdatedAt(OffsetDateTime.now());
                                            return accountRepository
                                                    .save(account)
                                                    .map(savedAccount -> Result
                                                            .<AccountTypeMapAggregate>builder()
                                                            .data(AccountTypeMapAggregate
                                                                    .builder()
                                                                    .account(savedAccount)
                                                                    .accountType(accountType)
                                                                    .build()
                                                            )
                                                            .code(200)
                                                            .message("AdminAccount patchOneByAccountId is succeed.")
                                                            .build()
                                                    )
                                                    .switchIfEmpty(Mono
                                                            .just(Result
                                                                    .<AccountTypeMapAggregate>builder()
                                                                    .code(404)
                                                                    .message("AdminAccount patchOneByAccountId is failed, account is not saved.")
                                                                    .build()
                                                            )
                                                    );
                                        })
                                        .switchIfEmpty(Mono
                                                .just(Result
                                                        .<AccountTypeMapAggregate>builder()
                                                        .code(404)
                                                        .message("AdminAccount patchOneByAccountId is failed, account is not found by accountId.")
                                                        .build()
                                                )
                                        )
                                )
                        )
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<AccountTypeMapAggregate>builder()
                                        .code(404)
                                        .message("AdminAccount patchOneByAccountId is failed, accountTypeMap is not found by accountId and accountTypeName.")
                                        .build()
                                )
                        )
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMapAggregate>builder()
                                .code(404)
                                .message("AdminAccount patchOneByAccountId is failed, accountType is not found by accountTypeName.")
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
                                .flatMap(account -> {
                                    return accountTypeMapRepository
                                            .deleteById(accountTypeMap.getAccountId())
                                            .flatMap(deletedAccountTypeMap -> accountRepository
                                                    .deleteOneById(account.getId())
                                                    .map(deletedAccount -> Result
                                                            .<AccountTypeMapAggregate>builder()
                                                            .data(AccountTypeMapAggregate
                                                                    .builder()
                                                                    .account(deletedAccount)
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
                                                                    .message("AdminAccount deleteOneByAccountId is failed, account is not deleted.")
                                                                    .build()
                                                            )
                                                    )
                                            )
                                            .switchIfEmpty(Mono
                                                    .just(Result
                                                            .<AccountTypeMapAggregate>builder()
                                                            .code(404)
                                                            .message("AdminAccount deleteOneByAccountId is failed, accountTypeMap is not deleted.")
                                                            .build()
                                                    )
                                            );
                                })
                                .switchIfEmpty(Mono
                                        .just(Result
                                                .<AccountTypeMapAggregate>builder()
                                                .code(404)
                                                .message("AdminAccount deleteOneByAccountId is failed, account is not found by accountId.")
                                                .build()
                                        )
                                )
                        )
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<AccountTypeMapAggregate>builder()
                                        .code(404)
                                        .message("AdminAccount deleteOneByAccountId is failed, accountTypeMap is not found by accountId and accountTypeName.")
                                        .build()
                                )
                        )
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMapAggregate>builder()
                                .code(404)
                                .message("AdminAccount deleteOneByAccountId is failed, accountType is not found by accountTypeName.")
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
