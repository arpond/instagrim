<%-- 
    Document   : tagPics
    Created on : 24-Oct-2014, 09:34:40
    Author     : Andrew
--%>

<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Iterator"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.Pic"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/Scripts/requests.js" type="text/javascript"></script>
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
        <%
            java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
            
            if (lsPics == null || lsPics.isEmpty()) {
        %>
        <p>No Pictures found</p>
        <%
        } 
        else 
        {
            Iterator<Pic> iterator;
            iterator = lsPics.iterator();
            while (iterator.hasNext()) 
            {
                Pic p = (Pic) iterator.next();
                HashSet<String> tagSet = p.getTags();
                String tags ="";
                if (tagSet.isEmpty())
                {
                    tags = "No Tags";
                }
                else
                {
                    Iterator<String> it;
                    it = tagSet.iterator();
                    while (it.hasNext())
                    {
                        tags += (String) it.next();
                        if (it.hasNext())
                        {
                            tags += ",";
                        }
                    }
                }

        %>
        <div class="<%=p.getSUUID()%>">
            <a href="/Instagrim/Image/<%=p.getSUUID()%>" ><img src="/Instagrim/Thumb/<%=p.getSUUID()%>"></a><br/>
            <p><h3 class="<%=p.getSUUID()%>">Tags: <%=tags%></h3>
            </p>
        </div>
        <%
            }
        }
            %>

    </body>
</html>
