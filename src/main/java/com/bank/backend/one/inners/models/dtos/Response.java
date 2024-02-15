package com.bank.backend.one.inners.models.dtos;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response<T> {
    private T data;
    private String message;
}
