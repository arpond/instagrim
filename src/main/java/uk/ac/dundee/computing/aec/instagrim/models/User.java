/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.aec.instagrim.lib.simpleSHA256;
import uk.ac.dundee.computing.aec.instagrim.lib.saltGenerator;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

/**
 *
 * @author Administrator
 */
public class User {
    Cluster cluster;
    public User(){
        
    }
    
    // TODO - Add and store salt
    
    public String RegisterUser(String username, String password, String email){
        /*AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        String encodedPassword=null;
        try {
            encodedPassword= sha1handler.SHA1(password);
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException et){
            System.out.println("Can't check your password");
            return "Can't encode your password";
        }*/
        
        simpleSHA256 sha256handler = new simpleSHA256();
        saltGenerator saltShaker = new saltGenerator();
        String encodedPassword = null;
        String salt = null;
        try
        {
            salt = sha256handler.convertToHex(saltShaker.generateSalt());
            encodedPassword = sha256handler.SHA256(password + salt);
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException et)
        {
            System.out.println("Can't check your password");
            return "Can't encode your password";
        }
         
        

        Session session = cluster.connect("instagrim");
        PreparedStatement psUserCheck = session.prepare("select login from userprofiles where login =?");
        PreparedStatement psInsertUser = session.prepare("insert into userprofiles (login,password,salt,email) Values(?,?,?,{?})");
       
        BoundStatement bsUserCheck = new BoundStatement(psUserCheck);     
        BoundStatement bsInsertUser = new BoundStatement(psInsertUser);
        
        ResultSet rs = session.execute(bsUserCheck.bind(username));
        
        if (!rs.isExhausted())
        {
            return "Username already exists";
        }
        session.execute(// this is where the query is executed
bsInsertUser.bind(// here you are binding the 'boundStatement'
                        username,encodedPassword,salt,email));
        //We are assuming this always works.  Also a transaction would be good here !
        
        return "success";
    }
    
    // Improve no images returned
    
    public boolean IsValidUser(String username, String password){
        /*AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        String EncodedPassword=null;
        try {
            EncodedPassword= sha1handler.SHA1(Password);
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException et){
            System.out.println("Can't check your password");
            return false;
        }*/
        
        simpleSHA256 sha256handler = new simpleSHA256();
        //saltGenerator saltShaker = new saltGenerator();
        String encodedPassword = null;
        //String salt = null;
        
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select password, salt from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return false;
        } else {
            for (Row row : rs) {
               
                String StoredPass = row.getString("password");
                String storedSalt = row.getString("salt");
                
                try
                {
                    //salt = sha256handler.convertToHex(saltShaker.generateSalt());
                    encodedPassword = sha256handler.SHA256(password + storedSalt);
                }
                catch (UnsupportedEncodingException | NoSuchAlgorithmException et)
                {
                    System.out.println("Can't check your password");
                    return false;
                }
                if (StoredPass.compareTo(encodedPassword) == 0)
                    return true;
            }
        }
   
    
    return false;  
    }
       public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    
}
