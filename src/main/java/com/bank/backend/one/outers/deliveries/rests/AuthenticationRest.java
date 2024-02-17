package com.bank.backend.one.outers.deliveries.rests;


import com.bank.backend.one.inners.models.dtos.Response;
import com.bank.backend.one.inners.models.dtos.requests.authentications.logins.LoginByEmailAndPasswordRequest;
import com.bank.backend.one.inners.models.dtos.requests.authentications.registers.RegisterByEmailAndPasswordRequest;
import com.bank.backend.one.inners.models.dtos.responses.authentications.logins.LoginByEmailAndPasswordResponse;
import com.bank.backend.one.inners.models.dtos.responses.authentications.registers.RegisterByEmailAndPasswordResponse;
import com.bank.backend.one.inners.usecases.authentications.logins.LoginAuthentication;
import com.bank.backend.one.inners.usecases.authentications.registers.RegisterAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(
        value = "/authentications"
)
public class AuthenticationRest {

    @Autowired
    private LoginAuthentication loginAuthentication;
    @Autowired
    private RegisterAuthentication registerAuthentication;

    @PostMapping(
            value = "/logins/email-and-passwords"
    )
    public Mono<ResponseEntity<Response<LoginByEmailAndPasswordResponse>>> loginByEmailAndPassword(
            @RequestBody
            LoginByEmailAndPasswordRequest loginByEmailAndPasswordRequest
    ) {
        return loginAuthentication
                .loginByEmailAndPassword(loginByEmailAndPasswordRequest)
                .map(result -> ResponseEntity
                        .status((Integer) result.getCode())
                        .body(Response
                                .<LoginByEmailAndPasswordResponse>builder()
                                .data(result.getData())
                                .message(result.getMessage())
                                .build()
                        )
                );
    }

    @PostMapping(
            value = "/registers/email-and-passwords"
    )
    public Mono<ResponseEntity<Response<RegisterByEmailAndPasswordResponse>>> registerByEmailAndPassword(
            @RequestBody
            RegisterByEmailAndPasswordRequest registerByEmailAndPasswordRequest
    ) {
        return registerAuthentication
                .registerByEmailAndPassword(registerByEmailAndPasswordRequest)
                .map(result -> ResponseEntity
                        .status((Integer) result.getCode())
                        .body(Response
                                .<RegisterByEmailAndPasswordResponse>builder()
                                .data(result.getData())
                                .message(result.getMessage())
                                .build()
                        )
                );
    }

}
