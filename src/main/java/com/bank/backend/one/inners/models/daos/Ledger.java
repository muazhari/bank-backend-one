package com.bank.backend.one.inners.models.daos;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table(
        value = "ledger"
)
@Data
@Builder
public class Ledger {
    private UUID id;
    private UUID accountId;
    private Number amount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
