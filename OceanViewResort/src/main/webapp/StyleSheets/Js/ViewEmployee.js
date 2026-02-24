// Toast Notification System for View Employee
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
    
    .loading-spinner {
        border: 3px solid rgba(255,255,255,0.3);
        border-radius: 50%;
        border-top: 3px solid #0dcaf0;
        width: 40px;
        height: 40px;
        animation: spin 1s linear infinite;
        margin: 20px auto;
    }
    
    .loading-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0,0,0,0.7);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 10000;
        backdrop-filter: blur(5px);
    }
    
    .loading-text {
        color: white;
        font-size: 18px;
        margin-top: 15px;
        text-align: center;
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

// Add fadeOutRight animation
const animationStyles = document.createElement('style');
animationStyles.textContent = `
    @keyframes fadeOutRight {
        from {
            opacity: 1;
            transform: translateX(0);
        }
        to {
            opacity: 0;
            transform: translateX(100%);
        }
    }
`;
document.head.appendChild(animationStyles);

// Loading Spinner Functions
function showLoadingSpinner(message = "Loading employee data...") {
    // Remove existing overlay if any
    const existingOverlay = document.querySelector('.loading-overlay');
    if (existingOverlay) {
        document.body.removeChild(existingOverlay);
    }
    
    const overlay = document.createElement('div');
    overlay.className = 'loading-overlay';
    
    const spinnerContainer = document.createElement('div');
    spinnerContainer.style.textAlign = 'center';
    
    const spinner = document.createElement('div');
    spinner.className = 'loading-spinner';
    
    const text = document.createElement('div');
    text.className = 'loading-text';
    text.textContent = message;
    
    spinnerContainer.appendChild(spinner);
    spinnerContainer.appendChild(text);
    overlay.appendChild(spinnerContainer);
    document.body.appendChild(overlay);
}

function hideLoadingSpinner() {
    const overlay = document.querySelector('.loading-overlay');
    if (overlay) {
        overlay.style.animation = 'fadeOut 0.3s ease-out forwards';
        setTimeout(() => {
            if (overlay.parentNode) {
                overlay.parentNode.removeChild(overlay);
            }
        }, 300);
    }
}

// Add fadeOut animation
const fadeStyles = document.createElement('style');
fadeStyles.textContent = `
    @keyframes fadeOut {
        from { opacity: 1; }
        to { opacity: 0; }
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
`;
document.head.appendChild(fadeStyles);

document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM Content Loaded');
    
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
    
    // Add context path meta tag if not exists
    if (!document.querySelector('meta[name="contextPath"]')) {
        const meta = document.createElement('meta');
        meta.name = 'contextPath';
        meta.content = document.querySelector('[href*="/StyleSheets"]')?.href?.split('/StyleSheets')[0] || '';
        document.head.appendChild(meta);
        console.log('Added context path meta tag:', meta.content);
    } else {
        console.log('Context path meta tag already exists:', document.querySelector('meta[name="contextPath"]').content);
    }
    
    // Check for URL parameters and show toast
    const urlParams = new URLSearchParams(window.location.search);
    const error = urlParams.get('error');
    const success = urlParams.get('success');
    const warning = urlParams.get('warning');
    
    if (error) {
        ToastManager.showError(decodeURIComponent(error));
        // Remove parameters from URL
        window.history.replaceState({}, document.title, window.location.pathname);
    } else if (success) {
        ToastManager.showSuccess(decodeURIComponent(success));
        // Remove parameters from URL
        window.history.replaceState({}, document.title, window.location.pathname);
    } else if (warning) {
        ToastManager.showWarning(decodeURIComponent(warning));
        // Remove parameters from URL
        window.history.replaceState({}, document.title, window.location.pathname);
    }
    
    // Load employee data from server
    loadEmployeeData();
    
    // Test delete functionality
    console.log('Delete buttons available:', document.querySelectorAll('.btn-delete').length);
    console.log('Modal available:', !!document.getElementById('deleteEmployeeModal'));
    console.log('Confirm button available:', !!document.getElementById('confirmDeleteBtn'));
});

function loadEmployeeData() {
    // Show loading spinner
    showLoadingSpinner("Fetching employee data...");
    
    // Simulate API call delay (similar to MakeReservation)
    setTimeout(() => {
        hideLoadingSpinner();
        
        // Check if we have employee data
        const employeeRows = document.querySelectorAll('.resort-admin-table-row');
        const noDataMessage = document.querySelector('.text-center.py-5');
        
        if (noDataMessage) {
            ToastManager.showWarning("No employee records found");
        } else if (employeeRows.length > 0) {
            ToastManager.showSuccess(`Successfully loaded ${employeeRows.length} employee record(s)`);
        } else {
            // Database error case
            ToastManager.showError("Failed to fetch employee data from database");
        }
        
        // Add delete button handlers
        addDeleteButtonHandlers();
    }, 800 + Math.random() * 700); // 0.8-1.5 seconds (faster than before)
}

function addDeleteButtonHandlers() {
    // Add click handlers to delete buttons
    const deleteButtons = document.querySelectorAll('.btn-delete');
    console.log('Found delete buttons:', deleteButtons.length);
    
    deleteButtons.forEach((button, index) => {
        console.log(`Button ${index}:`, button);
        console.log(`Employee ID: ${button.dataset.employeeId}`);
        console.log(`Employee Name: ${button.dataset.employeeName}`);
        
        button.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('Delete button clicked!');
            const employeeId = this.dataset.employeeId;
            const employeeName = this.dataset.employeeName;
            
            console.log('Employee ID:', employeeId);
            console.log('Employee Name:', employeeName);
            
            if (employeeId && employeeName) {
                showDeleteModal(employeeId, employeeName);
            } else {
                console.error('Missing employee data!');
            }
        });
    });
}

