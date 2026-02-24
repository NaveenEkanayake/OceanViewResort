/**
 * Switch between Login and Register tabs
 */
function switchTab(tabName, event) {
    if (event) event.preventDefault();
    
    // Hide all form containers and remove active class from buttons
    document.querySelectorAll('.al-form-container').forEach(form => {
        form.classList.remove('active');
    });
    document.querySelectorAll('.al-tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Show the selected form and activate the clicked button
    const targetForm = document.getElementById(tabName + 'Form');
    const targetButton = event ? event.currentTarget : 
        document.querySelector(`.al-tab-btn:nth-child(${tabName === 'login' ? 1 : 2})`);
    
    if (targetForm) targetForm.classList.add('active');
    if (targetButton) targetButton.classList.add('active');
}

document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const statusModalEl = document.getElementById('statusModal');
    // Initialize Bootstrap Modal
    const statusModal = new bootstrap.Modal(statusModalEl);

    // --- FORM SUBMISSION ANIMATION ---
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Determine if this is the login or register form
            const action = new URL(form.action).searchParams.get('action');
            const isLogin = action === 'login';
            const btn = document.getElementById(isLogin ? 'loginBtn' : 'regBtn');
            const text = document.getElementById(isLogin ? 'loginText' : 'regText');
            const spinner = document.getElementById(isLogin ? 'loginSpinner' : 'regSpinner');

            // Trigger Loading State
            if (btn) btn.disabled = true;
            if (text) text.textContent = "Processing...";
            if (spinner) spinner.classList.remove('d-none');

            // Natural delay for visual feedback before submission
            setTimeout(() => { form.submit(); }, 1000);
        });
    });

    // --- REDIRECTION & FEEDBACK LOGIC ---
    if (urlParams.has('success')) {
        const type = urlParams.get('success');
        let message = "";
        
        if (type === 'login') {
            message = "Login Successful! Redirecting...";
            handleUrlFeedback('Success', message, "fa-circle-check text-success");
            statusModal.show();
            setTimeout(() => { window.location.href = 'AdminDashboard.jsp'; }, 2000);
        } else if (type === 'registered') {
            message = "Registration Successful! You can now login.";
            handleUrlFeedback('Success', message, "fa-circle-check text-success");
            statusModal.show();
            // Switch to login tab after registration
            setTimeout(() => {
                switchTab('login', null);
                statusModal.hide();
            }, 2000);
        } else if (type === 'logout') {
            message = "You have been logged out successfully.";
            handleUrlFeedback('Success', message, "fa-circle-check text-success");
            statusModal.show();
        }
    } else if (urlParams.has('error')) {
        const errorType = urlParams.get('error');
        let message = "Authentication failed. Please try again.";
        
        if (errorType === 'reg_fail') {
            message = "Registration failed. Username or email may already exist.";
        } else if (errorType === 'login_fail') {
            message = "Invalid username or password.";
        }
        
        handleUrlFeedback('Error', message, "fa-circle-xmark text-danger");
        statusModal.show();
    }

    /**
     * Updates modal content based on status
     */
    function handleUrlFeedback(t, m, i) {
        document.getElementById('modalTitle').innerText = t;
        document.getElementById('modalMessage').innerText = m;
        document.getElementById('modalIcon').className = "fa-solid " + i + " display-1 mb-3";
    }
});