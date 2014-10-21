/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Error;
import uk.ac.dundee.computing.aec.instagrim.models.UserModel;

/**
 * TODO - Display errors alongside fields with errors in it.
 * TODO - Also pass correct fields apart from password
 */

/**
 *
 * @author Administrator
 */
@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class Register extends HttpServlet {
    Cluster cluster=null;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }




    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username=request.getParameter("username");
        String password=request.getParameter("password");
        String confirm = request.getParameter("confirmpass");
        //String firstname=request.getParameter("firstname");
        //String lastname=request.getParameter("lastname");
        String email=request.getParameter("email");
        
        if (username.equals(""))
        {
            Error.error("You must enter a username", request, response);
            return;
        }
        else if (password.equals(""))
        {
            Error.error("You must enter a passweord", request, response);
            return;
        }
        else if (!password.equals(confirm))
        {
            Error.error("Your passwords do not match", request, response);
            return;
        }
        else if (email.equals(""))
        {
            Error.error("You must enter an email address", request, response);
            return;
        }
        
        UserModel us=new UserModel();
        us.setCluster(cluster);
        //System.out.println(getServletContext().getContextPath());
        ServletContext context = request.getSession().getServletContext();
        
        System.out.println("Context Path: "+ context.getContextPath());
        String regResult = us.RegisterUser(username, password, email, context);
 
        if (regResult.equals("success"))
        {
            request.setAttribute("message", "Registration Succesful");
            RequestDispatcher view = request.getRequestDispatcher("/message.jsp");
            view.forward(request, response);
        }
        else
        {
            request.setAttribute("error", "There was a problem with your registation - " + regResult );
            RequestDispatcher view = request.getRequestDispatcher("/error.jsp");
            view.forward(request, response);
        }
        
	//response.sendRedirect("/Instagrim");
        
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    @Override
    public void destroy()
    {
        cluster.close();
    }
}
