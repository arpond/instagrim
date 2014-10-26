<%-- 
    Document   : profile
    Created on : 11-Oct-2014, 16:52:10
    Author     : Andrew
--%>



<%@page import="uk.ac.dundee.computing.arp.instagrim.stores.LoggedIn"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Set" %>
<%@page import="java.util.Date" %>
<%@page import="java.util.Iterator" %>
<%@page import="uk.ac.dundee.computing.arp.instagrim.stores.UserDetails" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/Scripts/requests.js" type="text/javascript"></script>
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
                    <li><a href="/Instagrim/Images/">Latest Images</a></li>
                    <li><a href="/Instagrim/upload.jsp">Upload</a></li>
                    <li><a href="/Instagrim/Images/<%=lg.getUsername()%>">Your Images</a></li>
                    <li><a href="/Instagrim/Profile/View/<%=lg.getUsername()%>">View Profile</a></li>
                    <li><a class ="active" href="/Instagrim/Profile/Edit/<%=lg.getUsername()%>">Edit Profile</a></li>
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
            UserDetails userDetails = (UserDetails) request.getAttribute("details");
            String owner = userDetails.getUsername();
            String firstname = userDetails.getFirstname();
            String lastname = userDetails.getLastname();
            String street = userDetails.getStreet();
            String city = userDetails.getCity();
            String zipcode;
            int zip = userDetails.getZip();
            Set<String> emails = userDetails.getEmails();
            
            String mails = "";
            
            if (firstname == null)
            {
                firstname =  "";
            }
            
            if (lastname == null)
            {
                lastname = "";
            }
            
            if (street.equals(""))
            {
                city = "";
                zipcode = "";
            }
            else
            {
                zipcode = Integer.toString(zip);
            }
            
            
            Iterator<String> iterator;
            iterator = emails.iterator();
            while (iterator.hasNext()) 
            {
                mails += (String) iterator.next() + ";";
            }
            
            //Date joined = (Date) request.getAttribute("joined");
            %>
        <article>
            <h2>Profile: <%=owner%></h2>
            <div id="profile">
                <div id="updateAvatar">
                    <h3>Upload Avatar</h3>
                    <form method="POST" enctype="multipart/form-data" action="/Instagrim/Profile/View/<%=owner%>" id="upload">
                        <ul>
                            <li><label>Upload Profile Picture:</label><input type="file" name="upfile"></li>
                            <li><input type="submit" value="upload" name="action" id="uploadButton" class="button"> to upload the file!</li>
                        </ul>
                        <input type="hidden" name="owner" value="<%=owner%>">
                    </form>
                </div>
                <div id="updateProfile">
                    <h3>Update Profile</h3>
                    <form id="updateForm" method="POST"  action="/Instagrim/Profile/View/<%=owner%>" >
                        <ul>
                            <li><label class="input">First Name </label><input type="text" name="firstname" value="<%=firstname%>"></li>
                            <li><label class="input">Last Name </label><input type="text" name="lastname" value="<%=lastname%>"></li>
                            <li><label class="input">Street </label><input type="text" name="street" value="<%=street%>"></li>
                            <li><label class="input">City </label><input type="text" name="city" value="<%=city%>"></li>
                            <li><label class="input">Zip </label><input type="text" name="zip" value="<%=zipcode%>"></li>
                            <li><label class="input">Emails  </label><input type="text" name="email" value="<%=mails%>"></li>
                            <input type="hidden" name="owner" value="<%=owner%>" id="updateButton">
                        </ul>
                        <input type="submit" value="update" name="action" class="button"> 
                    </form>
                </div>
            </div>
        </article>
    </body>
</html>
