package com.bank.backend.one.inners.models.dtos.requests.accounts.users;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatchFirstUserAccountRequest {
    private String email;
    private String password;
}
