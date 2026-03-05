<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Password Recovery - Ocean View Resort</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/AdminForgotPassword_New.css">
</head>
<body class="admin-forgot-new-body">
    <!-- Toast Container -->
    <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 2000;">
        <div id="adminForgotNewToast" class="toast align-items-center text-white border-0 shadow-lg">
            <div class="d-flex">
                <div class="toast-body d-flex align-items-center py-3">
                    <i class="fa-solid me-2 fs-5" id="toastIcon"></i>
                    <span id="toastMessage"></span>
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    </div>

    <!-- Step 1: Enter Email -->
    <div class="form-section active" id="emailSection">
        <div class="forgot-card animate__animated animate__fadeInUp">
            <div class="text-center mb-4">
                <i class="fas fa-user-shield fs-1 admin-icon"></i>
                <h2 class="fw-bold mb-2">Admin Password Recovery</h2>
                <p class="text-muted">Enter your registered email address to reset your password</p>
            </div>
            
            <form id="emailForm" onsubmit="submitEmail(event)">
                <div class="mb-4">
                    <label class="form-label fw-bold">
                        <i class="fas fa-envelope me-2 admin-icon"></i>Admin Email Address
                    </label>
                    <input type="email" class="form-control form-control-lg" id="adminEmail" 
                           placeholder="admin@oceanviewresort.com" required>
                    <small class="text-muted">We'll send a verification code to this email</small>
                </div>
                
                <button type="submit" class="btn btn-admin-new w-100 py-2 fw-bold" id="sendCodeBtn">
                    <span class="btn-text">Send Verification Code</span>
                    <span class="spinner-border spinner-border-sm d-none" role="status"></span>
                </button>
            </form>
            
            <div class="text-center mt-3">
                <a href="AdminLogin.jsp" class="text-admin-new-link">
                    <i class="fas fa-arrow-left me-1"></i> Back to Admin Login
                </a>
            </div>
        </div>
    </div>

    <!-- Step 2: Enter OTP Code -->
    <div class="form-section" id="otpSection">
        <div class="forgot-card animate__animated animate__fadeInUp">
            <div class="text-center mb-4">
                <i class="fas fa-lock-open fs-1 admin-icon"></i>
                <h2 class="fw-bold mb-2">Verification Code</h2>
                <p class="text-muted">Enter the 6-digit code sent to your email</p>
            </div>
            
            <div class="alert alert-info-custom">
                <i class="fas fa-info-circle me-2"></i>
                A verification code has been sent to <strong id="displayEmail"></strong>
            </div>
            
            <form id="otpForm" onsubmit="verifyOTP(event)">
                <div class="mb-4">
                    <label class="form-label fw-bold">
                        <i class="fas fa-shield-halved me-2 admin-icon"></i>Verification Code
                    </label>
                    <input type="text" class="form-control form-control-lg text-center tracking-widest" 
                           id="otpCode" placeholder="000000" maxlength="6" pattern="[0-9]{6}" required>
                </div>
                
                <button type="submit" class="btn btn-admin-new w-100 py-2 fw-bold" id="verifyOtpBtn">
                    <span class="btn-text">Verify Code</span>
                    <span class="spinner-border spinner-border-sm d-none" role="status"></span>
                </button>
            </form>
            
            <div class="text-center mt-3">
                <button type="button" class="btn btn-link text-admin-new-link" onclick="resendCode()">
                    <i class="fas fa-redo me-1"></i> Resend Code
                </button>
            </div>
        </div>
    </div>

    <!-- Step 3: Reset Password -->
    <div class="form-section" id="resetSection">
        <div class="forgot-card animate__animated animate__fadeInUp">
            <div class="text-center mb-4">
                <i class="fas fa-key fs-1 admin-icon"></i>
                <h2 class="fw-bold mb-2">Reset Password</h2>
                <p class="text-muted">Create your new secure password</p>
            </div>
            
            <form id="resetForm" onsubmit="resetPassword(event)">
                <div class="mb-3">
                    <label class="form-label fw-bold">
                        <i class="fas fa-lock me-2 admin-icon"></i>New Password
                    </label>
                    <input type="password" class="form-control form-control-lg" 
                           id="newPassword" placeholder="••••••••" minlength="6" required>
                    <small class="text-muted">Must be at least 6 characters long</small>
                </div>
                
                <div class="mb-4">
                    <label class="form-label fw-bold">
                        <i class="fas fa-lock me-2 admin-icon"></i>Confirm Password
                    </label>
                    <input type="password" class="form-control form-control-lg" 
                           id="confirmPassword" placeholder="••••••••" minlength="6" required>
                    <small class="text-danger d-none" id="passwordMatchError">Passwords do not match!</small>
                </div>
                
                <button type="submit" class="btn btn-admin-new w-100 py-2 fw-bold" id="resetPasswordBtn">
                    <span class="btn-text">Reset Password</span>
                    <span class="spinner-border spinner-border-sm d-none" role="status"></span>
                </button>
            </form>
            
            <div class="text-center mt-3">
                <a href="AdminLogin.jsp" class="text-admin-new-link">
                    <i class="fas fa-check-circle me-1"></i> Return to Login
                </a>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/StyleSheets/Js/AdminForgotPassword_New.js"></script>
</body>
</html>
