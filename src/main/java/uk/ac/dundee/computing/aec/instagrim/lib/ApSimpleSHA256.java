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
 * Class for SHA 256 hash
 * 
 * @author Andrew
 */
public class ApSimpleSHA256 {
    
    /**
     * Converts the byte array to a Hex string
     * 
     * @param data The byte array to convert
     * @return String representing the byte array in hex
     */
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
    
    /**
     * Generate an SHA 256 hash from the text passed
     * 
     * @param text String to convert to hash 
     * @return String of the SHA 256 Hash
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException 
     */
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
