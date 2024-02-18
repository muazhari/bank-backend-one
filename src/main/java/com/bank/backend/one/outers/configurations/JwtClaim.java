package com.bank.backend.one.outers.configurations;


import lombok.Data;

@Data
public class JwtClaim {
    private String account;
    private String type;
}
