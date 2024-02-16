package com.bank.backend.one.inners.models.dtos.requests.accounts;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SaveOneAccountRequest {
    private String email;
    private String password;
    private String typeName;
}
