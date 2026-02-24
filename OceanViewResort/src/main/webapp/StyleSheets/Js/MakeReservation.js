// Sidebar & Submenu Logic
const toggleBtn = document.getElementById('toggleBtn');
const sidebar = document.getElementById('sidebar');

if (toggleBtn) {
    toggleBtn.addEventListener('click', function() {
        sidebar.classList.toggle('collapsed');
        toggleBtn.classList.toggle('rotate-icon');
    });
}

function toggleSubmenu(submenuId, chevronId) {
    const submenu = document.getElementById(submenuId);
    const chevron = document.getElementById(chevronId);
    if (submenu && chevron) {
        submenu.classList.toggle('show');
        chevron.classList.toggle('rotated');
    }
}

// Form & Toast Logic
document.addEventListener('DOMContentLoaded', function() {
    const resForm = document.getElementById('resForm');
    const submitBtn = document.getElementById('submitBtn');
    const btnText = document.getElementById('btnText');
    const btnSpinner = document.getElementById('btnSpinner');
    const toastEl = document.getElementById('reservationToast');
    const toastMsg = document.getElementById('toastMsg');

    if (resForm) {
        resForm.addEventListener('submit', function(e) {
            e.preventDefault();

            // 1. Validation
            if (!resForm.checkValidity()) {
                e.stopPropagation();
                resForm.classList.add('was-validated');
                return;
            }

            // 2. Show Loading State
            submitBtn.disabled = true;
            btnText.textContent = "Processing...";
            btnSpinner.classList.remove('d-none');

            const formData = new FormData(resForm);

            // 3. Perform Fetch
            // Get context path for servlet URL
            const contextPathMeta = document.querySelector('meta[name="contextPath"]');
            let submitUrl;
            if (contextPathMeta && contextPathMeta.getAttribute('content')) {
                submitUrl = contextPathMeta.getAttribute('content') + '/submitReservation';
            } else {
                submitUrl = '../submitReservation';
            }

            fetch(submitUrl, {
                method: 'POST',
                body: new URLSearchParams(formData)
            })
            .then(response => {
                // Slight delay (1 second) so the user sees the loading animation
                setTimeout(() => {
                    if (response.ok) {
                        showToast(true, "Reservation Created Successfully!");
                        resForm.reset();
                        resForm.classList.remove('was-validated');

                        // Update the Progress Bars in the UI immediately
                        if (typeof updateRoomBars === "function") {
                            updateRoomBars();
                        }
                    } else if (response.status === 409) {
                        // Handles "Exceed value" logic from the Servlet
                        showToast(false, "No rooms available for this type!");
                    } else {
                        showToast(false, "Error: Could not save reservation.");
                    }
                    
                    // 4. Reset Button State
                    submitBtn.disabled = false;
                    btnText.textContent = "Confirm Reservation";
                    btnSpinner.classList.add('d-none');
                }, 1000); 
            })
            .catch(error => {
                console.error('Error:', error);
                showToast(false, "Connection error.");
                submitBtn.disabled = false;
                btnText.textContent = "Confirm Reservation";
                btnSpinner.classList.add('d-none');
            });
        });
    }

    function showToast(isSuccess, message) {
        if (!toastEl) return;
        toastEl.classList.remove('bg-success', 'bg-danger');
        toastEl.classList.add(isSuccess ? 'bg-success' : 'bg-danger');
        toastMsg.innerHTML = `<i class="fa-solid ${isSuccess ? 'fa-circle-check' : 'fa-circle-xmark'} me-2"></i> ${message}`;
        
        const bsToast = bootstrap.Toast.getOrCreateInstance(toastEl);
        bsToast.show();
    }
});