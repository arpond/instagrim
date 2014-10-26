<%-- 
    Document   : index
    Created on : Sep 28, 2014, 7:01:44 PM
    Author     : Administrator
--%>


<%--
    TOADD - Logout option, when user is logged in
    TOADD - Edit Profile option when user is logged in
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <header class="center">        
            <div id="headline"><span id="title">InstaGrim ! </span><span id="tagline">Your world in Black and White</span></div>
            <nav>
                <ul id="mainmenu" class="center">
                        <%

                    LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                    if (lg != null) {
                        String UserName = lg.getUsername();
                        if (lg.getlogedin()) {
                        %>
                    <li><a href="/Instagrim/Images/">Latest Images</a></li>
                    <li><a href="/Instagrim/upload.jsp">Upload</a></li>
                    <li><a href="/Instagrim/Images/<%=lg.getUsername()%>">Your Images</a></li>
                    <li><a href="/Instagrim/Profile/View/<%=lg.getUsername()%>">View Profile</a></li>
                    <li><a href="/Instagrim/Profile/Edit/<%=lg.getUsername()%>">Edit Profile</a></li>
                    <li><a href="/Instagrim/Logout">Logout</a></li>
                    <%  }
                    }else{
                    %>
                    <li><a href="/Instagrim/Images/">Latest Images</a></li>
                    <li><a href="/Instagrim/register.jsp">Register</a></li>
                    <li><a href="/Instagrim/login.jsp">Login</a></li>
                    <%
                    }%>
                </ul>
            </nav>        
        </header>
        <footer class="center">
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
                <li>&COPY; Andy C</li>
            </ul>
        </footer>
    </body>
</html>
