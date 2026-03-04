<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.Cookie" %>
<%
    // Check if admin is logged in via cookie
    boolean isAdminLoggedIn = false;
    String adminUsername = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("adminUser".equals(cookie.getName())) {
                isAdminLoggedIn = true;
                adminUsername = cookie.getValue();
                break;
            }
        }
    }
    if (!isAdminLoggedIn) {
        response.sendRedirect("AdminLogin.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Ocean View Resort</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/AdminSidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/AdminDashboard.css">
</head>

<body>
    <%@ include file="../Components/AdminSidebar.jsp" %>

    <div class="admin-content-wrapper">
        <main>
            <div class="container-fluid px-4 py-3">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <div>
                        <h1 class="mt-2 text-white fw-bold">Admin Dashboard</h1>
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item active text-white-50">Overview</li>
                            </ol>
                        </nav>
                    </div>
                </div>
                
                <%@ include file="../Components/AdminDashboardContent.jsp" %>
            </div>
        </main>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/StyleSheets/Js/AdminSidebar.js"></script>
</body>
</html>