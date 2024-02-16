package com.bank.backend.one.inners.models.dtos.requests.accounts.admins;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveOneAdminAccountRequest {
    private String email;
    private String password;
}
