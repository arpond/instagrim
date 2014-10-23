/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

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
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Error;
import uk.ac.dundee.computing.aec.instagrim.lib.UserPermission;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.models.CommentModel;

/**
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