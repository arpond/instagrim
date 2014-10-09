package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

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
    "/Delete/*"
})
@MultipartConfig

/** 
 * TODO - Improve when Image doesn't exist (invalid UUID line 122 see /Images/namedoesn'texist, UUID.java 194)
 * TODO - Improve when "/Image/" and "/Image" (array out of bounds line 94)
 * TODO - Improve when "/Images/" and "/Images" (array out of bounds line 97)
 * TODO - Improve when "/Thumb/" and "/Thumb" (array out of bounds line 100)
 * TODO - Improve when Thumb doesn't exist (invalid UUID line 122, UUID.java 194)
 * TODO - Improve how unrecognised file types are handled
 * TOFIX - Negative array size exception when uploading huge file (line 154)
 */

public class Image extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;
    private HashMap CommandsMap = new HashMap();
    
    

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Image() {
        super();
        // TODO Auto-generated constructor stub
        CommandsMap.put("Image", 1);
        CommandsMap.put("Images", 2);
        CommandsMap.put("Thumb", 3);
        CommandsMap.put("Delete", 4);

    }

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String args[] = Convertors.SplitRequestPath(request);
        int command;
        try {
            command = (Integer) CommandsMap.get(args[1]);
        } catch (Exception et) {
            error("Bad Operator",  request, response);
            return;
        }
        try
        {
            switch (command) 
            {
            case 1:
                DisplayImage(Convertors.DISPLAY_PROCESSED,args[2], response, request);
                break;
            case 2:
                DisplayImageList(args[2], request, response);
                break;
            case 3:
                DisplayImage(Convertors.DISPLAY_THUMB,args[2],  response, request);
                break;
            case 4:
                DeleteImage(args[2], request, response);
                break;
            default:
                error("Bad Operator",  request, response);
            }
        }
        catch (ArrayIndexOutOfBoundsException oobex)
        {
            error("ArrayOutOfBounds",  request, response);
        }  
    }

    private void DisplayImageList(String User, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(User);
        RequestDispatcher rd = request.getRequestDispatcher("/UsersPics.jsp");
        request.setAttribute("Pics", lsPics);
        rd.forward(request, response);

    }

    private void DeleteImage(String picid, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        String user = lg.getUsername();
        //boolean success = tm.picDelete(user, picid);
        String success = tm.picDelete(user, java.util.UUID.fromString(picid));  
        if (!success.equals("success"))
        {
            error("Image Not Found\n" + success, request, response);
            return;
        }
        else
        {
            request.setAttribute("message", "Image Successfully Deleted");
            RequestDispatcher view = request.getRequestDispatcher("/message.jsp");
            view.forward(request, response);
        }
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
            error("Image Not Found",  request, response);
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        for (Part part : request.getParts()) {
            System.out.println("Part Name " + part.getName());
            String type = part.getContentType();
            if (!type.equalsIgnoreCase("image/jpeg") && !type.equalsIgnoreCase("image/png"))
            {
                error("Unrecognised File Type",  request, response);
                return;
            }
            long size = part.getSize();
            if (size > 104857600L)
            {
                error("The file size is too large",  request, response);
                return;
            }
            String filename = part.getSubmittedFileName();
            InputStream is = request.getPart(part.getName()).getInputStream();
            int i = is.available();
            HttpSession session=request.getSession();
            LoggedIn lg= (LoggedIn)session.getAttribute("LoggedIn");    
            String username="majed";
            if (lg.getlogedin()){
                username=lg.getUsername();
            }
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

    private void error(String mess, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("error", mess);
        RequestDispatcher view = request.getRequestDispatcher("/error.jsp");
        view.forward(request, response);
        return;
    }
}
