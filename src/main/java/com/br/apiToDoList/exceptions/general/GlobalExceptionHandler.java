package com.br.apiToDoList.exceptions.general;



import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    //Authentication
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RestErrorMessage> handleAuth() {
        RestErrorMessage error = new RestErrorMessage(HttpStatus.UNAUTHORIZED, "Invalid email or password");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }


    //Code 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<RestErrorMessage>> handleValidation (MethodArgumentNotValidException ex) {
        List<RestErrorMessage> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> new RestErrorMessage(HttpStatus.BAD_REQUEST, err.getField() + ": " + err.getDefaultMessage()))
            .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

   

}
