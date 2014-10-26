<%-- 
    Document   : login.jsp
    Created on : Sep 28, 2014, 12:04:14 PM
    Author     : Administrator
--%>

<%@page import="uk.ac.dundee.computing.arp.instagrim.stores.LoggedIn"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim-arp/Styles.css" />

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
                    <li><a href="/Instagrim-arp/Images/">Latest Images</a></li>
                    <li><a href="/Instagrim-arp/upload.jsp">Upload</a></li>
                    <li><a href="/Instagrim-arp/Images/<%=lg.getUsername()%>">Your Images</a></li>
                    <li><a href="/Instagrim-arp/Profile/View/<%=lg.getUsername()%>">View Profile</a></li>
                    <li><a href="/Instagrim-arp/Profile/Edit/<%=lg.getUsername()%>">Edit Profile</a></li>
                    <li><a href="/Instagrim-arp/Logout">Logout</a></li>
                    <%  }
                    }else{
                    %>
                    <li><a href="/Instagrim-arp/Images/">Latest Images</a></li>
                    <li><a href="/Instagrim-arp/register.jsp">Register</a></li>
                    <li><a class="active" href="/Instagrim-arp/login.jsp">Login</a></li>
                    <%
                    }%>
                </ul>
            </nav>        
        </header>
       
        <article>
            <h3>Login</h3>
            <form method="POST"  action="Login" id="login">
                <div class="left">
                    <ul>
                        <li><label class="input">User Name</label> <input type="text" name="username"></li>
                        <li><label class="input">Password</label> <input type="password" name="password"></li>
                    </ul>
                </div>
                <div class="right"><input type="submit" value="Login" class="button" id="loginButton"></div>
            </form>

        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim-arp">Home</a></li>
            </ul>
        </footer>
    </body>
</html>
