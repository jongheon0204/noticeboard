package com.jongheon.www.noticeboard.cipher;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Component
public class SHA256 {

    public Optional<String> Encrypt(String msg){
        String encryptedMsg = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(msg.getBytes());
            StringBuilder builder = new StringBuilder();
            for (byte b : md.digest()) {
                builder.append(String.format("%02x", b));
            }
            encryptedMsg = builder.toString();
        } catch (NoSuchAlgorithmException ignored){
        }
        return Optional.ofNullable(encryptedMsg);
    }

}
