package com.bank.backend.one.outers.securities;

import com.bank.backend.one.outers.tools.AuthorizationHeaderTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthorizationHeaderTool authorizationHeaderTool;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return Mono
                .just(exchange.getRequest())
                .mapNotNull(serverHttpRequest -> serverHttpRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authorizationHeader -> authorizationHeader != null && !authorizationHeader.isBlank() && !authorizationHeader.isEmpty())
                .map(authorizationHeader -> authorizationHeaderTool.getTokenFromAuthorizationHeader("Bearer", authorizationHeader))
                .flatMap(accessToken -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(accessToken, null, null)))
                .map(SecurityContextImpl::new);
    }
}
