/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.util.LinkedList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.lib.Error;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.TagModel;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

/**
 *
 * @author Andrew
 */
@WebServlet(urlPatterns = {
    "/Tag",
    "/Tag/*"
})
@MultipartConfig
public class Tags  extends HttpServlet {
    
    Cluster cluster;

    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        String args[] = Convertors.SplitRequestPath(request);
        String tag = "";
        try
        {
            tag = args[2];
        } 
        catch (ArrayIndexOutOfBoundsException oobex) {
            Error.error("ArrayOutOfBounds", request, response);
            return;
        }
        
        TagModel tm = new TagModel();
        tm.setCluster(cluster);
        LinkedList<Pic> pics = new LinkedList<Pic>();
        if (!tm.tagExists(tag))
        {
            System.out.println("No results");
            request.setAttribute("Pics", pics);
            RequestDispatcher rd = request.getRequestDispatcher("/tagPics.jsp");
            rd.forward(request, response);
            return;
        }
        
        PicModel pm = new PicModel();
        pm.setCluster(cluster);
        pics = pm.getTaggedPics(tag);
        
        request.setAttribute("Pics", pics);
        RequestDispatcher rd = request.getRequestDispatcher("/tagPics.jsp");
        rd.forward(request, response);
    }    
}
