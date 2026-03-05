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
    
    <%-- Help Modal for First Login --%>
    <% String showHelp = request.getParameter("showHelp");
       if ("true".equals(showHelp)) { %>
    <div class="modal fade" id="helpModal" tabindex="-1" aria-labelledby="helpModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title" id="helpModalLabel">
                        <i class="fas fa-info-circle me-2"></i>Welcome to Ocean View Resort Admin Panel
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="alert alert-info">
                        <h6><i class="fas fa-lightbulb me-2"></i>Quick Start Guide:</h6>
                        <ul class="mb-0">
                            <li><strong>Add Employees:</strong> Navigate to "Add Employee" to create new staff accounts</li>
                            <li><strong>Monitor Dashboard:</strong> View real-time statistics, revenue charts, and room occupancy</li>
                            <li><strong>Manage Staff:</strong> View, edit, or deactivate employee accounts in "View Employee"</li>
                            <li><strong>Track Performance:</strong> Each employee's reservations and payments are tracked separately</li>
                        </ul>
                    </div>
                    <p class="text-muted">For detailed documentation, click the "Help" button in the sidebar anytime.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var helpModal = new bootstrap.Modal(document.getElementById('helpModal'));
            helpModal.show();
            
            // Clean up URL without reload
            window.history.replaceState({}, document.title, window.location.pathname);
        });
    </script>
    <% } %>
</body>
</html>