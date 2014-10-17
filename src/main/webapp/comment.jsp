<%-- 
    Document   : comment
    Created on : 17-Oct-2014, 08:41:08
    Author     : Andrew
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
        <title>Instagrim</title>
    </head>
    <body>
        <header>
            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>
        </header>
        <%
            String picID = (String) request.getAttribute("picture");
            ArrayList<Comment> comments = (ArrayList<Comment>) request.getAttribute("comments");
        %>
        <p>
            <img style="-webkit-user-select: none; cursor: zoom-in;" src="/Instagrim/Image/<%=picID%>">
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
        <h3>On: <%=comment.getWrittenOn().toString()%></h3>
        <%
                }
            }
        %>
        
        <form method="POST"  action="Comments">
            <textarea name="comment" cols="50" rows="5">
            Enter your comment
            </textarea>
            <br/>
            <input type="submit" value="Comment"> 
        </form>
    </body>
</html>
