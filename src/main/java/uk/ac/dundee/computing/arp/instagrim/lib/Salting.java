/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.arp.instagrim.lib;

import java.security.SecureRandom;

/**
 * Class for salting
 * 
 * @author Andrew
 */
public class Salting {
    
    /**
     * Generate salt
     * 
     * @return byte array representing the salt
     */
    public static byte[] generateSalt()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[40];
        random.nextBytes(salt);
        return salt;
    }
}
