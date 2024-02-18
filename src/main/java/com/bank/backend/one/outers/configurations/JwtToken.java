package com.bank.backend.one.outers.configurations;


import lombok.Data;

@Data
public class JwtToken {
    private String issuer;
    private String subject;
    private String secret;
    private JwtClaim claim;
}
