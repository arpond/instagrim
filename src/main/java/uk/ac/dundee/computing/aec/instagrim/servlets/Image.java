package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import javax.persistence.Convert;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.lib.Error;
import uk.ac.dundee.computing.aec.instagrim.lib.UserPermission;
import uk.ac.dundee.computing.aec.instagrim.models.UserModel;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.CommentModel;
import uk.ac.dundee.computing.aec.instagrim.models.TagModel;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import uk.ac.dundee.computing.aec.instagrim.stores.Comment;

/**
 * Servlet implementation class Image
 */
@WebServlet(urlPatterns = {
    "/Image",
    "/Image/*",
    "/Thumb/*",
    "/Images",
    "/Images/*",
    "/Delete/",
    "/Delete/*",
})
@MultipartConfig

/** 
 * TODO - Improve when Image doesn't exist (invalid UUID line 122 see /Images/namedoesn'texist, UUID.java 194)
 * TODO - Improve when "/Image/" and "/Image" (array out of bounds line 94)
 * TODO - Improve when "/Images/" and "/Images" (array out of bounds line 97)
 * TODO - Improve when "/Thumb/" and "/Thumb" (array out of bounds line 100)
 * TODO - Improve when Thumb doesn't exist (invalid UUID line 122, UUID.java 194)
 * TODO - Improve how unrecognised file types are handled
 */

