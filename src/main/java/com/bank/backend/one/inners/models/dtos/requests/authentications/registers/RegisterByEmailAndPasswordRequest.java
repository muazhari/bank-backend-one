package com.bank.backend.one.inners.models.dtos.requests.authentications.registers;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterByEmailAndPasswordRequest {
    private String email;
    private String password;
    private String typeName;
}
