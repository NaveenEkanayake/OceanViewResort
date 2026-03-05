// AdminForgotPassword.js - Handles admin forgot password functionality integrated with AdminServlet

let currentEmail = '';
let resendAttempts = 0;
const MAX_RESEND_ATTEMPTS = 3;

// Get context path from page (set by JSP)
const getContextPath = () => {
    // Try to get from a base element or default to OceanViewResort
    const base = document.querySelector('base');
    if (base && base.href) {
        const url = new URL(base.href);
        return url.pathname.replace(/\/$/, '');
    }
    // Fallback: extract from current page URL structure
    const path = window.location.pathname;
    const parts = path.split('/').filter(p => p);
    if (parts.length > 1) {
        return '/' + parts[0];
    }
    return '';
};

// Show toast notification
function showToast(message, type = 'info') {
    const toast = document.getElementById('adminForgotPasswordToast');
    const toastIcon = document.getElementById('toastIcon');
    const toastMessage = document.getElementById('toastMessage');
    
    // Set icon based on type
    if (type === 'success') {
        toastIcon.className = 'fa-solid fa-circle-check me-2 fs-5 text-success';
        toast.classList.remove('bg-danger', 'bg-warning');
        toast.classList.add('bg-success');
    } else if (type === 'error') {
        toastIcon.className = 'fa-solid fa-circle-exclamation me-2 fs-5 text-danger';
        toast.classList.remove('bg-success', 'bg-warning');
        toast.classList.add('bg-danger');
    } else {
        toastIcon.className = 'fa-solid fa-info-circle me-2 fs-5 text-info';
        toast.classList.remove('bg-success', 'bg-danger', 'bg-warning');
        toast.classList.add('bg-info');
    }
    
    toastMessage.textContent = message;
    
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
}

// Switch between form sections
function switchSection(sectionId) {
    document.querySelectorAll('.form-section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById(sectionId).classList.add('active');
}

// Submit email to send OTP
async function submitEmail(event) {
    event.preventDefault();
    
    const email = document.getElementById('adminEmail').value.trim();
    const sendCodeBtn = document.getElementById('sendCodeBtn');
    const btnText = sendCodeBtn.querySelector('.btn-text');
    const spinner = sendCodeBtn.querySelector('.spinner-border-sm');
    
    if (!email) {
        showToast('Please enter your email address', 'error');
        return;
    }
    
    // Validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showToast('Please enter a valid email address', 'error');
        return;
    }
    
    try {
        // Show loading state
        btnText.textContent = 'Sending...';
        spinner.classList.remove('d-none');
        sendCodeBtn.disabled = true;
        
        // Build servlet URL with context path
        const contextPath = getContextPath();
        const servletUrl = window.location.origin + contextPath + '/AdminServlet';
        
        console.log('Context path:', contextPath);
        console.log('Sending OTP request to:', `${servletUrl}?action=sendOTP&email=${encodeURIComponent(email)}`);
        
        // Call AdminServlet to send OTP
        const response = await fetch(`${servletUrl}?action=sendOTP&email=${encodeURIComponent(email)}`, {
            method: 'GET'
        });
        
        console.log('Response status:', response.status);
        console.log('Response headers:', response.headers.get('content-type'));
        
        // Check if response is OK (status 200-299)
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Server error response:', errorText);
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const data = await response.json();
        
        if (data.success) {
            currentEmail = email;
            document.getElementById('displayEmail').textContent = email;
            
            // For demo purposes, show OTP in toast (REMOVE IN PRODUCTION)
            if (data.otpCode) {
                console.log('OTP Code (for demo):', data.otpCode);
                showToast(`Verification code sent! Check console for demo code.`, 'success');
            } else {
                showToast('Verification code sent successfully!', 'success');
            }
            
            // Move to OTP section
            switchSection('otpSection');
        } else {
            showToast(data.message || 'Failed to send verification code', 'error');
            sendCodeBtn.disabled = false;
        }
    } catch (error) {
        console.error('Error sending OTP:', error);
        showToast('Server error. Please try again.', 'error');
        sendCodeBtn.disabled = false;
    } finally {
        // Reset button state
        btnText.textContent = 'Send Verification Code';
        spinner.classList.add('d-none');
    }
}

