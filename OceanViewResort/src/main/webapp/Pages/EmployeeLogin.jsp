<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Employee Login - Ocean View Resort</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/MainSidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/EmployeeLogin.css">
</head>

<body class="el-login-body">
    <!-- Toast Container for Login Messages -->
    <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 2000;">
        <!-- Error Toast -->
        <div id="loginErrorToast" class="toast align-items-center text-white bg-danger border-0 shadow-lg" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body d-flex align-items-center py-3">
                    <i class="fa-solid fa-circle-exclamation me-2 fs-5"></i>
                    <span id="loginErrorMessage">Invalid username or password. Please try again.</span>
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
        
        <!-- Success Toast -->
        <div id="loginSuccessToast" class="toast align-items-center text-white bg-success border-0 shadow-lg" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body d-flex align-items-center py-3">
                    <i class="fa-solid fa-check-circle me-2 fs-5"></i>
                    <span>Welcome back! Login successful.</span>
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    </div>

    <div class="container d-flex justify-content-center align-items-center">
        <div class="el-glass-card animate__animated animate__zoomIn">
            <div class="mb-4">
                <i class="fa-solid fa-anchor fs-1 text-white mb-2"></i>
                <h2 class="text-white fw-bold">Employee Access</h2>
                <p class="text-white-50 small">Enter your credentials to manage resort operations</p>
            </div>

            <%@ include file="../Components/EmployeeLoginForm.jsp" %>

            <div class="mt-4 animate__animated animate__fadeIn">
                <a href="MainPage.jsp" class="text-white-50 text-decoration-none small">
                    <i class="fa-solid fa-arrow-left me-1"></i> Back to Portal
                </a>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/StyleSheets/Js/MainSideBar.js"></script>
    <script>
        // Show toast notifications based on URL parameters
        document.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            const errorParam = urlParams.get('error');
            const successParam = urlParams.get('success');
            
            // Show error toast if login failed
            if (errorParam === 'login_fail') {
                const loginErrorToast = new bootstrap.Toast(document.getElementById('loginErrorToast'));
                loginErrorToast.show();
            }
            
            // Show success toast if login succeeded (optional - for better UX)
            if (successParam === 'login') {
                const loginSuccessToast = new bootstrap.Toast(document.getElementById('loginSuccessToast'));
                loginSuccessToast.show();
            }
        });
        
        // Employee Login Form Submission Animation
        document.addEventListener('DOMContentLoaded', function() {
            const employeeLoginForm = document.querySelector('form[action*="LoginServlet"]');
            
            if (employeeLoginForm) {
                employeeLoginForm.addEventListener('submit', function(e) {
                    e.preventDefault();
                    
                    const btn = document.getElementById('employeeLoginBtn');
                    const text = document.getElementById('employeeLoginText');
                    const spinner = document.getElementById('employeeLoginSpinner');

                    // Trigger Loading State
                    if (btn) btn.disabled = true;
                    if (text) text.textContent = "Processing...";
                    if (spinner) spinner.classList.remove('d-none');

                    // Natural delay for visual feedback before submission
                    setTimeout(() => { employeeLoginForm.submit(); }, 1000);
                });
            }
        });
    </script>
</body>
</html>