public class Image extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;
    private HashMap CommandsMap = new HashMap();
    private HashMap PostMap = new HashMap();
    
    

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Image() {
        super();
        // TODO Auto-generated constructor stub
        CommandsMap.put("Image", 1);
        CommandsMap.put("Images", 2);
        CommandsMap.put("Thumb", 3);
        CommandsMap.put("Comments", 5);
        CommandsMap.put("Sepia", 6);
        CommandsMap.put("Negative", 7);
        PostMap.put("upload", 1);
    }

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String args[] = Convertors.SplitRequestPath(request);
        int command, command2 = 0;
        try {
            try
            {
                command = (Integer) CommandsMap.get(args[2]);
                command2 = (Integer) CommandsMap.get(args[1]);
            }
            catch (Exception et)
            {
                command = (Integer) CommandsMap.get(args[1]);
            }
        } catch (Exception et) {
            Error.error("Bad Operator",  request, response);
            return;
        }
        System.out.println("Command: " + command);
        try
        {
            switch (command) 
            {
            case 1:
                DisplayImage(Convertors.DISPLAY_PROCESSED,args[2], response, request);
                break;
            case 2:
                if (args.length == 2)
                {
                    DisplayLatestImages(request,response);
                }
                else
                {
                    DisplayImageList(args[2], request, response);
                }
                break;
            case 3:
                DisplayImage(Convertors.DISPLAY_THUMB,args[2],  response, request);
                break;
            case 4:
                //DeleteImage(args[3], args[2], request, response);
                //break;
            case 5:
                if (command2 == 1)
                {
                    DisplayComments(args[3],  response, request);
                }
                else
                {
                    Error.error("Bad Operator",  request, response);
                }
                break;
            case 6:
                if (command2 == 1)
                {   
                    DisplayImage(Convertors.DISPLAY_SEPIA,args[3], response, request);
                }
                else
                {
                    Error.error("Bad Operator",  request, response);
                }
                break;
            case 7:
                if (command2 == 1)
                {
                    DisplayImage(Convertors.DISPLAY_NEGATIVE,args[3],response,request);
                }
                else
                {
                    Error.error("Bad Operator",  request, response);
                }
                break;
            default:
                Error.error("Bad Operator",  request, response);
            }
        }
        catch (ArrayIndexOutOfBoundsException oobex)
        {
            Error.error("ArrayOutOfBounds",  request, response);
        }  
    }
    
    private void DisplayLatestImages(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        PicModel pm = new PicModel();
        pm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = pm.getLatestPics();
        
        request.setAttribute("Pics", lsPics);
        RequestDispatcher rd = request.getRequestDispatcher("/latestPics.jsp");
        rd.forward(request, response);
    }       

    private void DisplayImageList(String owner, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(owner);
         
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        
        String permission = UserPermission.hasPermission(UserPermission.OWNER_MATCH, lg, owner);
        
        if (!permission.equals("success"))
        {
           request.setAttribute("Match", false);
        }
        else
        {
            request.setAttribute("Match",true);
        }
        
        request.setAttribute("Owner", owner);
        request.setAttribute("Pics", lsPics);
        RequestDispatcher rd = request.getRequestDispatcher("/UsersPics.jsp");
        rd.forward(request, response);
    }
    
    private void DisplayImage(int type,String Image, HttpServletResponse response, HttpServletRequest request) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        Pic p;
  
        try
        {
            p = tm.getPic(type,java.util.UUID.fromString(Image));
        }
        catch (IllegalArgumentException iae)
        {
            Error.error("Image Not Found",  request, response);
            return;
        }
        OutputStream out = response.getOutputStream();

        response.setContentType(p.getType());
        response.setContentLength(p.getLength());
        //out.write(Image);
        InputStream is = new ByteArrayInputStream(p.getBytes());
        BufferedInputStream input = new BufferedInputStream(is);
        byte[] buffer = new byte[8192];
        for (int length = 0; (length = input.read(buffer)) > 0;) {
            out.write(buffer, 0, length);
        }
        out.close();
    }
    
    public void DisplayComments(String image, HttpServletResponse response, HttpServletRequest request) throws ServletException, IOException
    {
        System.out.println("Comments");

        CommentModel cm = new CommentModel();
        cm.setCluster(cluster);
        ArrayList<Comment> comments = cm.getComments(java.util.UUID.fromString(image));

        request.setAttribute("comments",comments);
        request.setAttribute("picture",image);
        RequestDispatcher view = request.getRequestDispatcher("/comment.jsp");
        view.include(request, response);        
    }
    
    /**
     * Function for deleting an image
     * 
     * 
     * @param picid The id of the image to delete
     * @param request the http servlet request
     * @param response the http servlet response
     * @throws ServletException
     * @throws IOException 
     */
    private String DeleteImage(String picid, String owner, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        
        String permission = UserPermission.hasPermission(UserPermission.OWNER_MATCH, lg, owner);
        
        if (!permission.equals("success"))
        {
            return permission;
        }
        
        String success = tm.picDelete(owner, java.util.UUID.fromString(picid));  
        if (!success.equals("success"))
        {
            //Error.error("Image Not Found" + success, request, response);
            return "Image Not Found";
        }
        else
        {
            //request.setAttribute("message", "Image Successfully Deleted");
            //RequestDispatcher view = request.getRequestDispatcher("/message.jsp");
            //view.forward(request, response);
        }
        return "success";
    }
    
    private void UploadImage (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        for (Part part : request.getParts()) {
            System.out.println("Part Name " + part.getName());
            String type = part.getContentType();
            System.out.println(type);
            if (!type.equalsIgnoreCase("image/jpeg") && !type.equalsIgnoreCase("image/png"))
            {
                Error.error("Unrecognised File Type",  request, response);
                return;
            }
            long size = part.getSize();
            if (size > 104857600L)
            {
                Error.error("The file size is too large",  request, response);
                return;
            }
            String filename = part.getSubmittedFileName();
            InputStream is = request.getPart(part.getName()).getInputStream();
            int i = is.available();

            HttpSession session = request.getSession();
            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");

            String permission = UserPermission.hasPermission(UserPermission.LOGGED_IN, lg, "");
            
            if (!permission.equals("success"))
            {
                Error.error(permission, request, response);
                return;
            }
            String username = lg.getUsername();
            
            if (i > 0) {
                // Watch for negative array size..
                byte[] b = new byte[i+1];
                is.read(b);
                System.out.println("Length : " + b.length);
                PicModel tm = new PicModel();
                tm.setCluster(cluster);
                tm.insertPic(b, type, filename, username);

                is.close();
            }
            RequestDispatcher rd = request.getRequestDispatcher("/upload.jsp");
            rd.forward(request, response);
        }
    }
    
    private void updateTags(String picid, String[] tags)
    {   
        System.out.println("Updating Tags..");
        TagModel tm = new TagModel();
        tm.setCluster(cluster);
        java.util.UUID picUUID = java.util.UUID.fromString(picid);
        HashSet<String> oldTags = tm.getTags(picUUID);
        HashSet<String> newTags = new HashSet<String>();
        for (String tag : tags)
        {
            newTags.add(tag);
        }
        HashSet<String> toDelete = new HashSet<String>(oldTags);
        HashSet<String> toAdd = new HashSet<String>(newTags)    ;
        toDelete.removeAll(newTags);
        toAdd.removeAll(oldTags);
        
        PicModel pm = new PicModel();
        pm.setCluster(cluster);
               
        if (!toDelete.isEmpty())
        {
            System.out.println("deleting..");
            Iterator<String> itDel;
            itDel = toDelete.iterator();
            while (itDel.hasNext())
            {
                pm.deleteTagFromPic(itDel.next(), picUUID);
            }
        }
        
        if (!toAdd.isEmpty())
        {
            System.out.println("adding..");
            Iterator<String> itAdd;
            itAdd = toAdd.iterator();
            while (itAdd.hasNext())
            {
                pm.addTagToPic(itAdd.next(), picUUID);
            }
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        System.out.println("Inside doPut..");
        String args[] = Convertors.SplitRequestPath(request);

        System.out.println("Arguments: ");
        for (String arg : args)
        {
            System.out.println(arg);
        }
        
        System.out.println("Tags: " + args[4]);
        String owner = args[2];
        String picid = args[3];
        String tags[] = args[4].split(",");
       
        for (String tag : tags)
        {
            System.out.println(tag);
        }
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");

        response.setContentType("application/json");
        String permission = UserPermission.hasPermission(UserPermission.OWNER_MATCH, lg, owner);
        if (!permission.equals("success"))
        {
            System.out.println("Fail Permission");
            response.getWriter().write("{ \"success\": false, \"message\": \"" + permission + "\" }");
        }
        else
        {
            updateTags(picid,tags);
            System.out.println("Success");
            response.getWriter().write("{ \"success\": true, \"message\": \"Tags Updated\" }");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {         
        System.out.println("Inside doDelete");
        String args[] = Convertors.SplitRequestPath(request);
        
        String owner = args[2];
        String picid = args[3];
        System.out.println("owner:"+owner+" picid:" + picid);
        
        String result = DeleteImage(picid,owner,request,response);
        //boolean success = true;
        response.setContentType("application/json");
        if (result.equals("success"))
        {
            response.getWriter().write("{ \"success\": true, \"message\": \"Image Succesfully Deleted\" }");
        }
        else
        {
            response.getWriter().write("{ \"success\": false, \"message\": \"" + result + "\" }");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = (String) request.getParameter("action");
        System.out.println(action);
        int command;
        try {
            command = (Integer) PostMap.get(action);
        } catch (Exception et) {
            Error.error("Bad Post Operator",  request, response);
            return;
        }
        
        switch (command)
        {
            case 1:
                UploadImage (request,response);
                break;
            default:
                Error.error("Bad Post Operator",  request, response);
        }
        
        

    }
    
    @Override
    public void destroy()
    {
        cluster.close();
    }
}
