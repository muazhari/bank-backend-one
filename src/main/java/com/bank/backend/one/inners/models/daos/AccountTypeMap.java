package com.bank.backend.one.inners.models.daos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
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
    private UUID accountTypeId;
    @JsonSerialize(
            using = OffsetDateTimeSerializer.class
    )
    private OffsetDateTime createdAt;
    @JsonSerialize(
            using = OffsetDateTimeSerializer.class
    )
    private OffsetDateTime updatedAt;
}
