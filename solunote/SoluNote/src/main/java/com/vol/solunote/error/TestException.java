package com.vol.solunote.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class TestException extends RuntimeException {
    
    String message;
    
    public TestException(String message) {
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}

