<%-- 
    Document   : error
    Created on : 08-Oct-2014, 09:02:59
    Author     : Andrew
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="Styles.css" />
        <title>Instagrim</title>
    </head>
    <body>
        <header>
            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>
        </header>
        <p>
            <%
              String error = (String) request.getAttribute("error");
              out.print(error);
            %>
        </p>
    </body>
</html>
