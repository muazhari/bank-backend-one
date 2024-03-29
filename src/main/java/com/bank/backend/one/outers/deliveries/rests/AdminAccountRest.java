package com.bank.backend.one.outers.deliveries.rests;


import com.bank.backend.one.inners.models.dtos.Response;
import com.bank.backend.one.inners.models.dtos.aggregates.AccountTypeMapAggregate;
import com.bank.backend.one.inners.models.dtos.requests.accounts.DeleteOneAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.FindOneAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.PatchOneAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.SaveOneAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.admins.PatchOneAdminAccountRequest;
import com.bank.backend.one.inners.models.dtos.requests.accounts.admins.SaveOneAdminAccountRequest;
import com.bank.backend.one.inners.usecases.accounts.AggregateAccount;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(
        value = "/accounts/admins"
)
@SecurityRequirement(
        name = "bearerAuth"
)
public class AdminAccountRest {

    @Autowired
    private AggregateAccount aggregateAccount;

    @GetMapping(
            value = "/{accountId}"
    )
    public Mono<ResponseEntity<Response<AccountTypeMapAggregate>>> getAdminAccountByAccountId(
            @PathVariable(
                    value = "accountId"
            )
            UUID accountId
    ) {
        return aggregateAccount
                .findOneByAccountId(FindOneAccountRequest
                        .builder()
                        .accountId(accountId)
                        .typeName("admin")
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
    public Mono<ResponseEntity<Response<AccountTypeMapAggregate>>> saveOneAdminAccountRequest(
            @RequestBody
            SaveOneAdminAccountRequest request
    ) {
        return aggregateAccount
                .saveOne(SaveOneAccountRequest
                        .builder()
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .typeName("admin")
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
    public Mono<ResponseEntity<Response<AccountTypeMapAggregate>>> patchAdminAccountByAccountId(
            @PathVariable(
                    value = "accountId"
            )
            UUID accountId,
            @RequestBody
            PatchOneAdminAccountRequest request
    ) {
        return aggregateAccount
                .patchOneByAccountId(PatchOneAccountRequest
                        .builder()
                        .accountId(accountId)
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .typeName("admin")
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
    public Mono<ResponseEntity<Response<AccountTypeMapAggregate>>> deleteAdminAccountByAccountId(
            @PathVariable(
                    value = "accountId"
            )
            UUID accountId
    ) {
        return aggregateAccount.deleteOneByAccountId(DeleteOneAccountRequest
                        .builder()
                        .accountId(accountId)
                        .typeName("admin")
                        .build()
                )
                .map(result -> ResponseEntity
                        .status((Integer) result.getCode())
                        .body(Response.<AccountTypeMapAggregate>builder()
                                .data(result.getData())
                                .message(result.getMessage())
                                .build()
                        )
                );
    }

}
