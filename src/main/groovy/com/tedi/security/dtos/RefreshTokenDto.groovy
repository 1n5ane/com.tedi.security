package com.tedi.security.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class RefreshTokenDto {
    String refreshToken

    @Override
    public String toString() {
        return "RefreshTokenDto{" +
                "refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
