<%-- 
    Document   : register.jsp
    Created on : Sep 28, 2014, 6:29:51 PM
    Author     : Administrator

    TODO - Add space for errors for redirect to here from failed registration
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
                    <li><a class="active" href="/Instagrim-arp/register.jsp">Register</a></li>
                    <li><a href="/Instagrim-arp/login.jsp">Login</a></li>
                    <%
                    }%>
                </ul>
            </nav>        
        </header>
        <article>
            <h3>Register as user</h3>
            <form method="POST"  action="Register" id="register">
                <ul>
                    <li><label class="input">User Name</label> <input type="text" name="username"></li>
                    <li><label class="input">Password</label> <input type="password" name="password"></li>
                    <li><label class="input">Confirm Password</label> <input type="password" name="confirmpass"></li>
                    <li><label class="input">Email</label>  <input type="text" name="email"></li>
                </ul>
                <input type="submit" value="Register" class="button" id="registerButton"> 
            </form>

        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim-arp">Home</a></li>
            </ul>
        </footer>
    </body>
</html>
