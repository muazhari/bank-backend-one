package com.bank.backend.one.inners.models.daos;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table(
        value = "account_type_map"
)
@Data
@Builder
public class AccountTypeMap {
    private UUID id;
    private UUID accountId;
    private String accountTypeName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
