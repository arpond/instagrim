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
import java.util.HashSet;
import java.util.LinkedList;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

/**
 *
 * @author Andrew
 */
public class TagModel {
    
    Cluster cluster;

    public TagModel() {
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
    
    public HashSet<String> getTags(java.util.UUID picid)
    {
        Session session = cluster.connect("instagrim");
        HashSet<String> tags = new HashSet<String>();
        PreparedStatement psTagIDs = session.prepare("select tagid from tagpic where picid = ?");        
        BoundStatement bsTagIDs = new BoundStatement(psTagIDs); 
        
        ResultSet rs = null;
        rs = session.execute(bsTagIDs.bind(picid));
        if (rs.isExhausted())
        {
            return tags;
        }
        for (Row row : rs)
        {
            ResultSet tag = null;
            java.util.UUID tagid = row.getUUID("tagid");
            PreparedStatement psTags = session.prepare("select * from tags where tagid = ? ");
            BoundStatement bsTag = new BoundStatement(psTags);
            tag = session.execute(bsTag.bind(tagid));
            if (!tag.isExhausted())
            {
                for (Row line : tag)
                {
                    String tagName = line.getString("tag");
                    tags.add(tagName);                          
                }
            }
        }
        session.close();
        return tags;
    }
    
    public boolean tagExists(String tag)
    {
        Session session = cluster.connect("instagrim");
        PreparedStatement psTag = session.prepare("select * from tags where tag = ? ALLOW FILTERING");
        BoundStatement bsTag = new BoundStatement(psTag);
        ResultSet rs = session.execute(bsTag.bind(tag));
        if (!rs.isExhausted())
        {
            session.close();
            return true;
        }
        session.close();
        return false;
    }
    
    public java.util.UUID getTagID(String tag)
    {
        java.util.UUID tagid = null;
        Session session = cluster.connect("instagrim");
        PreparedStatement psTagID = session.prepare("select tagid from tags where tag = ? ALLOW FILTERING");
        BoundStatement bsTagID = new BoundStatement(psTagID);
        
        ResultSet rs = null;
        
        rs = session.execute(psTagID.bind(tag));
        if(rs.isExhausted())
        {
            System.out.println("Tag not found");
            return null;
        }
        for (Row row : rs)
        {
            tagid = row.getUUID("tagid");
        }
        session.close();
        return tagid;
    }
    
    public java.util.UUID addNewTag(String tag)
    {
        System.out.println("Adding " + tag + " to tags");
        Session session = cluster.connect("instagrim");
        Convertors convertor = new Convertors();
        java.util.UUID tagID = convertor.getTimeUUID();
        PreparedStatement psNewTag = session.prepare("insert into tags (tagid,tag) values(?,?) ");
        BoundStatement bsNewTag = new BoundStatement(psNewTag);
        session.execute(bsNewTag.bind(tagID, tag));
        session.close();
        return tagID;
    }
}