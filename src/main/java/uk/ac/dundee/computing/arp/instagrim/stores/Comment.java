/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.arp.instagrim.stores;

import java.util.Date;

/**
 *
 * @author Andrew
 */
public class Comment {
    private String author;
    private String comment;
    private Date writtenOn;

    public Comment() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getWrittenOn() {
        return writtenOn;
    }

    public void setWrittenOn(Date writtenOn) {
        this.writtenOn = writtenOn;
    }
    
    public void setAllOfComment(String author, String comment, Date writtenOn)
    {
        this.author = author;
        this.comment = comment;
        this.writtenOn = writtenOn;
    }
}
