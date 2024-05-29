package com.tedi.security.services.validation

import com.tedi.security.utils.data.validation.exception.ValidationException
import org.springframework.stereotype.Component

@Component
interface ValidationService {

    public void validate(def request) throws ValidationException
}