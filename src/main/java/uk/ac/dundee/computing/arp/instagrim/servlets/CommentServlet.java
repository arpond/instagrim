/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.arp.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.arp.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.arp.instagrim.lib.Error;
import uk.ac.dundee.computing.arp.instagrim.lib.UserPermission;
import uk.ac.dundee.computing.arp.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.arp.instagrim.models.CommentModel;

/**
 * Comment Servlet
 * 
 * @author Andrew
 */
@WebServlet(name = "AddComment", urlPatterns = {"/AddComment"})
public class CommentServlet  extends HttpServlet {
    
    Cluster cluster;

    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }
    
    /**
     * Adds a comment to a a picture
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        CommentModel cm = new CommentModel();
        cm.setCluster(cluster);
        
        String commentText = request.getParameter("comment");
        String picID = request.getParameter("picID");
        
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        String permission = UserPermission.hasPermission(UserPermission.LOGGED_IN, lg, "");
        
        if (!permission.equals("success"))
        {
            Error.error(permission, request, response);
            return;
        }
        String user = lg.getUsername();
        
        cm.insertComment(user, picID, commentText);
        
        request.setAttribute("message", "Comment Posted");
        RequestDispatcher view = request.getRequestDispatcher("/message.jsp");
        view.forward(request, response);
    }
    
    @Override
    public void destroy()
    {
        cluster.close();
    }
}