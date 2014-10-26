/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.arp.instagrim.stores;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import com.datastax.driver.core.UDTValue;

/**
 *
 * @author Andrew
 */
public class UserDetails {
    private String username;
    private String firstname = "";
    private String lastname = "";
    private Date joined;
    private Set<String> emails;
    private String street = "";
    private String city = "";
    private int zip = 0;

    public UserDetails() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Date getJoined() {
        return joined;
    }

    public void setJoined(Date joined) {
        this.joined = joined;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(HashSet emails) {
        this.emails = emails;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }
    
    public void setUser(String username, String firstname, String lastname, Date joined, Set<String> emails, String street, String city, int zip)
    {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.joined = joined;
        this.emails = emails;
        this.street = street;
        this.city = city;
        this.zip = zip;
    }
    
    @Override public String toString()
    {
        return "Username: " + username +
               ", First Name: " + firstname +
               ", Last Name: " + lastname +
               ", joined: " + joined.toString();
    }


    
}
