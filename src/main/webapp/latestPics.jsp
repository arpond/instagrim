<%-- 
    Document   : latestPics
    Created on : 24-Oct-2014, 19:33:20
    Author     : Andrew
--%>

<%@page import="java.util.HashSet"%>
<%@page import="java.util.Iterator"%>
<%@page import="uk.ac.dundee.computing.arp.instagrim.stores.Pic"%>
<%@page import="uk.ac.dundee.computing.arp.instagrim.stores.LoggedIn"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim-arp/Styles.css" />
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
                    <li><a class="active" href="/Instagrim-arp/Images/">Latest Images</a></li>
                    <li><a href="/Instagrim-arp/upload.jsp">Upload</a></li>
                    <li><a href="/Instagrim-arp/Images/<%=lg.getUsername()%>">Your Images</a></li>
                    <li><a href="/Instagrim-arp/Profile/View/<%=lg.getUsername()%>">View Profile</a></li>
                    <li><a href="/Instagrim-arp/Profile/Edit/<%=lg.getUsername()%>">Edit Profile</a></li>
                    <li><a href="/Instagrim-arp/Logout">Logout</a></li>
                    <%  }
                    }else{
                    %>
                    <li><a class="active" href="/Instagrim-arp/Images/">Latest Images</a></li>
                    <li><a href="/Instagrim-arp/register.jsp">Register</a></li>
                    <li><a href="/Instagrim-arp/login.jsp">Login</a></li>
                    <%
                    }%>
                </ul>
            </nav>        
        </header>
        <article>
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
                            String next = (String) it.next();
                            tags += "<a href=\"\\Instagrim-arp\\Tag\\" + next + "\">" + next + "</a>";
                            if (it.hasNext())
                            {
                                tags += ",";
                            }
                        }
                    }

            %>
            <div id="pic<%=p.getSUUID()%>" class="pic">
                <div class="thumb">
                    <span class="shadow">
                        <a href="/Instagrim-arp/Image/<%=p.getSUUID()%>" ><img src="/Instagrim-arp/Thumb/<%=p.getSUUID()%>"></a>
                    </span>
                </div>
                <div class="details">
                    <div class="tags"><span class="<%=p.getSUUID()%>">Tags: <%=tags%></span><span class="right"></span></div>
                    <div class="comment"><a href="/Instagrim-arp/Image/Comments/<%=p.getSUUID()%>">Comments</a></div>
                    <div class="owner">Owner: <a href="/Instagrim-arp/Profile/View/<%=p.getOwner()%>" ><%=p.getOwner()%></a></div>
                </div>
            </div>
                <%} 
            }%>

        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
                <li>&COPY; Andy C</li>
            </ul>
        </footer>
    </body>
</html>
