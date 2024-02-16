package com.bank.backend.one.inners.models.dtos.requests.authentications.registers;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RegisterByEmailAndPasswordRequest {
    private String email;
    private String password;
    private String typeName;
}
