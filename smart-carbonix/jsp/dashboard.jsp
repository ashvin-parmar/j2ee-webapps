<%@ page import="java.util.List" %>
<%@ page import="model.UserStats" %>
<%@ page import="model.Badge" %>

<!DOCTYPE html>
<html>
<head>
    <title>Sustainability Dashboard</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="bg-light">
<div class="container my-4">
    <h2 class="text-center text-success">🌱 Sustainability Dashboard</h2>

    <!-- User Points & Level -->
    <div class="card my-3">
        <div class="card-body text-center">
            <h4>Points: <%= request.getAttribute("points") %></h4>
            <h5>Level: <%= request.getAttribute("level") %></h5>
        </div>
    </div>
</body>
</html>
