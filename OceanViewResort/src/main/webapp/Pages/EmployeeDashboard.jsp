<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Add JSTL Core taglib for future logic (requires the dependency in your pom.xml) --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ocean View Resort - Employee Dashboard</title>
    
    <%-- Prevents Sidebar Flicker on Page Load --%>
    <script>
        if (localStorage.getItem('sidebarState') === 'collapsed') {
            document.documentElement.classList.add('sidebar-is-collapsed');
        }
    </script>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>
    
  <link rel="stylesheet" href="../StyleSheets/Css/SideBar.css">
    <link rel="stylesheet" href="../StyleSheets/Css/EmployeeDashboard.css">
</head>

<body class="emp-dash-body">
    <%-- Sidebar Component --%>
 <%@ include file="../Components/SideBar.jsp" %>

    <div class="content-wrapper">
        <div class="container-fluid p-4">
            
            <%-- Dashboard Header --%>
            <div class="row mb-4 animate__animated animate__fadeInDown">
                <div class="col-12">
                    <h1 class="text-white fw-bold">Employee Dashboard</h1>
                    <p class="text-white-50">Room Availability status.</p>
                </div>
            </div>

            <%-- Room Progress Bars Content --%>
            <div class="row mb-4">
                <div class="col-12">
                     <%@ include file="../Components/EmployeeDashboardContent.jsp" %>
                </div>
            </div>

            <%-- Payment Statistics Section --%>
            <div class="row mb-4">
                <div class="col-12">
                    <h2 class="text-white fw-bold">Payment Statistics</h2>
                    <hr class="text-white-50">
                    <%@ include file="../Components/EmployeeDashStat.jsp" %>
                </div>
            </div>

        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
   <script src="../StyleSheets/Js/Sidebar.js"></script>
    <script src="../StyleSheets/Js/EmployeeDashboard.js"></script>
</body>
</html>