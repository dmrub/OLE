<%--
    Document   : index
    Created on : Jan 3, 2018, 10:17:11 PM
    Author     : rubinste
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>OLE Service</title>
    <style>
        table {
            font-family: arial, sans-serif;
            border-collapse: collapse;
            width: 100%;
        }

        td, th {
            border: 1px solid #dddddd;
            text-align: left;
            padding: 8px;
        }

    </style>
</head>
<body>
<h2>OLE Service</h2>
<p><b>Service Base URI:</b><pre>XXX</pre></p>
<table>
    <caption><h3>Available REST API</h3></caption>
    <tr>
        <th>Path</th>
        <th>Description</th>
    </tr><tr><td>/brick/<b><i>brickID</i></b></td><td>Get brick information</td></tr>
    <tr><td>/minifig/<b><i>minifigID</i></b></td><td>Get minifig information</td></tr>
</table>
</body>
</html>
