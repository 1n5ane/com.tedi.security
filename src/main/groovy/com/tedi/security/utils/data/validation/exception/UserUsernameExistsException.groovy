package com.tedi.security.utils.data.validation.exception

import org.springframework.stereotype.Component

@Component
class UserUsernameExistsException extends ValidationException {

    UserUsernameExistsException() {
        super()
    }

    UserUsernameExistsException(String message) {
        super(message)
    }
}
