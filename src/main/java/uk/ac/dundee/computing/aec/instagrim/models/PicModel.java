package uk.ac.dundee.computing.aec.instagrim.models;

/*
 * Expects a cassandra columnfamily defined as
 * use keyspace2;
 CREATE TABLE Tweets (
 user varchar,
 interaction_time timeuuid,
 tweet varchar,
 PRIMARY KEY (user,interaction_time)
 ) WITH CLUSTERING ORDER BY (interaction_time DESC);
 * To manually generate a UUID use:
 * http://www.famkruithof.net/uuid/uuidgen
 */
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.Bytes;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import static org.imgscalr.Scalr.*;
import org.imgscalr.Scalr.Method;

import uk.ac.dundee.computing.aec.instagrim.lib.*;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
//import uk.ac.dundee.computing.aec.stores.TweetStore;

public class PicModel {

    Cluster cluster;

    public void PicModel() {

    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public void insertPic(byte[] b, String type, String name, String user) {
        try {
            Convertors convertor = new Convertors();
            System.out.println("Inserting image...");
            String types[]=Convertors.SplitFiletype(type);
            ByteBuffer buffer = ByteBuffer.wrap(b);
            int length = b.length;
            java.util.UUID picid = convertor.getTimeUUID();
            
            //The following is a quick and dirty way of doing this, will fill the disk quickly !
            //Boolean success = (new File("/var/tmp/instagrim/")).mkdirs();
            //FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrim/" + picid));

            //output.write(b);
            System.out.println("Generating Thumb...");
            byte []  thumbb = picresize(types[1],b);
            int thumblength= thumbb.length;
            ByteBuffer thumbbuf=ByteBuffer.wrap(thumbb);
            System.out.println("Generateing Processed...");            
            byte[] processedb = picdecolour(types[1],b);
            ByteBuffer processedbuf=ByteBuffer.wrap(processedb);
            int processedlength=processedb.length;
            
//            byte[] sepiadb = applyFilter(Convertors.DISPLAY_SEPIA, types[1], b);
//            ByteBuffer sepiabuf = ByteBuffer.wrap(sepiadb);
//            int sepialength = sepiadb.length;
//            
//            byte[] negativedb = applyFilter(Convertors.DISPLAY_NEGATIVE, types[1], b);
//            ByteBuffer negativebuf = ByteBuffer.wrap(negativedb);
//            int negativelength = negativedb.length;
            
            Session session = cluster.connect("instagrim");

            PreparedStatement psInsertPic = session.prepare("insert into pics ( picid, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement psInsertPicToUser = session.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)");
            BoundStatement bsInsertPic = new BoundStatement(psInsertPic);
            BoundStatement bsInsertPicToUser = new BoundStatement(psInsertPicToUser);

            System.out.println("Executing queries...");            
            Date DateAdded = new Date();
            session.execute(bsInsertPic.bind(picid, buffer, thumbbuf,processedbuf, user, DateAdded, length,thumblength,processedlength, type, name));
            session.execute(bsInsertPicToUser.bind(picid, user, DateAdded));
            session.close();

        } catch (IOException ex) {
            System.out.println("Error --> " + ex);
        }
    }
    
    /**
     * Deletes the appropriate image from the database
     * 
     * @param user The user the image belongs to
     * @param picid The UUID of the picture
     * @return If the action was successful or not
     */
    public String picDelete(String user, java.util.UUID picid)
    {
        try {
            Session session = cluster.connect("instagrim");

            //PreparedStatement psDeletePic = session.prepare("insert into pics ( picid, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement psInsertedTime = session.prepare("SELECT interaction_time, user FROM pics WHERE picid = ?");
            PreparedStatement psDeletePic = session.prepare("DELETE FROM pics WHERE picid = ?");
            PreparedStatement psDeletePicUserPicList = session.prepare("DELETE FROM userpiclist WHERE user = ? AND pic_added = ?");
            PreparedStatement psDeletePicComments = session.prepare("DELETE FROM comments WHERE picid = ?");
            BoundStatement bsInsertedTime = new BoundStatement(psInsertedTime);
            BoundStatement bsDeletePic = new BoundStatement(psDeletePic);
            BoundStatement bsDeletePicUserPicList = new BoundStatement(psDeletePicUserPicList);
            BoundStatement bsDeletePicComments = new BoundStatement(psDeletePicComments);
            
            ResultSet rs = session.execute(bsInsertedTime.bind(picid));
            Date dateAdded = new Date();
            String owner = "";
            
            TagModel tm = new TagModel();
            tm.setCluster(cluster);

            if (rs.isExhausted()) 
            {
                session.close();
                return "no rows";
            } 
            else 
            {
                for (Row row : rs) 
                {
                    dateAdded = row.getDate("interaction_time");
                    owner = row.getString("user");
                }
            }
            if (owner.equals(user))
            {
                HashSet<String> tags = tm.getTags(picid);
                Iterator it;
                it = tags.iterator();
                session.execute(bsDeletePic.bind(picid));
                session.execute(bsDeletePicUserPicList.bind(user, dateAdded));
                session.execute(bsDeletePicComments.bind(picid));
                while (it.hasNext()) {
                    String tag = (String) it.next();
                    deleteTagFromPic(tag, picid);
                }
                session.close();
                return "success";
            }
            session.close();
            return "owner != user";
        }
        catch (Exception ex)
        {
            return ex.getMessage();
        }
    }
    

    public byte[] picresize(String type, byte[] temp) throws IOException{
        InputStream in = new ByteArrayInputStream(temp);
        BufferedImage BI = ImageIO.read(in);
        //BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
        BufferedImage thumbnail = createThumbnail(BI);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, type, baos);
        baos.flush();

        byte[] imageInByte = baos.toByteArray();
        baos.close();
        in.close();
        return imageInByte;
    }
    
