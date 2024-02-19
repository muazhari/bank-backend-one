package com.bank.backend.one.outers.deliveries.rests;


import com.bank.backend.one.inners.models.dtos.Response;
import com.bank.backend.one.inners.models.dtos.Session;
import com.bank.backend.one.inners.usecases.authorization.SessionAuthorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(
        value = "/authorizations"
)
public class AuthorizationRest {

    @Autowired
    private SessionAuthorization sessionAuthorization;

    @PostMapping(
            value = "/refreshes/access-tokens"
    )
    public Mono<ResponseEntity<Response<Session>>> refreshAccessToken(
            @RequestBody
            Session refreshAccessTokenRequest
    ) {
        return sessionAuthorization
                .refreshAccessToken(refreshAccessTokenRequest)
                .map(result -> ResponseEntity
                        .status((Integer) result.getCode())
                        .body(Response
                                .<Session>builder()
                                .data(result.getData())
                                .message(result.getMessage())
                                .build()
                        )
                );
    }
}
