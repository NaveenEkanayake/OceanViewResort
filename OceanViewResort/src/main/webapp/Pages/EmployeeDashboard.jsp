<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.Cookie" %>
<%
    // Check if employee is logged in via cookie
    boolean isEmployeeLoggedIn = false;
    String employeeUsername = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("employeeUser".equals(cookie.getName())) {
                isEmployeeLoggedIn = true;
                employeeUsername = cookie.getValue();
                break;
            }
        }
    }
    
    // Redirect to MainPage if not authenticated
    if (!isEmployeeLoggedIn) {
        response.sendRedirect("MainPage.jsp");
        return;
    }
%>
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
    <%-- Toast Notification Container --%>
    <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 2000;">
        <div id="loginSuccessToast" class="toast align-items-center text-white bg-success border-0 shadow-lg" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body d-flex align-items-center py-3">
                    <i class="fa-solid fa-check-circle me-2 fs-5"></i>
                    <span id="toastMessage">Login successful! Welcome!</span>
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    </div>

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
    
    <script>
        // Show login success toast if redirect from login
        document.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            const successMessage = urlParams.get('success');
            const showHelpParam = urlParams.get('showHelp');
            
            if (successMessage === 'login') {
                const toastElement = document.getElementById('loginSuccessToast');
                const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
                toast.show();
                
                // Clean up URL without reload
                window.history.replaceState({}, document.title, window.location.pathname);
            }
            
            // Show help modal on first login
            if (showHelpParam === 'true') {
                var helpModal = new bootstrap.Modal(document.getElementById('helpModal'));
                setTimeout(function() {
                    helpModal.show();
                }, 1000); // Show after toast
                
                // Clean up URL without reload
                window.history.replaceState({}, document.title, window.location.pathname);
            }
        });
    </script>
    
    <%-- Help Modal for First Login --%>
    <% String showHelp = request.getParameter("showHelp");
       if ("true".equals(showHelp)) { %>
    <div class="modal fade" id="helpModal" tabindex="-1" aria-labelledby="helpModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header bg-success text-white">
                    <h5 class="modal-title" id="helpModalLabel">
                        <i class="fas fa-user-circle me-2"></i>Welcome to Ocean View Resort Employee Portal
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="alert alert-info">
                        <h6><i class="fas fa-lightbulb me-2"></i>Your Daily Workflow:</h6>
                        <ol class="mb-0">
                            <li><strong>Make Reservation:</strong> Create new bookings for guests (Single/Double/Ocean Suite)</li>
                            <li><strong>View Reservations:</strong> See ONLY your own reservations in real-time</li>
                            <li><strong>Update/Delete:</strong> Modify or cancel your reservations as needed</li>
                            <li><strong>Calculate Bill:</strong> Process payments (Cash/Card) with extra charges for amenities</li>
                        </ol>
                    </div>
                    <div class="alert alert-warning mt-3">
                        <h6><i class="fas fa-exclamation-triangle me-2"></i>Important Notes:</h6>
                        <ul class="mb-0">
                            <li>You can only view and manage reservations YOU created</li>
                            <li>Payment statistics show YOUR personal performance only</li>
                            <li>Always send invoice emails after successful payment</li>
                            <li>Extra charges apply for: Gym, Mini-fridge, Spa, Room Service, etc.</li>
                        </ul>
                    </div>
                    <p class="text-muted mt-3">Need more help? Click the "Help" button in the sidebar anytime!</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success" data-bs-dismiss="modal">Got it!</button>
                </div>
            </div>
        </div>
    </div>
    <% } %>
</body>
</html>