package com.bank.backend.one.inners.models.daos;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table(
        value = "transaction"
)
@Data
@Builder
public class Transaction {
    private UUID id;
    private UUID ledgerId;
    private Number amountBefore;
    private Number amountAfter;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