// Verify OTP code
async function verifyOTP(event) {
    event.preventDefault();
    
    const otp = document.getElementById('otpCode').value.trim();
    const verifyOtpBtn = document.getElementById('verifyOtpBtn');
    const btnText = verifyOtpBtn.querySelector('.btn-text');
    const spinner = verifyOtpBtn.querySelector('.spinner-border-sm');
    
    if (!otp) {
        showToast('Please enter the verification code', 'error');
        return;
    }
    
    if (otp.length !== 6) {
        showToast('Verification code must be 6 digits', 'error');
        return;
    }
    
    try {
        // Show loading state
        btnText.textContent = 'Verifying...';
        spinner.classList.remove('d-none');
        verifyOtpBtn.disabled = true;
        
        // Build servlet URL with context path
        const contextPath = getContextPath();
        const servletUrl = window.location.origin + contextPath + '/AdminServlet';
        
        console.log('Verifying OTP with:', `${servletUrl}?action=verifyOTP&email=${encodeURIComponent(currentEmail)}&otp=${encodeURIComponent(otp)}`);
        
        // Call AdminServlet to verify OTP
        const response = await fetch(`${servletUrl}?action=verifyOTP&email=${encodeURIComponent(currentEmail)}&otp=${encodeURIComponent(otp)}`, {
            method: 'GET'
        });
        
        // Check if response is OK
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Server error response:', errorText);
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const data = await response.json();
        
        if (data.success) {
            showToast('Verification code validated!', 'success');
            // Move to reset password section
            setTimeout(() => {
                switchSection('resetSection');
            }, 1000);
        } else {
            showToast(data.message || 'Invalid verification code', 'error');
            verifyOtpBtn.disabled = false;
        }
    } catch (error) {
        console.error('Error verifying OTP:', error);
        showToast('Server error. Please try again.', 'error');
        verifyOtpBtn.disabled = false;
    } finally {
        // Reset button state
        btnText.textContent = 'Verify Code';
        spinner.classList.add('d-none');
    }
}

// Resend verification code
async function resendCode() {
    if (resendAttempts >= MAX_RESEND_ATTEMPTS) {
        showToast('Maximum resend attempts reached. Please try again later.', 'error');
        return;
    }
    
    try {
        // Build servlet URL with context path
        const contextPath = getContextPath();
        const servletUrl = window.location.origin + contextPath + '/AdminServlet';
                        
        const response = await fetch(`${servletUrl}?action=sendOTP&email=${encodeURIComponent(currentEmail)}`, {
            method: 'GET'
        });
                
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Server error response:', errorText);
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
                
        const data = await response.json();
        
        if (data.success) {
            resendAttempts++;
            showToast(`Verification code resent! (${MAX_RESEND_ATTEMPTS - resendAttempts} attempts remaining)`, 'success');
            if (data.otpCode) {
                console.log('New OTP Code (for demo):', data.otpCode);
            }
        } else {
            showToast(data.message || 'Failed to resend code', 'error');
        }
    } catch (error) {
        console.error('Error resending code:', error);
        showToast('Server error. Please try again.', 'error');
    }
}

// Reset password
async function resetPassword(event) {
    event.preventDefault();
    
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const resetPasswordBtn = document.getElementById('resetPasswordBtn');
    const btnText = resetPasswordBtn.querySelector('.btn-text');
    const spinner = resetPasswordBtn.querySelector('.spinner-border-sm');
    const matchError = document.getElementById('passwordMatchError');
    
    // Validate passwords
    if (newPassword.length < 6) {
        showToast('Password must be at least 6 characters long', 'error');
        return;
    }
    
    if (newPassword !== confirmPassword) {
        matchError.classList.remove('d-none');
        return;
    }
    
    matchError.classList.add('d-none');
    
    try {
        // Show loading state
        btnText.textContent = 'Resetting...';
        spinner.classList.remove('d-none');
        resetPasswordBtn.disabled = true;
        
        // Build servlet URL with context path
        const contextPath = getContextPath();
        const servletUrl = window.location.origin + contextPath + '/AdminServlet';
        
        console.log('Resetting password with:', `${servletUrl}?action=resetPassword&email=${encodeURIComponent(currentEmail)}&newPassword=${encodeURIComponent(newPassword)}`);
        
        // Call AdminServlet to reset password
        const response = await fetch(`${servletUrl}?action=resetPassword&email=${encodeURIComponent(currentEmail)}&newPassword=${encodeURIComponent(newPassword)}`, {
            method: 'GET'
        });
        
        // Check if response is OK
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Server error response:', errorText);
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const data = await response.json();
        
        if (data.success) {
            showToast('Password reset successfully!', 'success');
            
            // Redirect to login after success
            setTimeout(() => {
                window.location.href = 'AdminLogin.jsp';
            }, 2000);
        } else {
            showToast(data.message || 'Failed to reset password', 'error');
            resetPasswordBtn.disabled = false;
        }
    } catch (error) {
        console.error('Error resetting password:', error);
        showToast('Server error. Please try again.', 'error');
        resetPasswordBtn.disabled = false;
    } finally {
        // Reset button state
        btnText.textContent = 'Reset Password';
        spinner.classList.add('d-none');
    }
}

// Clear password match error when user starts typing
document.addEventListener('DOMContentLoaded', function() {
    const confirmPasswordInput = document.getElementById('confirmPassword');
    if (confirmPasswordInput) {
        confirmPasswordInput.addEventListener('input', function() {
            const matchError = document.getElementById('passwordMatchError');
            matchError.classList.add('d-none');
        });
    }
});
