package com.bank.backend.one.inners.models.dtos.requests.accounts;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class PatchOneAccountRequest {
    private UUID accountId;
    private String email;
    private String password;
    private String typeName;
}
