package com.bank.backend.one.inners.usecases.authentications.logins;


import com.bank.backend.one.inners.models.dtos.Result;
import com.bank.backend.one.inners.models.dtos.requests.authentications.logins.LoginByEmailAndPasswordRequest;
import com.bank.backend.one.inners.models.dtos.responses.authentications.logins.LoginByEmailAndPasswordResponse;
import com.bank.backend.one.outers.repositories.AccountRepository;
import com.bank.backend.one.outers.repositories.AccountTypeMapRepository;
import com.bank.backend.one.outers.repositories.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LoginAuthentication {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountTypeRepository accountTypeRepository;
    @Autowired
    private AccountTypeMapRepository accountTypeMapRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Mono<Result<LoginByEmailAndPasswordResponse>> loginByEmailAndPassword(LoginByEmailAndPasswordRequest request) {
        return accountRepository.findFirstByEmailAndPassword(request.getEmail(), passwordEncoder.encode(request.getPassword()))
                .flatMap(account -> accountTypeMapRepository.findFirstByAccountIdAndAccountTypeName(account.getId(), request.getTypeName())
                        .flatMap(accountTypeMap -> accountTypeRepository.findFirstByName(accountTypeMap.getAccountTypeName())
                                .map(accountType -> Result.<LoginByEmailAndPasswordResponse>builder()
                                        .code(200)
                                        .message("Login authentication loginByEmailAndPassword succeeded.")
                                        .data(
                                                LoginByEmailAndPasswordResponse.builder()
                                                        .account(account)
                                                        .accountType(accountType)
                                                        .build()
                                        )
                                        .build()
                                )
                        )
                        .switchIfEmpty(
                                Mono.just(
                                        Result.<LoginByEmailAndPasswordResponse>builder()
                                                .code(403)
                                                .message("Login authentication loginByEmailAndPassword failed, account not found by accountTypeId.")
                                                .build()
                                )
                        )
                ).switchIfEmpty(
                        Mono.just(
                                Result.<LoginByEmailAndPasswordResponse>builder()
                                        .code(404)
                                        .message("Login authentication loginByEmailAndPassword failed, account not found.")
                                        .build()
                        )
                ).onErrorReturn(
                        Result.<LoginByEmailAndPasswordResponse>builder()
                                .code(500)
                                .message("Login authentication loginByEmailAndPassword failed.")
                                .build()
                );
    }
}
