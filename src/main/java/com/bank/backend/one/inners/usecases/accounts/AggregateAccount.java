package com.bank.backend.one.inners.usecases.accounts;

import com.bank.backend.one.inners.models.daos.Account;
import com.bank.backend.one.inners.models.daos.AccountTypeMap;
import com.bank.backend.one.inners.models.dtos.Result;
import com.bank.backend.one.inners.models.dtos.aggregates.AccountTypeMapAggregate;
import com.bank.backend.one.inners.models.dtos.requests.accounts.DeleteFirstAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.FindFirstAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.PatchFirstAccountRequest;
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


    public Mono<Result<AccountTypeMapAggregate>> findFirstByAccountId(FindFirstAccountRequest request) {
        return accountTypeRepository
                .findFirstByName(request.getTypeName())
                .flatMap(accountType -> {
                    return accountTypeMapRepository
                            .findFirstByAccountIdAndAccountTypeName(request.getAccountId(), accountType.getName())
                            .flatMap(accountTypeMap -> {
                                return accountRepository
                                        .findFirstById(request.getAccountId())
                                        .map(account -> Result
                                                .<AccountTypeMapAggregate>builder()
                                                .data(AccountTypeMapAggregate
                                                        .builder()
                                                        .account(account)
                                                        .accountType(accountType)
                                                        .build()
                                                )
                                                .code(200)
                                                .message("AdminAccount findFirstByAccountId succeed.")
                                                .build()
                                        );
                            })
                            .switchIfEmpty(Mono
                                    .just(Result
                                            .<AccountTypeMapAggregate>builder()
                                            .code(404)
                                            .message("AdminAccount findFirstByAccountId failed, accountTypeMap not found.")
                                            .build()
                                    )
                            );
                })
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMapAggregate>builder()
                                .code(404)
                                .message("AdminAccount findFirstByAccountId failed, accountType not found.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<AccountTypeMapAggregate>builder()
                        .code(500)
                        .message("AdminAccount findFirstByAccountId failed.")
                        .build()
                );
    }

    public Mono<Result<AccountTypeMapAggregate>> saveOne(SaveOneAccountRequest request) {
        return accountTypeRepository
                .findFirstByName("admin")
                .flatMap(accountType -> {
                    return accountRepository
                            .findFirstByEmail(request.getEmail())
                            .map(account -> Result
                                    .<AccountTypeMapAggregate>builder()
                                    .code(409)
                                    .data(AccountTypeMapAggregate
                                            .builder()
                                            .account(account)
                                            .accountType(accountType)
                                            .build()
                                    )
                                    .message("AdminAccount saveOne failed, account already exists by email.")
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
                                                                        .accountTypeName(accountType.getName())
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
                                                                        .message("AdminAccount saveOne succeed.")
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
                                .message("AdminAccount saveOne failed, accountType not found.")
                                .build()
                        )
                )
                .onErrorReturn(
                        Result
                                .<AccountTypeMapAggregate>builder()
                                .code(500)
                                .message("AdminAccount saveOne failed.")
                                .build()
                );
    }

    public Mono<Result<AccountTypeMapAggregate>> patchFirstByAccountId(PatchFirstAccountRequest request) {
        return accountTypeRepository
                .findFirstByName(request.getTypeName())
                .flatMap(accountType -> accountTypeMapRepository.findFirstByAccountIdAndAccountTypeName(request.getAccountId(), accountType.getName())
                        .flatMap(accountTypeMap -> accountRepository
                                .findFirstByEmail(request.getEmail())
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
                                        .message("AdminAccount patchFirstByAccountId failed, account already exists by email.")
                                        .build()
                                )
                                .switchIfEmpty(
                                        accountRepository
                                                .findFirstById(accountTypeMap.getAccountId())
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
                                                        .message("AdminAccount patchFirstByAccountId succeed.")
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
                                                        .message("AdminAccount patchFirstByAccountId failed, accountTypeMap not found.")
                                                        .build()

                                        )
                        )
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMapAggregate>builder()
                                .code(404)
                                .message("AdminAccount patchFirstByAccountId failed, accountType not found.")
                                .build()
                        )
                )
                .onErrorReturn(
                        Result
                                .<AccountTypeMapAggregate>builder()
                                .code(500)
                                .message("AdminAccount patchFirstByAccountId failed.")
                                .build()
                );
    }

    public Mono<Result<AccountTypeMapAggregate>> deleteFirstByAccountId(DeleteFirstAccountRequest request) {
        return accountTypeRepository
                .findFirstByName(request.getTypeName())
                .flatMap(accountType -> accountTypeMapRepository
                        .findFirstByAccountIdAndAccountTypeName(request.getAccountId(), accountType.getName())
                        .flatMap(accountTypeMap -> accountRepository
                                .findFirstById(accountTypeMap.getAccountId())
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
                                        .message("AdminAccount deleteFirstByAccountId succeed.")
                                        .build()
                                )
                                .switchIfEmpty(Mono
                                        .just(Result
                                                .<AccountTypeMapAggregate>builder()
                                                .code(404)
                                                .message("AdminAccount deleteFirstByAccountId failed, account not found.")
                                                .build()
                                        )

                                )
                        )
                        .switchIfEmpty(Mono
                                .just(Result
                                        .<AccountTypeMapAggregate>builder()
                                        .code(404)
                                        .message("AdminAccount deleteFirstByAccountId failed, accountTypeMap not found.")
                                        .build()
                                )
                        )
                )
                .switchIfEmpty(Mono
                        .just(Result
                                .<AccountTypeMapAggregate>builder()
                                .code(404)
                                .message("AdminAccount deleteFirstByAccountId failed, accountType not found.")
                                .build()
                        )
                )
                .onErrorReturn(Result
                        .<AccountTypeMapAggregate>builder()
                        .code(500)
                        .message("AdminAccount deleteFirstByAccountId failed.")
                        .build()
                );
    }
}
