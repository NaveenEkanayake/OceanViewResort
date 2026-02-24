document.addEventListener('DOMContentLoaded', function() {
    // For ViewReservation.jsp - data is pre-loaded server-side
    // For other pages that use AJAX, fetchReservations would be called
    
    // Set up event delegation for delete buttons
    document.addEventListener('click', function(e) {
        if (e.target.closest('.delete-btn')) {
            e.preventDefault();
            const deleteBtn = e.target.closest('.delete-btn');
            const reservationId = deleteBtn.dataset.reservationId;
            if (reservationId) {
                handleDelete(parseInt(reservationId));
            }
        }
    });
});


function fetchReservations() {
    const tableBody = document.getElementById('resTableBody');
    const loadingRow = document.getElementById('loadingRow');
    const formatId = (id) => String(id).padStart(4, '0');

    const contextPathMeta = document.querySelector('meta[name="contextPath"]');
    let fetchUrl;
    if (contextPathMeta && contextPathMeta.getAttribute('content')) {
        fetchUrl = contextPathMeta.getAttribute('content') + '/submitReservation?action=fetchData';
    } else {
        fetchUrl = '../submitReservation?action=fetchData';
    }

    fetch(fetchUrl)
        .then(response => {
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            return response.json();
        })
        .then(data => {
            if (loadingRow) loadingRow.remove();
            tableBody.innerHTML = ''; 
            
            if (!data || data.length === 0) {
                tableBody.innerHTML = `<tr><td colspan="9" class="text-center py-5 text-white">No reservations found.</td></tr>`;
                return;
            }

            data.forEach(res => {
                // Ensure we check both the explicit status and any flag sent by the server
                // If your Servlet sends a 'paid' boolean, use: const isPaid = res.status === 'Paid' || res.isPaid;
                const isPaid = res.status === 'Paid';

                let statusBadge;
                if (isPaid) {
                    statusBadge = '<span class="badge bg-success">Paid</span>';
                } else {
                    statusBadge = `<span class="badge bg-info">${res.status || 'Payment Pending'}</span>`;
                }
                
                const row = `
                    <tr class="text-white align-middle animate__animated animate__fadeIn" id="row-${res.id}"> 
                        <td class="fw-bold text-primary">${formatId(res.id)}</td>
                        <td>${res.guestName}</td>
                        <td>${res.address}</td>
                        <td>${res.contactNumber}</td>
                        <td><span class="badge bg-info text-dark">${res.roomType}</span></td>
                        <td>${res.checkIn}</td>
                        <td>${res.checkOut}</td>
                        <td>${statusBadge}</td>
                        <td class="text-center">
                            <a href="UpdateReservation.jsp?id=${res.id}" class="btn btn-sm btn-outline-warning border-0 me-1">
                                <i class="fa-solid fa-pen-to-square"></i>
                            </a>
                            <button onclick="handleDelete(${res.id})" class="btn btn-sm btn-outline-danger border-0">
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </td>
                    </tr>`;
                tableBody.insertAdjacentHTML('beforeend', row);
            });
        })
        .catch(error => {
            console.error('Fetch Error:', error);
            tableBody.innerHTML = `<tr><td colspan="9" class="text-center text-danger">Error: ${error.message}</td></tr>`;
        });
}
window.handleDelete = function(id) {
    const confirmBtn = document.getElementById('confirmDeleteLink');
    const deleteModalElement = document.getElementById('deleteConfirmModal');
    const modalInstance = bootstrap.Modal.getOrCreateInstance(deleteModalElement);
    
    if (!id) return;

    modalInstance.show();
    confirmBtn.onclick = function() {
        // Get context path for servlet URL
        const contextPathMeta = document.querySelector('meta[name="contextPath"]');
        let deleteUrl;
        if (contextPathMeta && contextPathMeta.getAttribute('content')) {
            deleteUrl = contextPathMeta.getAttribute('content') + `/submitReservation?action=delete&id=${id}`;
        } else {
            deleteUrl = `../submitReservation?action=delete&id=${id}`;
        }
        fetch(deleteUrl)
            .then(response => {
                if (response.ok) {
                    modalInstance.hide();
                    const row = document.getElementById(`row-${id}`);
                    if (row) {
                        row.classList.add('animate__animated', 'animate__fadeOutRight');
                        setTimeout(() => {
                            row.remove();
                            const currentTableBody = document.getElementById('resTableBody');
                            if (currentTableBody.querySelectorAll('tr').length === 0) {
                                fetchReservations(); 
                            }
                        }, 500);
                    }
                    
                    showStatusToast(true, "Reservation deleted successfully.");
                } else {
                    showStatusToast(false, "Failed to delete reservation.");
                }
            })
            .catch(() => showStatusToast(false, "Connection error."));
    };
};

function showStatusToast(isSuccess, message) {
    const toastEl = document.getElementById('statusToast');
    const toastMsg = document.getElementById('toastMessage');
    const toastIcon = document.getElementById('toastIcon');

    toastEl.classList.remove('bg-success', 'bg-danger');
    toastIcon.classList.remove('fa-circle-check', 'fa-circle-xmark');

    if (isSuccess) {
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