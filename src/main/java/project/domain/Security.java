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
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author Shalom
 */
public class Security {

    private static final SecureRandom _random = new SecureRandom();

    public static HashAndSaltPair hash(String password) {

        try {
            byte[] saltBytes = new byte[16];
            _random.nextBytes(saltBytes);

            KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 100, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashBytes = factory.generateSecret(spec).getEncoded();

            String salt = Base64.getEncoder().encodeToString(saltBytes);
            String hash = Base64.getEncoder().encodeToString(hashBytes);

            return new HashAndSaltPair(hash, salt);

        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            throw new SecurityException("Failed creating an hash", ex);
        }
    }

    public static boolean authenticate(String inputPassword, HashAndSaltPair hashAndSalt) {

        try {
            byte[] saltBytes = Base64.getDecoder().decode(hashAndSalt.getSalt());
            KeySpec spec = new PBEKeySpec(inputPassword.toCharArray(), saltBytes, 100, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashBytes = factory.generateSecret(spec).getEncoded();

            String inputHash = Base64.getEncoder().encodeToString(hashBytes);
            return hashAndSalt.getHash().equals(inputHash);

        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            throw new SecurityException("Failed creating an hash", ex);
        }
    }
}
