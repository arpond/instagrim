/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.arp.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.util.Date;
import java.util.ArrayList;
import java.util.UUID;
import uk.ac.dundee.computing.arp.instagrim.stores.Comment;

/**
 * Model for comments
 * 
 * @author Andrew
 */
public class CommentModel {

    Cluster cluster;
    
    public CommentModel() {
    }
    
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    /**
     * Inserts a comment into the database
     * 
     * @param user String representing the author of the comment
     * @param pictureID String representing the id of the picture
     * @param comment String representing the comment
     */
    public void insertComment(String user, String pictureID, String comment)
    {
        Session session = cluster.connect("instagrim");
        
        PreparedStatement psInsertComment = session.prepare("insert into comments ( picid, author, comment, writtenOn) values(?,?,?,?)");
        BoundStatement bsInsertComment = new BoundStatement(psInsertComment);
               
        Date writtenOn = new Date();
        java.util.UUID picid = UUID.fromString(pictureID);
                
        session.execute(bsInsertComment.bind(picid, user, comment, writtenOn));
        session.close();
    }
    
    /**
     * Fetches the comments for a picture from the db
     * 
     * @param picid The id of the picture to fetch the comments for.
     * @return The array list of comments for this picture
     */
    public ArrayList<Comment> getComments(java.util.UUID picid)
    {
        Session session = cluster.connect("instagrim");
        
        PreparedStatement psComment = session.prepare("SELECT * FROM comments WHERE picid = ?");
        BoundStatement bsComment = new BoundStatement (psComment);
        
        ResultSet rs = null;
        rs = session.execute(bsComment.bind(picid));
        ArrayList<Comment> comments = new ArrayList<Comment>();
        if (rs.isExhausted()) 
        {
            System.out.println("No Images returned");
            return null;
        }
        else
        {
            for (Row row : rs) {
                Comment comment = new Comment();
                String author = row.getString("author");
                String text = row.getString("comment");
                Date writtenOn = row.getDate("writtenOn");
                comment.setAllOfComment(author, text, writtenOn);
                comments.add(comment);
            }
        }
        return comments;
    } 
}
