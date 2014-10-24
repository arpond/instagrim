<%-- 
    Document   : latestPics
    Created on : 24-Oct-2014, 19:33:20
    Author     : Andrew
--%>

<%@page import="java.util.HashSet"%>
<%@page import="java.util.Iterator"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.Pic"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
        <div id="content">
            <%
            java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
            
            if (lsPics == null) {
        %>
        <p>No Pictures found</p>
        <%
            } else {
                Iterator<Pic> iterator;
                iterator = lsPics.iterator();
                while (iterator.hasNext()) {
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
                <p>Owner: <%=p.getOwner()%></p>
                <p><h3 class="<%=p.getSUUID()%>">Tags: <%=tags%></h3>
                </p>
            </div>
                <%} 
            }%>

        </div>
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
                <li>&COPY; Andy C</li>
            </ul>
        </footer>
    </body>
</html>
