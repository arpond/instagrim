<%-- 
    Document   : profile
    Created on : 11-Oct-2014, 16:52:10
    Author     : Andrew
--%>



<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Set" %>
<%@page import="java.util.Date" %>
<%@page import="java.util.Iterator" %>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.UserDetails" %>
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
            
            <form method="POST" enctype="multipart/form-data" action="/Instagrim/Profile/View/<%=owner%>">
                Upload Profile Picture: <input type="file" name="upfile"><br/><br/>
                <input type="submit" value="upload" name="action"> to upload the file!
                <input type="hidden" name="owner" value="<%=owner%>">
            </form>
            
            <form id="updateForm" method="POST"  action="/Instagrim/Profile/View/<%=owner%>" >
                <ul>
                    <li>Profile: <%=owner%></li>
                    <li>First Name: <input type="text" name="firstname" value="<%=firstname%>"></li>
                    <li>Last Name : <input type="text" name="lastname" value="<%=lastname%>"></li>
                    <li>Street: <input type="text" name="street" value="<%=street%>"></li>
                    <li>City: <input type="text" name="city" value="<%=city%>"></li>
                    <li>Zip: <input type="text" name="zip" value="<%=zipcode%>"></li>
                    <li>Emails:  <input type="text" name="email" value="<%=mails%>"></li>
                    <input type="hidden" name="owner" value="<%=owner%>">
                </ul>
                <br/>
                <input type="submit" value="update" name="action"> 
            </form>
                <h2 onclick="updateProfile('<%=owner%>')">Click Me</h2>
        </header>
    </body>
</html>
