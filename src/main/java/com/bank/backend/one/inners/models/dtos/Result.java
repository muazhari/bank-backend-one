package com.bank.backend.one.inners.models.dtos;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result<T> {
    private T data;
    private Number code;
    private String message;
}
