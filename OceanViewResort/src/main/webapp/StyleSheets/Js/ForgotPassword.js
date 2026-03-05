// ForgotPassword.js - Employee Password Reset System

let employeeEmail = '';
let otpCode = '';
let forgotPasswordToast = null;

// Get context path from window location
const getContextPath = () => {
    const path = window.location.pathname;
    return path.substring(0, path.indexOf('/', 1));
};

// Initialize toast when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    const toastElement = document.getElementById('forgotPasswordToast');
    if (toastElement) {
        forgotPasswordToast = new bootstrap.Toast(toastElement, { delay: 4000 });
    }
});

// Show loading spinner on button
function showLoading(buttonId) {
    const btn = document.getElementById(buttonId);
    if (btn) {
        const btnText = btn.querySelector('.btn-text');
        const spinner = btn.querySelector('.spinner-border');
        if (btnText) btnText.classList.add('d-none');
        if (spinner) spinner.classList.remove('d-none');
        btn.disabled = true;
    }
}

// Hide loading spinner from button
function hideLoading(buttonId) {
    const btn = document.getElementById(buttonId);
    if (btn) {
        const btnText = btn.querySelector('.btn-text');
        const spinner = btn.querySelector('.spinner-border');
        if (btnText) btnText.classList.remove('d-none');
        if (spinner) spinner.classList.add('d-none');
        btn.disabled = false;
    }
}

// Show different sections
function showSection(sectionId) {
    document.querySelectorAll('.form-section').forEach(section => {
        section.classList.remove('active');
    });
    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        targetSection.classList.add('active');
    }
}

// Update step indicator
function updateStepIndicator(stepNumber) {
    document.querySelectorAll('.step').forEach((step, index) => {
        step.classList.remove('active', 'completed');
        if (index + 1 < stepNumber) {
            step.classList.add('completed');
            step.innerHTML = '<i class="fas fa-check"></i>';
        } else if (index + 1 === stepNumber) {
            step.classList.add('active');
            step.textContent = index + 1;
        } else {
            step.textContent = index + 1;
        }
    });
}

// Show toast notification
function showToast(message, type = 'info') {
    if (!forgotPasswordToast) return;
    
    const toastEl = document.getElementById('forgotPasswordToast');
    const toastIcon = document.getElementById('toastIcon');
    const toastMessage = document.getElementById('toastMessage');
    
    // Remove old color classes
    toastEl.classList.remove('bg-success', 'bg-danger', 'bg-warning', 'bg-info');
    
    // Set icon and color based on type
    switch(type) {
        case 'success':
            toastEl.classList.add('bg-success');
            toastIcon.className = 'fa-solid fa-check-circle me-2 fs-5';
            break;
        case 'error':
            toastEl.classList.add('bg-danger');
            toastIcon.className = 'fa-solid fa-exclamation-circle me-2 fs-5';
            break;
        case 'warning':
            toastEl.classList.add('bg-warning');
            toastIcon.className = 'fa-solid fa-triangle-exclamation me-2 fs-5';
            break;
        default:
            toastEl.classList.add('bg-info');
            toastIcon.className = 'fa-solid fa-info-circle me-2 fs-5';
    }
    
    toastMessage.textContent = message;
    forgotPasswordToast.show();
}

// Submit email to get OTP
async function submitEmail(event) {
    event.preventDefault();
    employeeEmail = document.getElementById('employeeEmail').value;
    
    showLoading('sendCodeBtn');
    
    try {
        const response = await fetch(getContextPath() + '/EmployeeForgotPasswordServlet?action=sendOTP', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'email=' + encodeURIComponent(employeeEmail)
        });
        
        const result = await response.json();
        
        if (result.success) {
            otpCode = result.otpCode; // Store for verification (remove in production)
            document.getElementById('displayEmail').textContent = employeeEmail;
            updateStepIndicator(2);
            showSection('otpSection');
            showToast('Verification code sent to your email!', 'success');
        } else {
            showToast(result.message, 'error');
            hideLoading('sendCodeBtn');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Failed to send verification code. Please try again.', 'error');
        hideLoading('sendCodeBtn');
    }
}

// Verify OTP code
async function verifyOTP(event) {
    event.preventDefault();
    const enteredOTP = document.getElementById('otpCode').value;
    
    showLoading('verifyOtpBtn');
    
    try {
        const response = await fetch(getContextPath() + '/EmployeeForgotPasswordServlet?action=verifyOTP', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'email=' + encodeURIComponent(employeeEmail) + '&otp=' + encodeURIComponent(enteredOTP)
        });
        
        const result = await response.json();
        
        if (result.success) {
            updateStepIndicator(3);
            showSection('passwordSection');
            showToast('Code verified successfully!', 'success');
        } else {
            showToast('Invalid verification code. Please try again.', 'error');
            hideLoading('verifyOtpBtn');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Failed to verify code. Please try again.', 'error');
        hideLoading('verifyOtpBtn');
    }
}

// Reset password with new password
async function resetPassword(event) {
    event.preventDefault();
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    // Check if passwords match
    if (newPassword !== confirmPassword) {
        document.getElementById('passwordMatchError').classList.remove('d-none');
        return;
    }
    
    showLoading('resetPasswordBtn');
    
    try {
        const response = await fetch(getContextPath() + '/EmployeeForgotPasswordServlet?action=resetPassword', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'email=' + encodeURIComponent(employeeEmail) + '&newPassword=' + encodeURIComponent(newPassword)
        });
        
        const result = await response.json();
        
        if (result.success) {
            updateStepIndicator(4);
            showSection('successSection');
            showToast('Password reset successful! Redirecting to login...', 'success');
            
            // Redirect to login after 3 seconds
            setTimeout(function() {
                window.location.href = 'EmployeeLogin.jsp';
            }, 3000);
        } else {
            showToast(result.message, 'error');
            hideLoading('resetPasswordBtn');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Failed to reset password. Please try again.', 'error');
        hideLoading('resetPasswordBtn');
    }
}
