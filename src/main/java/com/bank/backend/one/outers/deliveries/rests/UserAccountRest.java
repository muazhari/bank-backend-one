package com.bank.backend.one.outers.deliveries.rests;


import com.bank.backend.one.inners.models.dtos.Response;
import com.bank.backend.one.inners.models.dtos.aggregates.AccountTypeMapAggregate;
import com.bank.backend.one.inners.models.dtos.requests.accounts.DeleteOneAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.FindOneAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.PatchOneAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.SaveOneAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.users.PatchOneUserAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.users.SaveOneUserAccountRequest;
import com.bank.backend.one.inners.usecases.accounts.AggregateAccount;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(
        value = "/accounts/users"
)
@SecurityRequirement(name = "bearerAuth")
public class UserAccountRest {

    @Autowired
    private AggregateAccount aggregateAccount;

    @GetMapping(
            value = "/{accountId}"
    )
    public Mono<ResponseEntity<Response<AccountTypeMapAggregate>>> getUserAccountByAccountId(
            @PathVariable(
                    value = "accountId"
            )
            UUID accountId
    ) {
        return aggregateAccount
                .findOneByAccountId(FindOneAccountRequest
                        .builder()
                        .accountId(accountId)
                        .typeName("user")
                        .build()
                )
                .map(result -> ResponseEntity
                        .status((Integer) result.getCode())
                        .body(Response
                                .<AccountTypeMapAggregate>builder()
                                .data(result.getData())
                                .message(result.getMessage())
                                .build()
                        )
                );
    }

    @PostMapping(
            value = ""
    )
    public Mono<ResponseEntity<Response<AccountTypeMapAggregate>>> saveOneUserAccountRequest(
            @RequestBody
            SaveOneUserAccountRequest request
    ) {
        return aggregateAccount
                .saveOne(SaveOneAccountRequest
                        .builder()
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .typeName("user")
                        .build()
                )
                .map(result -> ResponseEntity
                        .status((Integer) result.getCode())
                        .body(Response
                                .<AccountTypeMapAggregate>builder()
                                .data(result.getData())
                                .message(result.getMessage())
                                .build()
                        )
                );
    }

    @PatchMapping(
            value = "/{accountId}"
    )
    public Mono<ResponseEntity<Response<AccountTypeMapAggregate>>> patchUserAccountByAccountId(
            @PathVariable(
                    value = "accountId"
            )
            UUID accountId,
            @RequestBody
            PatchOneUserAccountRequest request
    ) {
        return aggregateAccount
                .patchOneByAccountId(PatchOneAccountRequest
                        .builder()
                        .accountId(accountId)
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .typeName("user")
                        .build()
                )
                .map(result -> ResponseEntity
                        .status((Integer) result.getCode())
                        .body(Response
                                .<AccountTypeMapAggregate>builder()
                                .data(result.getData())
                                .message(result.getMessage())
                                .build()
                        )
                );
    }

    @DeleteMapping(
            value = "/{accountId}"
    )
    public Mono<ResponseEntity<Response<AccountTypeMapAggregate>>> deleteUserAccountByAccountId(
            @PathVariable(
                    value = "accountId"
            )
            UUID accountId
    ) {
        return aggregateAccount
                .deleteOneByAccountId(DeleteOneAccountRequest
                        .builder()
                        .accountId(accountId)
                        .typeName("user")
                        .build()
                )
                .map(result -> ResponseEntity
                        .status((Integer) result.getCode())
                        .body(Response
                                .<AccountTypeMapAggregate>builder()
                                .data(result.getData())
                                .message(result.getMessage())
                                .build()
                        )
                );
    }

}