    public byte[] picdecolour(String type, byte[] temp) throws IOException {
        InputStream in = new ByteArrayInputStream(temp);
        BufferedImage BI = ImageIO.read(in);
        //BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
        BufferedImage processed = createProcessed(BI);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(processed, type, baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }   
    
    public static BufferedImage createThumbnail(BufferedImage img) {
        img = resize(img, Method.SPEED, 250, OP_ANTIALIAS, OP_GRAYSCALE);
        // Let's add a little border before we return result.
        return pad(img, 2);
    }
    
    public static BufferedImage createProcessed(BufferedImage img) {
        int Width=img.getWidth()-1;
        img = resize(img, Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
        return pad(img, 4);
    }
   
    public static BufferedImage createSepia(BufferedImage img)
    {
        int width = img.getWidth() - 1;
        img = resize(img, Method.SPEED, width, OP_ANTIALIAS);
        img = toSepia(img, 80);
        return img;
    }
    
    public static BufferedImage createNegative(BufferedImage img)
    {
        img = invertImage(img);
        return img;
    }
   
    /**
     * Taken from http://stackoverflow.com/questions/21899824/java-convert-a-greyscale-and-sepia-version-of-an-image-with-bufferedimage
     * @param img
     * @param sepiaIntensity
     * @return 
     */
    public static BufferedImage toSepia(BufferedImage img, int sepiaIntensity) 
    {
        BufferedImage sepia = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        // Play around with this.  20 works well and was recommended
        //   by another developer. 0 produces black/white image
        int sepiaDepth = 20;
        int w = img.getWidth();
        int h = img.getHeight();
        WritableRaster raster = sepia.getRaster();
        // We need 3 integers (for R,G,B color values) per pixel.
        int[] pixels = new int[w * h * 3];
        img.getRaster().getPixels(0, 0, w, h, pixels);
        //  Process 3 ints at a time for each pixel.  Each pixel has 3 RGB
        //    colors in array
        for (int i = 0; i < pixels.length; i += 3) 
        {
            int r = pixels[i];
            int g = pixels[i + 1];
            int b = pixels[i + 2];

            int gry = (r + g + b) / 3;
            r = g = b = gry;
            r = r + (sepiaDepth * 2);
            g = g + sepiaDepth;

            if (r > 255) 
            {
                r = 255;
            }
            if (g > 255) 
            {
                g = 255;
            }
            if (b > 255) 
            {
                b = 255;
            }
            
            // Darken blue color to increase sepia effect
            b -= sepiaIntensity;

            // normalize if out of bounds
            if (b < 0) 
            {
                b = 0;
            }
            if (b > 255) 
            {
                b = 255;
            }
            pixels[i] = r;
            pixels[i + 1] = g;
            pixels[i + 2] = b;
        }
        raster.setPixels(0, 0, w, h, pixels);
        return sepia;
    }
    
    public static BufferedImage invertImage(BufferedImage img) 
    {
        for (int x = 0; x < img.getWidth(); x++) 
        {
            for (int y = 0; y < img.getHeight(); y++) 
            {
                int rgba = img.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color(255 - col.getRed(),
                                255 - col.getGreen(),
                                255 - col.getBlue());
                img.setRGB(x, y, col.getRGB());
            }
        }
        return img;
    }

   
    public java.util.LinkedList<Pic> getPicsForUser(String User) {
        java.util.LinkedList<Pic> Pics = new java.util.LinkedList<>();
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select picid from userpiclist where user =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        User));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return null;
        } else {
            TagModel tm = new TagModel();
            tm.setCluster(cluster);
            for (Row row : rs) {
                Pic pic = new Pic();
                java.util.UUID UUID = row.getUUID("picid");
                System.out.println("UUID" + UUID.toString());
                pic.setUUID(UUID);
                pic.setTags(tm.getTags(UUID));
                Pics.add(pic);
            }
        }
        return Pics;
    }
    
    public void addTagToPic(String tag, java.util.UUID picid)
    {
        System.out.println("Adding " + tag + " to pic");
        Session session = cluster.connect("instagrim");
        TagModel tm = new TagModel();
        tm.setCluster(cluster);
        java.util.UUID tagid = null;
        if (tm.tagExists(tag))
        {
            tagid = tm.getTagID(tag);
        }
        else
        {
            tagid = Convertors.getTimeUUID();
        }
        PreparedStatement psAddTagToPic = session.prepare("insert into tagpic (tagid, picid) values (?,?)");
        PreparedStatement psIncreaseCount = session.prepare("UPDATE tags SET count=count+1 WHERE tagid = ? and tag = ?");
        BoundStatement bsAddTagToPic = new BoundStatement(psAddTagToPic);
        BoundStatement bsIncreaseCount = new BoundStatement(psIncreaseCount);
        session.execute(bsAddTagToPic.bind(tagid, picid));
        session.execute(bsIncreaseCount.bind(tagid,tag));
        session.close();
    }
    
    public void deleteTagFromPic(String tag, java.util.UUID picid)
    {
        System.out.println("Deleting " + tag + " from pic");
        Session session = cluster.connect("instagrim");
        TagModel tm = new TagModel();
        tm.setCluster(cluster);
        java.util.UUID tagid = tm.getTagID(tag);
        PreparedStatement psDelTagFromPic = session.prepare("DELETE FROM tagpic WHERE picid = ? and tagid = ?");
        PreparedStatement psReduceCount = session.prepare("UPDATE tags SET count=count-1 WHERE tagid = ? and tag = ?");
        BoundStatement bsDelTagFromPic = new BoundStatement(psDelTagFromPic);
        BoundStatement bsReduceCount = new BoundStatement(psReduceCount);
        session.execute(bsDelTagFromPic.bind(picid, tagid));
        session.execute(bsReduceCount.bind(tagid,tag));
        session.close();
    }

    public LinkedList<Pic> getTaggedPics(String tag)
    {
        java.util.LinkedList<Pic> pics = new java.util.LinkedList<>();
        //java.util.LinkedList<java.util.UUID> picIDs = new java.util.LinkedList<>();
        Session session = cluster.connect("instagrim");
        TagModel tm = new TagModel();
        tm.setCluster(cluster);
        
        java.util.UUID tagid = tm.getTagID(tag);
        
        PreparedStatement psPics = session.prepare("select picid from tagpic where tagid = ? ALLOW FILTERING");
        BoundStatement bsPics = new BoundStatement(psPics);
        ResultSet rs = null;
        rs = session.execute(bsPics.bind(tagid));
        for (Row row : rs)
        {
            Pic pic = new Pic();
            java.util.UUID UUID = row.getUUID("picid");
            System.out.println("UUID" + UUID.toString());
            pic.setUUID(UUID);
            pic.setTags(tm.getTags(UUID));
            pics.add(pic);
        }   
        session.close();
        return pics;
    }

    public LinkedList<Pic> getLatestPics()
    {
        LinkedList<Pic> latest = new LinkedList<Pic>();
        Session session = cluster.connect("instagrim");
        PreparedStatement psLatest = session.prepare("select picid, user from userpiclist Limit 64");
        BoundStatement bsLatest = new BoundStatement(psLatest);
        ResultSet rs = null;
        rs = session.execute(bsLatest);
        if(rs.isExhausted())
        {
            System.out.println("No latest pics");
            return latest;       
        }
        TagModel tm = new TagModel();
        tm.setCluster(cluster);
        
        for (Row row : rs)
        {
            Pic pic = new Pic();
            java.util.UUID UUID = row.getUUID("picid");
            String owner = row.getString("user");
            System.out.println("UUID" + UUID.toString());
            pic.setUUID(UUID);
            pic.setOwner(owner);
            pic.setTags(tm.getTags(UUID));
            latest.add(pic);
        }
                        //pic.setTags(tm.getTags(UUID));
        return latest;
    }
    
    public Pic getPic(int image_type, java.util.UUID picid) {
        Session session = cluster.connect("instagrim");
        System.out.println("Getting picture..");
        ByteBuffer bImage = null;
        String type = null;
        int length = 0;
        Pic p = new Pic();
        try {
            Convertors convertor = new Convertors();
            ResultSet rs = null;
            PreparedStatement ps = null;
         
            if (image_type == Convertors.DISPLAY_IMAGE) 
            {    
                ps = session.prepare("select image,imagelength,type from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_THUMB) {
                ps = session.prepare("select thumb,imagelength,thumblength,type from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_PROCESSED ) {
                ps = session.prepare("select processed,processedlength,type from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_NEGATIVE) {
                ps = session.prepare("select image,imagelength,negative,negativelength,type from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_SEPIA) {
                System.out.println("Trying to fetch Sepia");
                ps = session.prepare("select image,imagelength,sepia,sepialength,type from pics where picid =?");
            }
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute( // this is where the query is executed
                    boundStatement.bind( // here you are binding the 'boundStatement'
                            picid));

            
            if (rs.isExhausted()) 
            {
                session.close();
                System.out.println("No Images returned");
                return null;
                
            } else {
                for (Row row : rs) {
                    //System.out.println(i);
                    if (image_type == Convertors.DISPLAY_IMAGE) {
                        bImage = row.getBytes("image");
                        length = row.getInt("imagelength");
                        
                    } else if (image_type == Convertors.DISPLAY_THUMB) {
                        bImage = row.getBytes("thumb");
                        length = row.getInt("thumblength");
                
                    } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                        bImage = row.getBytes("processed");
                        length = row.getInt("processedlength");
                    }
                    else if (image_type == Convertors.DISPLAY_SEPIA)
                    {
                        bImage = row.getBytes("sepia");
                        length = row.getInt("sepialength");
                    }
                    else if (image_type == Convertors.DISPLAY_NEGATIVE)
                    {
                        bImage = row.getBytes("negative");
                        length = row.getInt("negativelength");
                    }
                    type = row.getString("type");
                    
                    if(bImage == null && (image_type == Convertors.DISPLAY_SEPIA || image_type == Convertors.DISPLAY_NEGATIVE))
                    {
                        String types[]=Convertors.SplitFiletype(type);
                        bImage = row.getBytes("image");  
                        byte[] temp = new byte[bImage.remaining()];
                        bImage.get(temp);
                        byte[] bArray = applyFilter(image_type, types[1], temp);
                        bImage = ByteBuffer.wrap(bArray);
                        length = bArray.length;
                        insertFiltered(image_type, bArray, picid);
                    }
                    
                    p.setPic(bImage, length, type);
                }
            }
        } catch (Exception et) {
            System.out.println("Can't get Pic" + et);
            session.close();
            return null;
        }
        session.close();
//        if (image_type == Convertors.DISPLAY_SEPIA || image_type == Convertors.DISPLAY_NEGATIVE)
//        {
//            System.out.println("Converting on the fly");
//            bImage = applyFilter(image_type,bImage,type);
//        }

        return p;
    } 
    
    private void insertFiltered(int image_type, byte[] bArray,  java.util.UUID picid)
    {
        Session session = cluster.connect("instagrim");
        
        int length = bArray.length;
        ByteBuffer bImage = ByteBuffer.wrap(bArray);
        
        PreparedStatement psImage = null;

        if (image_type == Convertors.DISPLAY_SEPIA )
        {
            System.out.println("Insertding Sepia..");
            psImage = session.prepare("Update pics set sepia = ?, sepialength = ? where picid =?");
        }
        else if (image_type == Convertors.DISPLAY_NEGATIVE)
        {
            System.out.println("Insertding Negative..");
            psImage = session.prepare("Update pics set negative = ?, negativelength = ? where picid =?");
        }        
        BoundStatement bsImage = new BoundStatement(psImage);
        session.execute(bsImage.bind(bImage,length,picid));

        session.close();
    }
    
    private byte[] applyFilter(int image_type, String type, byte[] temp) throws IOException
    {
        InputStream in = new ByteArrayInputStream(temp);
        BufferedImage BI = ImageIO.read(in);
        BufferedImage filtered = null;

        if (image_type == Convertors.DISPLAY_SEPIA)
        {
            System.out.println("Converting to Sepia..");
             filtered = createSepia(BI);
        }
        else if (image_type == Convertors.DISPLAY_NEGATIVE)
        {
            System.out.println("Converting to negative...");
            filtered = createNegative(BI);
        }
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ImageIO.write(filtered, type, bs);
        bs.flush();

        byte[] imageInByte = bs.toByteArray();
        bs.close();
        in.close();
        return imageInByte;
    } 
}
