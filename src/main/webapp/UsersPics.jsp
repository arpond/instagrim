<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.arp.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim-arp/Styles.css" />
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
                    <li><a href="/Instagrim-arp/Images/">Latest Images</a></li>
                    <li><a href="/Instagrim-arp/upload.jsp">Upload</a></li>
                    <li><a href="/Instagrim-arp/Images/<%=lg.getUsername()%>" class="active">Your Images</a></li>
                    <li><a href="/Instagrim-arp/Profile/View/<%=lg.getUsername()%>">View Profile</a></li>
                    <li><a href="/Instagrim-arp/Profile/Edit/<%=lg.getUsername()%>">Edit Profile</a></li>
                    <li><a href="/Instagrim-arp/Logout">Logout</a></li>
                    <%  }
                    }else{
                    %>
                    <li><a href="/Instagrim-arp/Images/">Latest Images</a></li>
                    <li><a href="/Instagrim-arp/register.jsp">Register</a></li>
                    <li><a href="/Instagrim-arp/login.jsp">Login</a></li>
                    <%
                    }%>
                </ul>
            </nav>        
        </header>
 
        <article>
        <%  
            boolean match = (boolean) request.getAttribute("Match");
            String owner = (String) request.getAttribute("Owner");
            if (match)
            {
        %>
            <h1>Your Pics</h1>
        <%
            }
            else
            {
        %>
        <h1><%=owner%> Pics</h1>
        <%
            }
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
        <%
                if (match)
                {%>
                <div class="tags"><span class="<%=p.getSUUID()%>">Tags: <%=tags%></span><span class="right"></span></div>
                <div class="filters">Additional Filters: <a href="/Instagrim-arp/Image/Sepia/<%=p.getSUUID()%>">Sepia</a>, <a href="/Instagrim-arp/Image/Negative/<%=p.getSUUID()%>">Negative</div>
                <div class="comment"><a href="/Instagrim-arp/Image/Comments/<%=p.getSUUID()%>">Comments</a></div>
                <div class="interface">
                    <button type="button" onclick="deleteImage('<%=p.getSUUID()%>', '<%=owner%>');">Delete</button>
                    <button type="button" onclick="showEditTags('<%=p.getSUUID()%>');" id="<%=p.getSUUID()%>">Edit Tags</button>
                </div>
                <div class="hide" id="tag<%=p.getSUUID()%>">
                    <input type="text" name="tagUpdate" class="<%=p.getSUUID()%>" value="<%=tags%>">
                    <button type="button" onclick="updateTags('<%=p.getSUUID()%>', '<%=owner%>');">Update Tags</button>
                </div>
                <%}
                else
                {%>
                <div class="tags"><span class="<%=p.getSUUID()%>">Tags: <%=tags%></span><span class="right"></span></div>
                <%}%>
            </div>
        </div>
        <%
            }
            }
        %>
        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim-arp">Home</a></li>
            </ul>
        </footer>
    </body>
</html>
