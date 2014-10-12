<%-- 
    Document   : profile
    Created on : 11-Oct-2014, 16:52:10
    Author     : Andrew
--%>



<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Set" %>
<%@page import="java.util.Date" %>
<%@page import="java.util.Iterator" %>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.UserDetails" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <header>
            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>
            <%
            UserDetails userDetails = (UserDetails) request.getAttribute("details");
            String owner = userDetails.getUsername();
            String firstname = userDetails.getFirstname();
            String lastname = userDetails.getLastname();
            Set<String> emails = userDetails.getEmails();
            Date joined = userDetails.getJoined();
            String fullname = "unknown";
            
            if (firstname != null && lastname != null)
            {
                fullname = firstname + " " + lastname;
            }
            else if (firstname != null)
            {
                fullname = firstname;
            }
            %>
            
            <ul>
                <li>Profile: <%=owner%></li>
                <li>Name: <%=fullname%></li>
                <li>Email: 
                <%
                Iterator<String> iterator;
                iterator = emails.iterator();
                while (iterator.hasNext()) 
                {
                    String email = (String) iterator.next();
                %>
                    <%=email%> <br />
                <%
                }
                %>
                </li>
                <li>Address: </li>
                <li>Joined: <%=joined.toString()%></li>
            </ul>
        </header>
    </body>
</html>