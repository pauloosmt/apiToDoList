package com.br.apiToDoList.infra.security;

public class SecurityCustomException extends RuntimeException{
    public SecurityCustomException(String message) {
        super(message);
    }
}
