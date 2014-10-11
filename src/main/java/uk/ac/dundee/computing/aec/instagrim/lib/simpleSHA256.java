/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.lib;


import java.io.UnsupportedEncodingException; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 

/**
 * Simple SHA256 hash
 * 
 */
public class simpleSHA256 {
    
    public static String convertToHex(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    } 
    
    public static String SHA256(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest  md;
        md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes("iso-8859-1"));
        byte[] sha256Hash = new byte[40];
        sha256Hash = md.digest();
        return convertToHex(sha256Hash);
    }
}
