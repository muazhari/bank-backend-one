package com.bank.backend.one.inners.models.daos;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table(
        value = "account_type"
)
@Data
@Builder
public class AccountType {
    private String name;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
