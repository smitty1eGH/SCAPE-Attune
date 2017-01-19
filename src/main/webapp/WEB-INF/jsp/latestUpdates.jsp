<%--
  Created by IntelliJ IDEA.
  User: aaronburrell
  Date: 6/13/16
  Time: 7:58 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="css/jquery.dataTables.min.css">
    <script src="js/jquery-2.1.3.js"></script>
    <script src="js/jquery.dataTables.min.js"></script>
    <script src="js/auditView.js"></script>
    <title>Audit View</title>
</head>
<body>
<div class="container">
    <h1 align="center">Audit Table</h1>
    <p></p>
    <table id="auditTable" class="table table-striped table-bordered" cellspacing="0" width="100%">
        <thead>
        <tr>
            <th>Entity Type</th>
            <th>Entity Name</th>
            <th>Date Time</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        </tbody>
    </table>
</div>
</body>
</html>
