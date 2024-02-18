package com.bank.backend.one.outers.repositories;

import com.bank.backend.one.inners.models.daos.AccountTypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public class AccountTypeMapRepository {

    @Autowired
    @Qualifier(
            value = "postgresOneDatabaseClient"
    )
    private DatabaseClient postgresOneDatabaseClient;

    public Mono<AccountTypeMap> findOneByAccountId(UUID accountId) {
        return postgresOneDatabaseClient
                .sql("""
                        SELECT * FROM account_type_map WHERE account_id = :accountId LIMIT 1;
                        """)
                .bind("accountId", accountId)
                .map((row, rowMetadata) -> AccountTypeMap
                        .builder()
                        .id(row.get("id", UUID.class))
                        .accountId(row.get("account_id", UUID.class))
                        .accountTypeId(row.get("account_type_id", UUID.class))
                        .createdAt(row.get("created_at", OffsetDateTime.class))
                        .updatedAt(row.get("updated_at", OffsetDateTime.class))
                        .build()
                )
                .one();
    }

    public Mono<AccountTypeMap> findOneByAccountIdAndAccountTypeName(UUID accountId, String accountTypeName) {
        return postgresOneDatabaseClient
                .sql("""
                        SELECT atm1.*
                        FROM account_type_map atm1
                        JOIN account_type at1 ON atm1.account_type_id = at1.id
                        WHERE atm1.account_id = :accountId
                        AND at1.name = :accountTypeName
                        LIMIT 1;
                        """)
                .bind("accountId", accountId)
                .bind("accountTypeName", accountTypeName)
                .map((row, rowMetadata) -> AccountTypeMap
                        .builder()
                        .id(row.get("id", UUID.class))
                        .accountId(row.get("account_id", UUID.class))
                        .accountTypeId(row.get("account_type_id", UUID.class))
                        .createdAt(row.get("created_at", OffsetDateTime.class))
                        .updatedAt(row.get("updated_at", OffsetDateTime.class))
                        .build()
                )
                .one();
    }

    public Mono<AccountTypeMap> save(AccountTypeMap toSaveAccountTypeMap) {
        return postgresOneDatabaseClient
                .sql("""
                        INSERT INTO account_type_map (id, account_id, account_type_id, created_at, updated_at)
                        VALUES (:id, :accountId, :accountTypeId, :createdAt, :updatedAt)
                        RETURNING *;
                        """)
                .bind("id", toSaveAccountTypeMap.getId())
                .bind("accountId", toSaveAccountTypeMap.getAccountId())
                .bind("accountTypeId", toSaveAccountTypeMap.getAccountTypeId())
                .bind("updatedAt", toSaveAccountTypeMap.getUpdatedAt())
                .bind("createdAt", toSaveAccountTypeMap.getCreatedAt())
                .map((row, rowMetadata) -> AccountTypeMap
                        .builder()
                        .id(row.get("id", UUID.class))
                        .accountId(row.get("account_id", UUID.class))
                        .accountTypeId(row.get("account_type_id", UUID.class))
                        .createdAt(row.get("created_at", OffsetDateTime.class))
                        .updatedAt(row.get("updated_at", OffsetDateTime.class))
                        .build()
                )
                .one();
    }

    public Mono<AccountTypeMap> deleteById(UUID id) {
        return postgresOneDatabaseClient
                .sql("""
                        DELETE FROM account_type_map WHERE id = :id
                        RETURNING *;
                        """)
                .bind("id", id)
                .map((row, rowMetadata) -> AccountTypeMap
                        .builder()
                        .id(row.get("id", UUID.class))
                        .accountId(row.get("account_id", UUID.class))
                        .accountTypeId(row.get("account_type_id", UUID.class))
                        .createdAt(row.get("created_at", OffsetDateTime.class))
                        .updatedAt(row.get("updated_at", OffsetDateTime.class))
                        .build()
                )
                .one();
    }

}
