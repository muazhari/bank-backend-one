package com.bank.backend.one.inners.models.daos;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table(
        value = "account"
)
@Data
@Builder
public class Account {
    private UUID id;
    private String email;
    private String password;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
