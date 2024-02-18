package com.bank.backend.one.inners.models.dtos.responses.authentications.logins;

import com.bank.backend.one.inners.models.daos.Account;
import com.bank.backend.one.inners.models.daos.AccountType;
import com.bank.backend.one.inners.models.dtos.Session;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginByEmailAndPasswordResponse {
    private Account account;
    private AccountType accountType;
    private Session session;
}
