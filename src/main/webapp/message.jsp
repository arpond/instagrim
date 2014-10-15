<%-- 
    Document   : message
    Created on : 09-Oct-2014, 11:30:07
    Author     : Andrew
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="refresh" content="5; url=http://localhost:8080/Instagrim/" />
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
        <title>Instagrim</title>
    </head>
    <body>
        <header>
            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>
        </header>
        <p>
            <%
              String message = (String) request.getAttribute("message");
              out.print(message);
            %>
        </p>
        <p>You will be redirected to the index in 5 seconds... or return to the <a href="/Instagrim/">index</a></p>
    </body>
</html>
