/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.UserDetails;

/**
 *
 * @author Andrew
 */
@WebServlet(urlPatterns = {
    "/Profile",
    "/Profile/*"
})
@MultipartConfig
public class Profile extends HttpServlet {
    
    private HashMap CommandsMap = new HashMap();
    private Cluster cluster;
    
    public Profile()
    {
        super();
        CommandsMap.put("View", 1);
        CommandsMap.put("Edit", 2);
    }
    
    

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        int command = 0;
        try {
            command = (Integer) CommandsMap.get(args[2]);
        } catch (Exception et) {
            String debug = "";
            for (int i = 0; i<args.length; i++)
            {
                debug += ", " + args[i];
            }   
            error("Bad Operator " + debug,  request, response);
            return;
        }
        
        try
        {
            switch (command) 
            {
            case 1:
                viewProfile(args[3], request, response);
                break;
            case 2:
                editProfile(args[3], request, response);
                break;
            default:
                error("Bad Operator - default",  request, response);
            }
        }
        catch (ArrayIndexOutOfBoundsException oobex)
        {
            error("ArrayOutOfBounds",  request, response);
        }  
    }
    
    private void viewProfile(String owner, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        User user = new User();
        user.setCluster(cluster);
        
        System.out.println(owner);
        UserDetails userDetails = user.getUserDetails(owner);
        System.out.println(userDetails);
        
        
        if (userDetails == null)
        {
            System.out.println("Big bad error!!");
            error("User not found", request, response);
            return;
        }
        request.setAttribute("details",userDetails);
        RequestDispatcher view = request.getRequestDispatcher("/viewProfile.jsp");
        view.include(request, response);
    }
    
    private void editProfile(String owner, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        User user = new User();
        user.setCluster(cluster);
        
        if (!userMatch(owner,request))
        {
            error("You do not have permission to do that!", request, response);
            return;
        }
                
        UserDetails userDetails = user.getUserDetails(owner);
        request.setAttribute("details",userDetails);
        RequestDispatcher view = request.getRequestDispatcher("/editProfile.jsp");
        view.forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //String username=request.getParameter("username");
        //String password=request.getParameter("password");
        String owner = request.getParameter("owner");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String mails = request.getParameter("email");
                     
        if (firstname == null)
        {
            firstname = "";
        }
        if (lastname == null)
        {
            lastname = "";
        }
        if (mails == null)
        {
            mails = "";
        }
        
        User us=new User();
        us.setCluster(cluster);
        
        if (!userMatch(owner,request))
        {
            error("You do not have permission to do that!", request, response);
            return;
        }
        
        HttpSession session=request.getSession();
        System.out.println("Session in servlet "+session);
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        String current = lg.getUsername();
               
        us.updateUserDetails(current, firstname, lastname, mails);
        response.sendRedirect("/Instagrim/Profile/View/" + owner);
    }
    
    private boolean userMatch(String owner, HttpServletRequest request)
    {
        HttpSession session = request.getSession();
            
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        if (lg == null || !lg.getlogedin())
        {
            return false;
        }
        
        String current = lg.getUsername();
        if (!current.equals(owner))
        {
            return false;
        }
        return true;
    }
    
    private void error(String mess, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("error", mess);
        RequestDispatcher view = request.getRequestDispatcher("/error.jsp");
        view.forward(request, response);
        return;
    }
}
