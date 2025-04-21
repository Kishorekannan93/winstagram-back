package com.example.socio_app.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionsHandle {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationException(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();
        for(FieldError error: ex.getBindingResult().getFieldErrors()){
            errors.put(error.getField(),error.getDefaultMessage());

        }
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIoException(IOException ex){
        return ResponseEntity.internalServerError().body("Error in uploading image.."+ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleGenricException(Exception e){
        Map<String,String> errorGn = new HashMap<>();
        errorGn.put("error",e.getMessage());
        return  ResponseEntity.badRequest().body(errorGn);
    }
}
