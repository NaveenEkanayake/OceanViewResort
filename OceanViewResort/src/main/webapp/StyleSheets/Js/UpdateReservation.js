document.addEventListener('DOMContentLoaded', function() {
    const resForm = document.getElementById('resForm');
    const submitBtn = document.getElementById('submitBtn');
    const btnText = document.getElementById('btnText');
    const btnSpinner = document.getElementById('btnSpinner');
    const toastEl = document.getElementById('reservationToast');
    const toastMsg = document.getElementById('toastMsg');

    // --- STEP 1: FETCH DATA FROM URL ---
    // This expects the URL to look like: UpdateReservation.jsp?id=101&guestName=John...
    const urlParams = new URLSearchParams(window.location.search);
    
    if (urlParams.has('id')) {
        document.getElementById('resId').value = urlParams.get('id');
        resForm.querySelector('[name="guestName"]').value = urlParams.get('guestName') || '';
        resForm.querySelector('[name="address"]').value = urlParams.get('address') || '';
        resForm.querySelector('[name="contactNumber"]').value = urlParams.get('contactNumber') || '';
        resForm.querySelector('[name="roomType"]').value = urlParams.get('roomType') || '';
        resForm.querySelector('[name="checkIn"]').value = urlParams.get('checkIn') || '';
        resForm.querySelector('[name="checkOut"]').value = urlParams.get('checkOut') || '';
    }

    // --- STEP 2: HANDLE THE UPDATE SUBMISSION ---
    if (resForm) {
        resForm.addEventListener('submit', function(e) {
            e.preventDefault();

            if (!resForm.checkValidity()) {
                resForm.classList.add('was-validated');
                return;
            }

            // UI Loading State
            submitBtn.disabled = true;
            btnText.textContent = "Updating Stay...";
            btnSpinner.classList.remove('d-none');

            const formData = new URLSearchParams(new FormData(resForm));

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
                body: formData
            })
            .then(response => {
                setTimeout(() => {
                    if (response.ok) {
                        showToast(true, "Reservation Details Updated!");
                        // Optional: Redirect back to the view table after 2 seconds
                        // setTimeout(() => { window.location.href = "ViewReservations.jsp"; }, 2000);
                    } else {
                        showToast(false, "Update failed. Please try again.");
                    }
                    
                    submitBtn.disabled = false;
                    btnText.textContent = "Update Reservation";
                    btnSpinner.classList.add('d-none');
                }, 1000); 
            })
            .catch(error => {
                showToast(false, "Network error occurred.");
                submitBtn.disabled = false;
                btnText.textContent = "Update Reservation";
                btnSpinner.classList.add('d-none');
            });
        });
    }

    function showToast(isSuccess, message) {
        toastEl.classList.remove('bg-success', 'bg-danger');
        toastEl.classList.add(isSuccess ? 'bg-success' : 'bg-danger');
        toastMsg.innerHTML = `<i class="fa-solid ${isSuccess ? 'fa-circle-check' : 'fa-circle-xmark'} me-2"></i> ${message}`;
        const bsToast = bootstrap.Toast.getOrCreateInstance(toastEl);
        bsToast.show();
    }
});