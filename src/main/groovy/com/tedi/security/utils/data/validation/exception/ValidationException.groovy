package com.tedi.security.utils.data.validation.exception

import org.springframework.stereotype.Component

@Component
abstract class ValidationException extends RuntimeException{
    ValidationException() {
        super()
    }

    ValidationException(String message){
        super(message)
    }
}