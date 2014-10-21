/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javax.servlet.http.Part;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.lib.Error;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.UserModel;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
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
    private HashMap PostMap = new HashMap();
    private Cluster cluster;

    public Profile() {
        super();
        CommandsMap.put("View", 1);
        CommandsMap.put("Edit", 2);
        CommandsMap.put("ProfilePic", 3);
        PostMap.put("update", 1);
        PostMap.put("upload", 2);
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
            for (int i = 0; i < args.length; i++) {
                debug += ", " + args[i];
            }
            Error.error("Bad Operator " + debug, request, response);
            return;
        }

        try {
            switch (command) {
                case 1:
                    viewProfile(args[3], request, response);
                    break;
                case 2:
                    editProfile(args[3], request, response);
                    break;
                case 3:
                    displayProfilePic(args[3], request, response);
                    break;
                default:
                    Error.error("Bad Operator - default", request, response);
            }
        } catch (ArrayIndexOutOfBoundsException oobex) {
            Error.error("ArrayOutOfBounds", request, response);
        }
    }

    private void displayProfilePic(String owner, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserModel um = new UserModel();
        um.setCluster(cluster);
        Pic p;

        try {
            p = um.getProfilePic(owner);
        } catch (IllegalArgumentException iae) {
            Error.error("Image Not Found", request, response);
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

    private void viewProfile(String owner, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserModel user = new UserModel();
        user.setCluster(cluster);
        System.out.println("Getting profile picture");
        System.out.println(owner);
        UserDetails userDetails = user.getUserDetails(owner);
        System.out.println(userDetails);

        if (userDetails == null) {
            System.out.println("Big bad error!!");
            Error.error("User not found", request, response);
            return;
        }

        request.setAttribute("details", userDetails);
        RequestDispatcher view = request.getRequestDispatcher("/viewProfile.jsp");
        view.include(request, response);
    }

    private void editProfile(String owner, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserModel user = new UserModel();
        user.setCluster(cluster);

        if (!userMatch(owner, request)) {
            Error.error("You do not have permission to do that!", request, response);
            return;
        }

        UserDetails userDetails = user.getUserDetails(owner);
        request.setAttribute("details", userDetails);
        RequestDispatcher view = request.getRequestDispatcher("/editProfile.jsp");
        view.forward(request, response);
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String owner = request.getParameter("owner");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String mails = request.getParameter("email");
        String street = request.getParameter("street");
        String city = request.getParameter("city");
        String zip = request.getParameter("zip");

        //String username=request.getParameter("username");
        //String password=request.getParameter("password");
        int zipcode = 0;

        if (!street.equals("") || !city.equals("") || !zip.equals("")) {
            if (!street.equals("") && !city.equals("") && !zip.equals("")) {
                try {
                    zipcode = Integer.parseInt(zip);
                } catch (Exception e) {
                    Error.error("Your zipcode is invalid", request, response);
                    return;
                }
            } else {
                Error.error("Your address is incomplete", request, response);
                return;
            }
        } else {
            street = "";
            city = "";
            zipcode = 0;
        }

        UserModel us = new UserModel();
        us.setCluster(cluster);

        if (!userMatch(owner, request)) {
            Error.error("You do not have permission to do that!", request, response);
            return;
        }

        HttpSession session = request.getSession();
        System.out.println("Session in servlet " + session);
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        String current = lg.getUsername();

        us.updateUserDetails(current, firstname, lastname, mails, street, city, zipcode);
        response.sendRedirect("/Instagrim/Profile/View/" + owner);
    }

    private void uploadProfilePic(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String owner = request.getParameter("owner");

        for (Part part : request.getParts()) {
            System.out.println("Part Name " + part.getName());
            if (part.getName().equals("upfile"))
            {
                String type = part.getContentType();
                if (!type.equalsIgnoreCase("image/jpeg") && !type.equalsIgnoreCase("image/png")) {
                    Error.error("Unrecognised File Type", request, response);
                    return;
                }
                long size = part.getSize();
                if (size > 10485760L) {
                    Error.error("The file size is too large", request, response);
                    return;
                }
                //String filename = part.getSubmittedFileName();
                InputStream is = request.getPart(part.getName()).getInputStream();
                int i = is.available();
                HttpSession session = request.getSession();
                LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                String username = "";
                if (lg.getlogedin()) {
                    username = lg.getUsername();
                } else {
                    Error.error("You are not logged in!", request, response);
                    return;
                }
                if (!username.equals(owner)) {
                    System.out.println("username: " + username + " owner: " + owner);
                    Error.error("You do not have permission to do that!", request, response);
                    return;
                }

                if (i > 0) {
                    // Watch for negative array size..
                    byte[] b = new byte[i + 1];
                    is.read(b);
                    System.out.println("Length : " + b.length);
                    UserModel u = new UserModel();
                    u.setCluster(cluster);
                    u.insertProfilePic(b, owner, type);
                    is.close();
                }
                response.sendRedirect("/Instagrim/Profile/View/" + owner);    
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        int command = 0;
        try {
            command = (Integer) PostMap.get(action);
        } catch (Exception et) {
            Error.error("Bad Operator - Post - Profile - " + action, request, response);
            return;
        }

        switch (command) {
            case 1:
                updateProfile(request, response);
                break;
            case 2:
                uploadProfilePic(request, response);
                break;
            default:
                Error.error("Bad Operator - default", request, response);
        }

    }

    private boolean userMatch(String owner, HttpServletRequest request) {
        HttpSession session = request.getSession();

        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        if (lg == null || !lg.getlogedin()) {
            return false;
        }

        String current = lg.getUsername();
        if (!current.equals(owner)) {
            return false;
        }
        return true;
    }
}
