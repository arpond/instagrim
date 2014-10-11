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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.models.User;

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
            error("You must enter a username",response);
            return;
        }
        else if (password.equals(""))
        {
            error("You must enter a passweord", response);
            return;
        }
        else if (!password.equals(confirm))
        {
            error("Your passwords do not match", response);
            return;
        }
        else if (email.equals(""))
        {
            error("You must enter an email address", response);
            return;
        }
        
        User us=new User();
        us.setCluster(cluster);
        String regResult = us.RegisterUser(username, password, email);
 
        if (regResult.equals("success"))
        {
            request.setAttribute("message", "Registeration Succesful");
            RequestDispatcher view = request.getRequestDispatcher("/message.jsp");
            view.forward(request, response);
        }
        else
        {
            request.setAttribute("error", "There was a problem with your registation - " + regResult );
            RequestDispatcher view = request.getRequestDispatcher("/error.jsp");
            view.forward(request, response);
        }
        
	response.sendRedirect("/Instagrim");
        
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

    private void error(String mess, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = null;
        out = new PrintWriter(response.getOutputStream());
        out.println("<h1>You have a na error in your input</h1>");
        out.println("<h2>" + mess + "</h2>");
        out.close();
        return;
    }

}
