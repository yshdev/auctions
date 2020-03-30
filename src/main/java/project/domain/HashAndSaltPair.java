/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.domain;

/**
 *
 * @author Shalom
 */
public class HashAndSaltPair {
 
    private String hash;
    private String salt;
    
    public HashAndSaltPair(String hash, String salt) {
        this.hash = hash;
        this.salt = salt;
    }

    public String getHash() {
        return hash;
    }

    public String getSalt() {
        return salt;
    }
    
    
}
