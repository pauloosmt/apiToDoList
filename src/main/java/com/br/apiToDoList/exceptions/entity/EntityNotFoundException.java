package com.br.apiToDoList.exceptions.entity;


public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String message) {
        super(message);
    }
}
