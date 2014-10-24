<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
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
        <header>
        
        <h1>InstaGrim ! </h1>
        <h2>Your world in Black and White</h2>
        </header>
        
        <nav>
            <ul>
                <li class="nav"><a href="/Instagrim/upload.jsp">Upload</a></li>
                <li class="nav"><a href="/Instagrim/Images/majed">Sample Images</a></li>
            </ul>
        </nav>
 
        <article>
            <h1>Your Pics</h1>
        <%
            java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
            String owner = (String) request.getAttribute("Owner");
            boolean match = (boolean) request.getAttribute("Match");
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
        <%
                if (match)
                {%>
            <button type="button" onclick="deleteImage('<%=p.getSUUID()%>', '<%=owner%>');">Delete</button>
                <%}%>
                <p><h3 class="<%=p.getSUUID()%>">Tags: <%=tags%></h3><button type="button" onclick="showEditTags('<%=p.getSUUID()%>');" id="<%=p.getSUUID()%>">Edit Tags</button>
            </p>
            <div class="hide" id="tag<%=p.getSUUID()%>">
                <input type="text" name="tagUpdate" class="<%=p.getSUUID()%>" value="<%=tags%>">
                <button type="button" onclick="updateTags('<%=p.getSUUID()%>', '<%=owner%>');">Update Tags</button>
            </div>
        </div>
        <%
            }
            }
        %>
        </article>
<!--        <div id="confirmDelete" title="Delete this image?">
            
            <p>This will delete the image permanently, are you sure?</p>
        </div>
        <div id="successfulDelete" title="Image Successfully Deleted">
            <p>The image was successfully deleted</p>
        </div>
        <div id="errorDelete" title="Error">
            <p>There was an error deleting the image</p>
        </div>    -->
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
            </ul>
        </footer>
    </body>
</html>
