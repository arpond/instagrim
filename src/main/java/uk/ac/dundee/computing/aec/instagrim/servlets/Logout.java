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

/**
 *
 * @author Andrew
 */
@WebServlet(name = "Logout", urlPatterns = {"/Logout"})
public class Logout extends HttpServlet {
    
    Cluster cluster;

    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }
    
    /**
     * Logs out the user
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session=request.getSession();
        session.invalidate();
        request.setAttribute("message", "You have succesfully logged out");
        RequestDispatcher view = request.getRequestDispatcher("/message.jsp");
        view.forward(request, response);
    }
    
        
    @Override
    public void destroy()
    {
        cluster.close();
    }
}
