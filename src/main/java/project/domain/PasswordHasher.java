/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.domain;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author Shalom
 */
public class PasswordHasher {
    
    private static SecureRandom _random = new SecureRandom();
    private String salt;
    private String hash;
    
    public PasswordHasher() {
    }
    
    public void hash(String password) {
         
        try {
            byte[] saltBytes = new byte[16];
            _random.nextBytes(saltBytes);
            
            KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 100, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashBytes = factory.generateSecret(spec).getEncoded();
            
            this.salt = new String(saltBytes, StandardCharsets.UTF_8);
            this.hash = new String(hashBytes, StandardCharsets.UTF_8);
            
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            throw new SecurityException("Failed creating an hash", ex);
        }
    }

    public String getSalt() {
        return salt;
    }

    public String getHash() {
        return hash;
    }
     
}
