<%-- 
    Document   : profile
    Created on : 11-Oct-2014, 16:52:10
    Author     : Andrew
--%>



<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.HashSet" %>
<%@page import="java.util.Date" %>
<%@page import="java.util.Iterator" %>
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
            String owner = (String) request.getAttribute("owner");
            String firstname = (String) request.getAttribute("firstname");
            String lastname = (String) request.getAttribute("lastname");
            HashSet emails = (HashSet) request.getAttribute("emails");
            Date joined = (Date) request.getAttribute("joined");
            %>
            
            <ul>
                <li>Profile: <%=owner%></li>
                <li>Name: <%=firstname + " " + lastname%></li>
                <li>Email: 
                <%
                Iterator<String> iterator;
                iterator = emails.iterator();
                while (iterator.hasNext()) 
                {
                    String email = (String) iterator.next();
                %>
                    <%=email + "<br/>"%>
                <%
                }
                %>
                
                %>
                </li>
                <li>Address: </li>
                <li>Joined: <%=joined.toString()%></li>
            </ul>
        </header>
    </body>
</html>
