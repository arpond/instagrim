<%-- 
    Document   : error
    Created on : 08-Oct-2014, 09:02:59
    Author     : Andrew
--%>

<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Contenxt-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
        <title>Instagrim</title>
    </head>
    <body>
        <header>
            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>
        </header>
        <nav>
            <ul id="mainmenu">
                    <%

                LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                if (lg != null) {
                    String UserName = lg.getUsername();
                    if (lg.getlogedin()) {
                    %>
                <li><a href="/Instagrim/Images/">Latest Images</a></li>
                <li><a href="upload.jsp">Upload</a></li>
                <li><a href="/Instagrim/Images/<%=lg.getUsername()%>">Your Images</a></li>
                <li><a href="/Instagrim/Profile/View/<%=lg.getUsername()%>">View Profile</a></li>
                <li><a href="/Instagrim/Profile/Edit/<%=lg.getUsername()%>">Edit Profile</a></li>
                <li><a href="/Instagrim/Logout">Logout</a></li>
                <%  }
                }else{
                %>
                <li><a href="/Instagrim/Images/">Latest Images</a></li>
                <li><a href="register.jsp">Register</a></li>
                <li><a href="login.jsp">Login</a></li>
                <%
                }%>
            </ul>
        </nav>
        <p>
            <%
              String error = (String) request.getAttribute("error");
              out.print(error);
            %>
        </p>
    </body>
</html>
