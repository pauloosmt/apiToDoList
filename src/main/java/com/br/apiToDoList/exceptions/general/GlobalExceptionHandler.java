package com.br.apiToDoList.exceptions.general;



import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.br.apiToDoList.exceptions.entity.EntityNotFoundException;
import com.br.apiToDoList.exceptions.login.RestErrorMessage;






@RestControllerAdvice
@Order(1)
public class GlobalExceptionHandler {
    
    //Code 404
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RestErrorMessage> handleEntityNotFound(EntityNotFoundException ex) {
        RestErrorMessage error = new RestErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    //Code 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RestErrorMessage> handleConflict(DataIntegrityViolationException ex) {
        RestErrorMessage error = new RestErrorMessage(HttpStatus.CONFLICT, ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

}
