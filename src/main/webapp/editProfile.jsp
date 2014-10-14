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
            
            String mails = "";
            
            if (firstname == null)
            {
                firstname =  "";
            }
            
            if (lastname == null)
            {
                lastname = "";
            }
            
            Iterator<String> iterator;
            iterator = emails.iterator();
            while (iterator.hasNext()) 
            {
                mails += (String) iterator.next() + ";";
            }
            
            //Date joined = (Date) request.getAttribute("joined");
            %>
            
            <form method="POST"  action="../View/<%=owner%>">
                <ul>
                    <li>Profile: <%=owner%></li>
                    <li>First Name: <input type="text" name="firstname" value="<%=firstname%>"></li>
                    <li>Last Name : <input type="text" name="lastname" value="<%=lastname%>"></li>
                    <li>Emails:  <input type="text" name="email" value="<%=mails%>"></li>
                    <input type="hidden" name="owner" value="<%=owner%>">
                </ul>
                <br/>
                <input type="submit" value="Update"> 
            </form>
        </header>
    </body>
</html>
