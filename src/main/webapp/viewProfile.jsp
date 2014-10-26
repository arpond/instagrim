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
                    <li><a class="active" href="/Instagrim/Profile/View/<%=lg.getUsername()%>">View Profile</a></li>
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
            UserDetails userDetails = (UserDetails) request.getAttribute("details");
            String owner = userDetails.getUsername();
            String firstname = userDetails.getFirstname();
            String lastname = userDetails.getLastname();
            String street = userDetails.getStreet();
            String city = userDetails.getCity();
            String zipcode;
            int zip = userDetails.getZip();
            Set<String> emails = userDetails.getEmails();
            Date joined = userDetails.getJoined();
            String fullname = "";
                     
            if (firstname != null && lastname != null)
            {
                fullname = firstname + " " + lastname;
            }
            else if (firstname != null)
            {
                fullname = firstname;
            }
            
            if (street.equals(""))
            {
                street = "-";
                city = "-";
                zipcode = "-";
            }
            else
            {
                zipcode = Integer.toString(zip);
            }
            %>
            
            <article>
                <h2>Profile: <%=owner%></h2>
                <div id="avatar"><img src="/Instagrim/Profile/ProfilePic/<%=owner%>" width="128" height="128"></div>
                <div id="userDetails">
                    <ul>
                        <li>Name: <%=fullname%></li>
                        <li>Email: 
                        <%
                        Iterator<String> iterator;
                        iterator = emails.iterator();
                        while (iterator.hasNext()) 
                        {
                            String email = (String) iterator.next();
                        %>
                            <%=email%> <br />
                        <%
                        }
                        %>
                        </li>
                        <li>Address
                            <ul>
                                <li>Street: <%=street%></li>
                                <li>City: <%=city%></li>
                                <li>Zip: <%=zipcode%></li>
                            </ul>
                        </li>
                        <li>Joined: <%=joined.toString()%></li>
                    </ul>
                </div>
                <br/>
            <%
            if (lg != null)
            {
                String user = lg.getUsername();
                if (!user.equals(owner))
                {%>
                <a href="/Instagrim/Images/<%=owner%>">View <%=owner%>'s gallery</a>
              <%}
            }
            else
            {%>
                <a href="/Instagrim/Images/<%=owner%>">View <%=owner%>'s gallery</a>
          <%}
            %>
            </article>
        </header>
    </body>
</html>
