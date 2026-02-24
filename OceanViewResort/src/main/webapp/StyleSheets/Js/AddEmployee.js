// Toast Notification System for Add Employee
const ToastManager = {
    showSuccess(message, duration = 5000) {
        this.showToast(message, 'success', duration);
    },
    
    showError(message, duration = 5000) {
        this.showToast(message, 'error', duration);
    },
    
    showWarning(message, duration = 5000) {
        this.showToast(message, 'warning', duration);
    },
    
    showToast(message, type = 'info', duration = 5000) {
        // Remove existing toasts
        this.removeExistingToasts();
        
        // Create toast container if it doesn't exist
        let toastContainer = document.getElementById('toast-container');
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.id = 'toast-container';
            toastContainer.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 9999;
                width: 350px;
                max-width: 90vw;
            `;
            document.body.appendChild(toastContainer);
        }
        
        // Create toast element
        const toast = document.createElement('div');
        toast.className = 'toast-notification';
        
        // Set styles based on type
        let backgroundColor, icon, iconColor;
        switch (type) {
            case 'success':
                backgroundColor = '#198754';
                icon = '✓';
                iconColor = '#fff';
                break;
            case 'error':
                backgroundColor = '#dc3545';
                icon = '✕';
                iconColor = '#fff';
                break;
            case 'warning':
                backgroundColor = '#ffc107';
                icon = '⚠';
                iconColor = '#000';
                break;
            default:
                backgroundColor = '#0d6efd';
                icon = 'ℹ';
                iconColor = '#fff';
        }
        
        toast.style.cssText = `
            background: ${backgroundColor};
            color: ${iconColor === '#fff' ? '#fff' : '#000'};
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 10px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            display: flex;
            align-items: center;
            animation: toastSlideIn 0.3s ease-out;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255,255,255,0.1);
            font-family: system-ui, -apple-system, sans-serif;
            font-size: 14px;
            font-weight: 500;
        `;
        
        // Add icon
        const iconElement = document.createElement('span');
        iconElement.style.cssText = `
            font-size: 18px;
            margin-right: 12px;
            font-weight: bold;
        `;
        iconElement.textContent = icon;
        
        // Add message
        const messageElement = document.createElement('span');
        messageElement.textContent = message;
        messageElement.style.cssText = 'flex: 1; line-height: 1.4;';
        
        // Add close button
        const closeBtn = document.createElement('button');
        closeBtn.innerHTML = '×';
        closeBtn.style.cssText = `
            background: transparent;
            border: none;
            color: ${iconColor};
            font-size: 20px;
            cursor: pointer;
            padding: 0;
            margin-left: 15px;
            opacity: 0.8;
            transition: opacity 0.2s;
        `;
        closeBtn.addEventListener('mouseenter', () => {
            closeBtn.style.opacity = '1';
        });
        closeBtn.addEventListener('mouseleave', () => {
            closeBtn.style.opacity = '0.8';
        });
        closeBtn.addEventListener('click', () => {
            this.hideToast(toast);
        });
        
        // Assemble toast
        toast.appendChild(iconElement);
        toast.appendChild(messageElement);
        toast.appendChild(closeBtn);
        
        // Add to container
        toastContainer.appendChild(toast);
        
        // Auto hide after duration
        if (duration > 0) {
            setTimeout(() => {
                this.hideToast(toast);
            }, duration);
        }
        
        return toast;
    },
    
    hideToast(toast) {
        if (toast && toast.parentNode) {
            toast.style.animation = 'toastSlideOut 0.3s ease-in forwards';
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
                this.removeEmptyContainer();
            }, 300);
        }
    },
    
    removeExistingToasts() {
        const existingToasts = document.querySelectorAll('.toast-notification');
        existingToasts.forEach(toast => {
            toast.style.animation = 'toastSlideOut 0.3s ease-in forwards';
        });
        setTimeout(() => {
            existingToasts.forEach(toast => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            });
            this.removeEmptyContainer();
        }, 300);
    },
    
    removeEmptyContainer() {
        const container = document.getElementById('toast-container');
        if (container && container.children.length === 0) {
            container.parentNode.removeChild(container);
        }
    }
};

// Add CSS animations
const toastStyles = document.createElement('style');
toastStyles.textContent = `
    @keyframes toastSlideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes toastSlideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
    
    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
    
    .btn-loading {
        position: relative;
        pointer-events: none;
    }
    
    .btn-loading .btn-text {
        visibility: hidden;
    }
    
    .btn-loading::after {
        content: '';
        position: absolute;
        width: 16px;
        height: 16px;
        top: 50%;
        left: 50%;
        margin-top: -8px;
        margin-left: -8px;
        border: 2px solid transparent;
        border-top-color: #ffffff;
        border-radius: 50%;
        animation: spin 0.6s linear infinite;
    }
    
    @media (max-width: 768px) {
        #toast-container {
            width: 90vw !important;
            right: 5vw !important;
            left: 5vw !important;
        }
    }
`;
document.head.appendChild(toastStyles);

document.addEventListener('DOMContentLoaded', function() {
    const toggleBtn = document.getElementById('toggleBtn');
    const body = document.body;

    // Sidebar Toggle
    if (toggleBtn) {
        toggleBtn.addEventListener('click', function() {
            body.classList.toggle('sidebar-is-collapsed');
            
            // Auto-close submenus on collapse
            if (body.classList.contains('sidebar-is-collapsed')) {
                document.querySelectorAll('.submenu').forEach(s => {
                    s.style.display = 'none';
                    s.classList.remove('show');
                });
                document.querySelectorAll('.chevron-icon').forEach(c => c.classList.remove('rotated'));
            }
        });
    }

    // Dropdown Styling Fix
    const selectOptions = document.querySelectorAll('.ae-custom-select option');
    selectOptions.forEach(opt => opt.style.backgroundColor = "#181818");
    
    // Check for URL parameters and show toast
    const urlParams = new URLSearchParams(window.location.search);
    const error = urlParams.get('error');
    const success = urlParams.get('success');
    
    if (error) {
        ToastManager.showError(decodeURIComponent(error));
        // Remove parameters from URL
        window.history.replaceState({}, document.title, window.location.pathname);
    } else if (success) {
        ToastManager.showSuccess(decodeURIComponent(success));
        // Remove parameters from URL
        window.history.replaceState({}, document.title, window.location.pathname);
    }
    
    // Add form submission handler with loading spinner
    const employeeForm = document.querySelector('form[action*="EmployeeServlet"]');
    if (employeeForm) {
        employeeForm.addEventListener('submit', function(e) {
            const submitBtn = this.querySelector('button[type="submit"]');
            if (submitBtn) {
                // Add loading state
                submitBtn.classList.add('btn-loading');
                const btnText = submitBtn.querySelector('.btn-text') || submitBtn;
                if (!btnText.classList.contains('btn-text')) {
                    // Wrap button text if not already wrapped
                    const text = btnText.textContent;
                    btnText.innerHTML = `<span class="btn-text">${text}</span>`;
                }
                
                // Show processing message
                ToastManager.showWarning('Processing employee registration...');
            }
        });
    }
});

// Submenu Toggle Function
function toggleSubmenu(menuId, chevronId) {
    if (document.body.classList.contains('sidebar-is-collapsed')) {
        document.body.classList.remove('sidebar-is-collapsed');
    }

    const menu = document.getElementById(menuId);
    const chevron = document.getElementById(chevronId);
    
    if (menu && chevron) {
        const isShowing = menu.classList.contains('show');
        menu.classList.toggle('show');
        chevron.classList.toggle('rotated');
        menu.style.display = isShowing ? 'none' : 'block';
    }
}