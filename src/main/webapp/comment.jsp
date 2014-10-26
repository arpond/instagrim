<%-- 
    Document   : comment
    Created on : 17-Oct-2014, 08:41:08
    Author     : Andrew
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.arp.instagrim.stores.*" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
        <title>Instagrim</title>
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
        <article>
            <%
                String picID = (String) request.getAttribute("picture");
                ArrayList<Comment> comments = (ArrayList<Comment>) request.getAttribute("comments");
            %>
            <p>
                <img style="-webkit-user-select: none; cursor: zoom-in; max-width: 800px" src="/Instagrim/Image/<%=picID%>">
            </p>
            <%
                if (comments == null)
                {
            %>
            <h3>No comments</h3>
            <%
                }
                else
                {
                    for (Comment comment : comments)
                    {
            %>
            <h3><%=comment.getAuthor()%> wrote: </h3>
            <p>
                <%=comment.getComment()%>
            </p>
            <h6>On: <%=comment.getWrittenOn().toString()%></h6>
            <%
                    }
                }
            %>

            <form method="POST"  action="/Instagrim/AddComment">
                <textarea name="comment" cols="50" rows="5">
                Enter your comment
                </textarea>
                <br/>
                <input type="submit" value="AddComment"> 
                <input type="hidden" name="picID" value="<%=picID%>">
            </form>
        </article>
    </body>
</html>
