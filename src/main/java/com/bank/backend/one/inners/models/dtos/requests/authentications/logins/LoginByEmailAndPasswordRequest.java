package com.bank.backend.one.inners.models.dtos.requests.authentications.logins;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LoginByEmailAndPasswordRequest {
    private String email;
    private String password;
    private String typeName;

}
