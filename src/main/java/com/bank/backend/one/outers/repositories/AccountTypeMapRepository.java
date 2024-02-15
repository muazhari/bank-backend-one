package com.bank.backend.one.outers.repositories;

import com.bank.backend.one.inners.models.daos.AccountTypeMap;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface AccountTypeMapRepository extends R2dbcRepository<AccountTypeMap, UUID> {
    Mono<AccountTypeMap> findFirstByAccountIdAndAccountTypeId(UUID accountId, UUID accountTypeId);
}
