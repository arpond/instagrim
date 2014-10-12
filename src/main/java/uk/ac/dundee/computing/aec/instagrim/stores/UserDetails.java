/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.stores;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Andrew
 */
public class UserDetails {
    private String username;
    private String firstname;
    private String lastname;
    private Date joined;
    private Set<String> emails;

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
    
    public void setUser(String username, String firstname, String lastname, Date joined, Set<String> emails )
    {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.joined = joined;
        this.emails = emails;
    }
}
