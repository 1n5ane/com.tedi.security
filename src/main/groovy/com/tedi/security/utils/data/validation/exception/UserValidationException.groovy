package com.tedi.security.utils.data.validation.exception

import org.springframework.stereotype.Component

@Component
class UserValidationException extends ValidationException{

    UserValidationException() {
        super()
    }

    UserValidationException(String message) {
        super(message)
    }
}
