package com.rewardify;

import com.rewardify.exceeption.ResourceNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

     @ExceptionHandler(ResourceNotExistException.class)
     public ResponseEntity<?> handleResourceNotFound(ResourceNotExistException ex) {
         return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
     }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleResourceNotFound(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
