<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Portal - Ocean View</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>
    <link rel="stylesheet" href="../StyleSheets/Css/AdminLogin.css">
</head>
<body class="al-auth-body">

    <div class="al-glass-card animate__animated animate__fadeInDown">
        <div class="al-tabs">
            <button class="al-tab-btn active" onclick="switchTab('login', event)">LOGIN</button>
            <button class="al-tab-btn" onclick="switchTab('register', event)">REGISTER</button>
        </div>

        <div class="text-center mb-4">
            <h2 class="text-white fw-bold al-main-title">Admin Management</h2>
            <p class="text-white-50 small">Secure Access for Ocean View Administrators</p>
        </div>

        <div id="loginForm" class="al-form-container active">
            <%@ include file="../Components/AdminLoginForm.jsp" %>
        </div>

        <div id="registerForm" class="al-form-container">
            <%@ include file="../Components/AdminRegisterForm.jsp" %>
        </div>

        <div class="mt-4 text-center animate__animated animate__fadeIn">
            <a href="MainPage.jsp" class="text-white-50 text-decoration-none small">
                <i class="fa-solid fa-arrow-left me-1"></i> Back to Portal
            </a>
        </div>
    </div>

<div class="modal fade" id="statusModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content bg-dark text-white border-0 shadow-lg" style="border-radius: 20px; backdrop-filter: blur(10px); background: rgba(0,0,0,0.85);">
                <div class="modal-body text-center p-5">
                    <i id="modalIcon"></i>
                    <h3 id="modalTitle" class="fw-bold mt-2"></h3>
                    <p id="modalMessage" class="text-white-50 mb-4"></p>
                    <button type="button" class="btn btn-outline-light rounded-pill px-5" data-bs-dismiss="modal">OK</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="../StyleSheets/Js/AdminLogin.js"></script>
</body>
</html>