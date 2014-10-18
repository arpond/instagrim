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
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.aec.instagrim.lib.simpleSHA256;
import uk.ac.dundee.computing.aec.instagrim.lib.saltGenerator;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import uk.ac.dundee.computing.aec.instagrim.stores.UserDetails;

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
        PreparedStatement psInsertUser = session.prepare("insert into userprofiles (login,password,salt,email,joined) Values(?,?,?,?,?)");
       
        BoundStatement bsUserCheck = new BoundStatement(psUserCheck);     
        BoundStatement bsInsertUser = new BoundStatement(psInsertUser);
        
        ResultSet rs = session.execute(bsUserCheck.bind(username));
        
        Date dateJoined = new Date();
        Set<String> emails = new HashSet<String>();
        emails.add(email);
        
        if (!rs.isExhausted())
        {
            return "Username already exists";
        }
        
        session.execute(// this is where the query is executed
            bsInsertUser.bind(// here you are binding the 'boundStatement'
                        username,encodedPassword,salt,emails,dateJoined));
        //We are assuming this always works.  Also a transaction would be good here !
        
        return "success";
    }
    
    // Improve no images returned
    
    public boolean IsValidUser(String username, String password){
         
        simpleSHA256 sha256handler = new simpleSHA256();
        String encodedPassword = null;
        
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
       
    public UserDetails getUserDetails(String username)
    {
        Session session = cluster.connect("instagrim");
       
        System.out.println(username);
        PreparedStatement ps = session.prepare("select * from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement bs = new BoundStatement(ps);
        
        rs = session.execute(bs.bind(username));
        UserDetails userDetails = new UserDetails();
        if (rs.isExhausted())
        {
            System.out.println("User not found - " + rs.isExhausted());
            return null;
        }
        else
        {
            for (Row row : rs)
            {
                String login = row.getString("login");
                String firstname = row.getString("first_name");
                String lastname = row.getString("last_name");
                Date joined = row.getDate("joined");
                Set<String> emails = row.getSet("email", String.class);
                Map<String, UDTValue> addresses = row.getMap("addresses", String.class, UDTValue.class);
                UDTValue address = addresses.get("Home");
                String street;
                String city;
                int zip;
                if (address == null)
                {
                    street = "";
                    city = "";
                    zip = 0;
                }
                else
                {
                    street = address.getString("street");
                    city = address.getString("city");
                    zip = address.getInt("zip");
                }
                userDetails.setUser(login, firstname, lastname, joined, emails, street, city, zip);
            }
        }
        
        System.out.println("User details: " + userDetails.toString());
        
        return userDetails;
    }
    
    public Boolean updateUserDetails(String username, String firstname, String lastname, String emails, String street, String city, int zip)
    {
        Session session = cluster.connect("instagrim");
       
        System.out.println(username);
        System.out.println(firstname);
        System.out.println(lastname);
        System.out.println(emails);
        
        UserType addressUDT = session.getCluster().getMetadata().getKeyspace("instagrim").getUserType("address");
        UDTValue address = addressUDT.newValue().setString("street", street).setString("city", city).setInt("zip", zip);
        Map<String, UDTValue> addresses = new HashMap<String, UDTValue>();
        addresses.put("Home", address);
        
        PreparedStatement ps = session.prepare("Update userprofiles set first_name = ?, last_name = ?, email = ?, addresses = ? where login =?");
        ResultSet rs = null;
        BoundStatement bs = new BoundStatement(ps);
        
        String[] emailData = emails.split(";");       
        HashSet<String> mails = new HashSet<String>();
        
        for (String mail : emailData)
        {
            mails.add(mail);
        }
        
        session.execute(bs.bind(firstname,lastname,mails,addresses,username));
        
        return true;
    }

    
}
