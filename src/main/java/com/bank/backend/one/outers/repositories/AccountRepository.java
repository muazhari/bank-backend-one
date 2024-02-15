package com.bank.backend.one.outers.repositories;

import com.bank.backend.one.inners.models.daos.Account;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface AccountRepository extends R2dbcRepository<Account, UUID> {

    Mono<Account> findFirstByEmailAndPassword(String email, String password);

    Mono<Account> findFirstByEmail(String email);

    Mono<Account> findFirstById(UUID id);
}
