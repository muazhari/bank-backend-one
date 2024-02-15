package com.bank.backend.one.inners.models.dtos.responses.authentications.registers;

import com.bank.backend.one.inners.models.daos.Account;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterByEmailAndPasswordResponse {
    private Account account;
}
