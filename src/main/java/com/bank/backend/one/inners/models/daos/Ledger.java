package com.bank.backend.one.inners.models.daos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
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
    @JsonSerialize(
            using = OffsetDateTimeSerializer.class
    )
    private OffsetDateTime createdAt;
    @JsonSerialize(
            using = OffsetDateTimeSerializer.class
    )
    private OffsetDateTime updatedAt;
}
