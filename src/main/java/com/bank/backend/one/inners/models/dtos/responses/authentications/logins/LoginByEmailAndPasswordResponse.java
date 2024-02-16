package com.bank.backend.one.inners.models.dtos.responses.authentications.logins;

import com.bank.backend.one.inners.models.daos.Account;
import com.bank.backend.one.inners.models.daos.AccountType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginByEmailAndPasswordResponse {
    private Account account;
    private AccountType accountType;
}
