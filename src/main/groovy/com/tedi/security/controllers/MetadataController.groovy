package com.tedi.security.controllers

import com.tedi.security.services.JwtService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import java.security.PublicKey
import java.security.interfaces.RSAPublicKey

@RestController
public class MetadataController {

    @Autowired
    private JwtService jwtService

//    JWK Set Endpoint
    @ResponseBody
    @GetMapping(value = "/.well-known/jwks.json", produces = "application/json;charset=UTF-8")
    def getJwks() {
        PublicKey publicKey = jwtService.getPublicKey()

        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey

        String publicKeyEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(rsaPublicKey.getModulus().toByteArray())
        String publicKeyExponent = Base64.getUrlEncoder().withoutPadding().encodeToString(rsaPublicKey.getPublicExponent().toByteArray())
        def res = ["keys": [["kty": publicKey.getAlgorithm(),
                            "use": "sig",
                            "alg": "RS256",
                            "kid": "1",
                            "n"  : publicKeyEncoded,
                            "e"  : publicKeyExponent]]
        ]

        return new ResponseEntity<>(res, HttpStatus.OK)
    }
}