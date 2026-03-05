<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password - Ocean View Resort</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/EmployeeLogin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/ForgotPassword.css">
</head>
<body class="forgot-password-body">
    <a href="EmployeeLogin.jsp" class="btn btn-outline-light back-btn">
        <i class="fas fa-arrow-left me-2"></i>Back to Login
    </a>

    <div class="forgot-card">
        <div class="forgot-header animate__animated animate__fadeInDown">
            <i class="fas fa-lock-reset"></i>
            <h2>Reset Your Password</h2>
            <p>Follow the steps to recover your account</p>
        </div>

        <!-- Step Indicator -->
        <div class="step-indicator">
            <div class="step active" id="step1Indicator">1</div>
            <div class="step" id="step2Indicator">2</div>
            <div class="step" id="step3Indicator">3</div>
        </div>

        <!-- Toast Container -->
        <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 2000;">
            <div id="forgotPasswordToast" class="toast align-items-center text-white border-0 shadow-lg" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="d-flex">
                    <div class="toast-body d-flex align-items-center py-3">
                        <i class="fa-solid me-2 fs-5" id="toastIcon"></i>
                        <span id="toastMessage"></span>
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            </div>
        </div>

        <!-- Step 1: Enter Email -->
        <div class="form-section active" id="emailSection">
            <form id="emailForm" onsubmit="submitEmail(event)">
                <div class="mb-4">
                    <label class="form-label fw-bold">
                        <i class="fas fa-envelope me-2 text-primary"></i>Enter Your Email
                    </label>
                    <input type="email" class="form-control form-control-lg" id="employeeEmail" 
                           placeholder="your.email@oceanview.com" required>
                    <small class="text-muted">We'll send a verification code to this email</small>
                </div>
                
                <button type="submit" class="btn btn-primary w-100 py-2 fw-bold" id="sendCodeBtn">
                    <span class="btn-text">Send Verification Code</span>
                    <span class="spinner-border spinner-border-sm d-none" role="status"></span>
                </button>
            </form>
        </div>

        <!-- Step 2: Enter OTP Code -->
        <div class="form-section" id="otpSection">
            <div class="alert alert-info">
                <i class="fas fa-info-circle me-2"></i>
                A verification code has been sent to <strong id="displayEmail"></strong>
            </div>
            
            <form id="otpForm" onsubmit="verifyOTP(event)">
                <div class="mb-4">
                    <label class="form-label fw-bold">
                        <i class="fas fa-shield-alt me-2 text-success"></i>Enter Verification Code
                    </label>
                    <input type="text" class="form-control form-control-lg text-center" id="otpCode" 
                           placeholder="XXXXXX" maxlength="6" required>
                    <small class="text-muted">Check your email for the 6-digit code</small>
                </div>
                
                <button type="submit" class="btn btn-success w-100 py-2 fw-bold" id="verifyOtpBtn">
                    <span class="btn-text">Verify Code</span>
                    <span class="spinner-border spinner-border-sm d-none" role="status"></span>
                </button>
            </form>
        </div>

        <!-- Step 3: New Password -->
        <div class="form-section" id="passwordSection">
            <div class="alert alert-success">
                <i class="fas fa-check-circle me-2"></i>
                Code verified! Now set your new password.
            </div>
            
            <form id="passwordForm" onsubmit="resetPassword(event)">
                <div class="mb-3">
                    <label class="form-label fw-bold">
                        <i class="fas fa-lock me-2 text-primary"></i>New Password
                    </label>
                    <input type="password" class="form-control form-control-lg" id="newPassword" 
                           placeholder="Enter new password" required minlength="6">
                </div>
                
                <div class="mb-4">
                    <label class="form-label fw-bold">
                        <i class="fas fa-lock me-2 text-primary"></i>Confirm Password
                    </label>
                    <input type="password" class="form-control form-control-lg" id="confirmPassword" 
                           placeholder="Confirm new password" required minlength="6">
                    <small class="text-danger d-none" id="passwordMatchError">Passwords do not match!</small>
                </div>
                
                <button type="submit" class="btn btn-primary w-100 py-2 fw-bold" id="resetPasswordBtn">
                    <span class="btn-text">Reset Password</span>
                    <span class="spinner-border spinner-border-sm d-none" role="status"></span>
                </button>
            </form>
        </div>

        <!-- Success Message -->
        <div class="form-section text-center" id="successSection">
            <div class="mb-4">
                <i class="fas fa-check-circle text-success" style="font-size: 4rem;"></i>
            </div>
            <h3 class="text-success fw-bold mb-3">Password Reset Successful!</h3>
            <p class="text-muted mb-4">Your password has been updated successfully.</p>
            <a href="EmployeeLogin.jsp" class="btn btn-primary px-4 py-2 fw-bold">
                <i class="fas fa-sign-in-alt me-2"></i>Login Now
            </a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/StyleSheets/Js/ForgotPassword.js"></script>
</body>
</html>
