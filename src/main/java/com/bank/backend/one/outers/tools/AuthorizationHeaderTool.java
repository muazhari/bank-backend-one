package com.bank.backend.one.outers.tools;


import org.springframework.stereotype.Component;

@Component
public class AuthorizationHeaderTool {

    public String getAuthorizationHeader(String scheme, String token) {
        return scheme + " " + token;
    }

    public String getTokenFromAuthorizationHeader(String scheme, String authorizationHeader) {
        return authorizationHeader.split(" ")[1];
    }

    public String getSchemeFromAuthorizationHeader(String authorizationHeader) {
        return authorizationHeader.split(" ")[0];
    }
}
