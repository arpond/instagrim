/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.lib;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Error class
 * 
 * @author Andrew
 */
public class Error {
    
    /**
     * Method for forwarding to the error page with an error
     * 
     * @param mess The error message to pass to the error page
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    public static void error(String mess, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("error", mess);
        RequestDispatcher view = request.getRequestDispatcher("/error.jsp");
        view.forward(request, response);
    }
}
