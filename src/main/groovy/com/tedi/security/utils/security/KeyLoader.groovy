package com.tedi.security.utils.security

import org.springframework.stereotype.Component

import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

@Component
class KeyLoader {

    static byte[] readKeyFromFile(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath))
    }

    static PublicKey loadPublicKey(String filePath) throws Exception {
        byte[] keyBytes = readKeyFromFile(filePath)
        byte[] derKeyBytes = convertPEMToDERPublicKey(keyBytes)
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(derKeyBytes)
        KeyFactory keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec);
    }

    static PrivateKey loadPrivateKey(String filePath) throws Exception {
        byte[] keyBytes = readKeyFromFile(filePath)
        byte[] derKeyBytes = convertPEMToDERPrivateKey(keyBytes)
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(derKeyBytes)
        KeyFactory keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }

    static byte[] convertPEMToDERPublicKey(byte[] pemKey) {
        String pemString = new String(pemKey)
        pemString = pemString.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "")
        return Base64.getDecoder().decode(pemString)
    }

    static byte[] convertPEMToDERPrivateKey(byte[] pemKey) {
        String pemString = new String(pemKey)
        pemString = pemString.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        return Base64.getDecoder().decode(pemString)
    }
}

