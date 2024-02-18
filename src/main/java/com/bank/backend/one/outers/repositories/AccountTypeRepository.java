package com.bank.backend.one.outers.repositories;

import com.bank.backend.one.inners.models.daos.AccountType;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface AccountTypeRepository extends R2dbcRepository<AccountType, UUID> {

    Mono<AccountType> findOneById(UUID id);

    Mono<AccountType> findOneByName(String name);

    Mono<AccountType> deleteByName(String name);
}
