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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import uk.ac.dundee.computing.aec.instagrim.lib.ApSimpleSHA256;
import uk.ac.dundee.computing.aec.instagrim.lib.Salting;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import uk.ac.dundee.computing.aec.instagrim.stores.UserDetails;

/**
 * User Model
 * 
 * @author Andrew
 */
public class UserModel {
            
    Cluster cluster;
    public UserModel(){
        
    }
    
    /**
     * Registers a user
     * 
     * @param username Username of the new user
     * @param password Password of the new user
     * @param email Email of the new user
     * @param context The current context 
     * @return String representing success or failure
     */
    public String RegisterUser(String username, String password, String email, ServletContext context){
        /*AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        String encodedPassword=null;
        try {
            encodedPassword= sha1handler.SHA1(password);
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException et){
            System.out.println("Can't check your password");
            return "Can't encode your password";
        }*/
        
        ApSimpleSHA256 sha256handler = new ApSimpleSHA256();
        Salting saltShaker = new Salting();
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
        
        try
        {
            //System.out.println(getServletContext().getContextPath());
            System.out.println("Reading Image");
            InputStream in =  context.getResourceAsStream("/Resources/default_profile_image.png");
            //BufferedImage BI = ImageIO.read(new File("http://localhost:8080/Instagrim/Resources/default_profile_image.png"));
            BufferedImage BI = ImageIO.read(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.out.println("ImageIO write");
            ImageIO.write(BI, "png", baos);
            
            baos.flush();
            System.out.println("to byte array");
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            System.out.println("Inserting");
            insertProfilePic(imageInByte,username,"png");
        }
        catch (IOException ioe)
        {
            System.out.println("error loading default profile image " + ioe.getMessage());
            ioe.printStackTrace();
        }
        
        return "success";
    }
    
   
    /**
     * Checks if a user is valid
     * 
     * @param username Username supplied
     * @param password Password supplied
     * @return If the user is valid or invalid
     */
    public boolean IsValidUser(String username, String password){
         
        ApSimpleSHA256 sha256handler = new ApSimpleSHA256();
        String encodedPassword = null;
        
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select password, salt from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        if (rs.isExhausted()) {
            System.out.println("No USer returned");
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
       
    /**
     * Gets the user details for the username provided
     * 
     * @param username The username to get the details of
     * @return USerDetails of the user
     */   
    public UserDetails getUserDetails(String username)
    {
        System.out.println("Getting user details..");
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
    
    /**
     * Updates a users details
     * @param username Username of the details to update
     * @param firstname First name to update to
     * @param lastname Last name to update to
     * @param emails Email to update to
     * @param street Street to update to
     * @param city City to update to
     * @param zip zip to update to
     * @return 
     */
    public void updateUserDetails(String username, String firstname, String lastname, String emails, String street, String city, int zip)
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
    }
    
    /**
     * Insert a profile picture for a user
     * @param b The bytes representing the image
     * @param user The user whose profile to update
     * @param type The file type of the image
     */
    public void insertProfilePic(byte[] b, String user, String type) 
    {
        System.out.println("wrapping");
        ByteBuffer buffer = ByteBuffer.wrap(b);
        int length = b.length;
        Session session = cluster.connect("instagrim");

        PreparedStatement psInsertProfilePic = session.prepare("Update userprofiles set profilepic = ?, profilepiclength = ?, profilepictype = ? where login = ?");
        BoundStatement bsInsertProfilePic = new BoundStatement(psInsertProfilePic);

        session.execute(bsInsertProfilePic.bind(buffer, length, type, user));
        session.close();
    }
    
    /**
     * Get a profile picture of a user
     * @param user the user whose profile picture to get
     * @return The profile picture
     */
    public Pic getProfilePic(String user)
    {
        System.out.println("Getting Profile Pic..");
        System.out.println("user: " + user);
        Session session = cluster.connect("instagrim");
        ByteBuffer bImage = null;
        int length = 0;
        String type = "";
        ResultSet rs = null;
        PreparedStatement ps = session.prepare("select profilepic, profilepiclength, profilepictype from userprofiles where login = ?");
        BoundStatement bs = new BoundStatement(ps);
        rs = session.execute(bs.bind(user));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return null;
        } else {
            for (Row row : rs) {
                bImage = row.getBytes("profilepic");
                length = row.getInt("profilepiclength");
                type = row.getString("profilepictype");
            }
        }
        Pic p = new Pic();
        p.setPic(bImage, length, type);
        return p;
    }
    
}