function showDeleteModal(employeeId, employeeName) {
    console.log('Showing delete modal for:', employeeName, '(ID:', employeeId, ')');
    
    // Set employee name in modal
    const nameElement = document.getElementById('employeeName');
    if (nameElement) {
        nameElement.textContent = employeeName;
    } else {
        console.error('Employee name element not found!');
    }
    
    // Get modal elements
    const modalElement = document.getElementById('deleteEmployeeModal');
    const confirmBtn = document.getElementById('confirmDeleteBtn');
    
    if (!modalElement) {
        console.error('Delete modal element not found!');
        return;
    }
    
    if (!confirmBtn) {
        console.error('Confirm button not found!');
        return;
    }
    
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
    
    // Show modal
    modal.show();
    
    // Set up confirm button handler
    confirmBtn.onclick = function() {
        console.log('Confirm delete clicked for:', employeeName);
        deleteEmployee(employeeId, employeeName);
        modal.hide();
    };
}

function deleteEmployee(employeeId, employeeName) {
    console.log('deleteEmployee called with:', employeeId, employeeName);
    
    // Show loading toast
    showEmployeeToast(false, `Deleting ${employeeName}...`, true);
    
    // Simple approach: use the same pattern as other forms in the application
    // Other JSP files use: ${pageContext.request.contextPath}/Pages/EmployeeServlet
    
    // Get context path from meta tag (should be set by JSP)
    const metaTag = document.querySelector('meta[name="contextPath"]');
    let contextPath = '';
    
    console.log('Meta tag found:', metaTag);
    
    if (metaTag) {
        contextPath = metaTag.getAttribute('content');
    }
    
    // If no context path from meta tag, try to derive it
    if (!contextPath) {
        // From current URL: /context/Pages/ViewEmployee.jsp
        const path = window.location.pathname;
        const pagesIndex = path.indexOf('/Pages/');
        if (pagesIndex > 0) {
            contextPath = path.substring(0, pagesIndex);
        }
    }
    
    // Construct URL - if we have context path, use it, otherwise use relative path
    let deleteUrl;
    if (contextPath) {
        deleteUrl = `${contextPath}/Pages/EmployeeServlet?action=delete&id=${employeeId}`;
    } else {
        // Fallback: relative path from current location (/Pages/ViewEmployee.jsp)
        // ../ goes to context root, then /Pages/EmployeeServlet
        deleteUrl = `../Pages/EmployeeServlet?action=delete&id=${employeeId}`;
    }
    
    console.log('Context path:', contextPath);
    console.log('Delete URL:', deleteUrl); // Debug line
    
    // Make AJAX request
    fetch(deleteUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        }
    })
    .then(response => {
        // Check if response is JSON
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return response.json();
        } else {
            // If not JSON, it's probably an error page
            return response.text().then(text => {
                console.error('Server returned HTML instead of JSON:', text);
                throw new Error('Server error: ' + (text.substring(0, 200) + (text.length > 200 ? '...' : '')));
            });
        }
    })
    .then(data => {
        if (data.success) {
            // Remove the row with animation
            const targetRow = document.querySelector(`tr[data-employee-id="${employeeId}"]`);
            
            if (targetRow) {
                targetRow.style.animation = 'fadeOutRight 0.5s ease-out forwards';
                setTimeout(() => {
                    targetRow.remove();
                    
                    // Check if table is empty
                    const remainingRows = document.querySelectorAll('.resort-admin-table-row');
                    if (remainingRows.length === 0) {
                        showEmptyTableMessage();
                    }
                }, 500);
            }
            
            showEmployeeToast(true, data.message || `${employeeName} deleted successfully`);
        } else {
            showEmployeeToast(false, data.message || `Failed to delete ${employeeName}`);
        }
    })
    .catch(error => {
        console.error('Delete error:', error);
        let errorMessage = `Error deleting ${employeeName}`;
        
        if (error.message.includes('Server error')) {
            errorMessage = error.message;
        } else if (error.message.includes('Network') || error.message.includes('Failed to fetch')) {
            errorMessage = `Connection error while deleting ${employeeName}. Please check your network.`;
        }
        
        showEmployeeToast(false, errorMessage);
    });
}

function showEmployeeToast(isSuccess, message, isLoading = false) {
    const toastEl = document.getElementById('employeeToast');
    const toastMsg = document.getElementById('toastMessage');
    const toastIcon = document.getElementById('toastIcon');

    toastEl.classList.remove('bg-success', 'bg-danger', 'bg-info');
    toastIcon.classList.remove('fa-circle-check', 'fa-circle-xmark', 'fa-spinner');

    if (isLoading) {
        toastEl.classList.add('bg-info');
        toastIcon.classList.add('fa-spinner', 'fa-spin');
    } else if (isSuccess) {
        toastEl.classList.add('bg-success');
        toastIcon.classList.add('fa-circle-check');
    } else {
        toastEl.classList.add('bg-danger');
        toastIcon.classList.add('fa-circle-xmark');
    }

    toastMsg.textContent = message;
    const bsToast = new bootstrap.Toast(toastEl);
    bsToast.show();
}

function showEmptyTableMessage() {
    const tableBody = document.querySelector('.resort-admin-custom-table tbody');
    if (tableBody) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="9" class="text-center py-5">
                    <div class="text-white-50">
                        <i class="fas fa-users fa-3x mb-3"></i>
                        <h4>No Employees Found</h4>
                        <p>There are no employees in the system yet.</p>
                    </div>
                </td>
            </tr>
        `;
    }
}