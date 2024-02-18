package com.bank.backend.one.inners.models.daos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
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

    @JsonSerialize(
            using = OffsetDateTimeSerializer.class
    )
    private OffsetDateTime createdAt;
    @JsonSerialize(
            using = OffsetDateTimeSerializer.class
    )
    private OffsetDateTime updatedAt;
}
