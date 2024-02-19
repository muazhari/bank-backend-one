package com.bank.backend.one.inners.models.dtos;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import com.ongres.scram.common.bouncycastle.base64.Base64;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class Session {
    private String accessToken;
    private String refreshToken;
    @JsonSerialize(
            using = OffsetDateTimeSerializer.class
    )
    private OffsetDateTime expiredAt;

    public String toJsonString() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJsonStringHash() {
        return Base64.toBase64String(this.toJsonString().getBytes());
    }
}
