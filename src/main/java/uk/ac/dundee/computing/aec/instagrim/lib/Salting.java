/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.lib;

import java.security.SecureRandom;

/**
 *
 * @author Andrew
 */
public class Salting {
    public static byte[] generateSalt()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[40];
        random.nextBytes(salt);
        return salt;
    }
    
}
