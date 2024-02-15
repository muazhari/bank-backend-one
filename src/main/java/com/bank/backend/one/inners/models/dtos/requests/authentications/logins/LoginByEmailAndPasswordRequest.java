package com.bank.backend.one.inners.models.dtos.requests.authentications.logins;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LoginByEmailAndPasswordRequest {
    private String email;
    private String password;
    private UUID accountTypeId;
}
