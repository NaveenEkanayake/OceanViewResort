<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, Model.Reservation, DAO.ReservationDao, DAO.PaymentDao, Model.Payment, java.sql.Connection, DB.DBConnect" %>
<%
ReservationDao reservationDao = new ReservationDao();
PaymentDao paymentDao = new PaymentDao();
List<Reservation> reservations = null;
String errorMessage = null;

try {
    reservations = reservationDao.getAllReservations();
    
    if (reservations != null) {
        for (Reservation res : reservations) {
            // Fetch associated payments for this specific reservation ID
            List<Payment> payments = paymentDao.getPaymentsByReservationId(res.getId());
            
            if (payments != null && !payments.isEmpty()) {
                // Check if ANY payment for this reservation has isPaid set to true
                // In MySQL, 1 is true. JDBC's getBoolean handles this automatically.
                boolean hasPaidPayment = payments.stream().anyMatch(p -> p.isPaid());
                
                // Debugging: Check your server logs/console for these outputs
                System.out.println("DEBUG: Res ID " + res.getId() + " | Payments found: " + payments.size() + " | Any Paid: " + hasPaidPayment);
                
                if (hasPaidPayment) {
                    res.setStatus("Paid");
                    System.out.println("DEBUG: Status for Res " + res.getId() + " forced to Paid.");
                } else {
                    // If payments exist but none are marked 'Paid', ensure status reflects that
                    res.setStatus("Payment Pending");
                }
            } else {
                // No payment records found at all
                res.setStatus("Payment Pending");
            }
        }
    }
} catch (Exception e) {
    errorMessage = e.getMessage();
    e.printStackTrace();
    reservations = java.util.Collections.emptyList();
}
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Ocean Resort - View Reservations</title>
    <meta name="contextPath" content="${pageContext.request.contextPath}">
    
    <script>
        if (localStorage.getItem('sidebarState') === 'collapsed') {
            document.documentElement.classList.add('sidebar-is-collapsed');
        }
    </script>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>
    <link rel="stylesheet" href="../StyleSheets/Css/SideBar.css">
    <link rel="stylesheet" href="../StyleSheets/Css/ViewReservation.css">
</head>

<body class="view-res-page-bg">
    <%@ include file="../Components/SideBar.jsp" %>

    <div class="content-wrapper">
        <div class="container view-res-card mt-5 animate__animated animate__fadeIn">
            <h2 class="text-center mb-4 text-white">
                <i class="fa-solid fa-hotel me-2 text-white"></i> View Reservations
            </h2>
            
            <div class="table-responsive">
                <table class="table view-res-table table-hover border-0 text-white"> 
                    <thead>
                        <tr>
                            <th class="text-white border-0 py-3">Res ID</th>
                            <th class="text-white border-0 py-3">Guest Name</th>
                            <th class="text-white border-0 py-3">Address</th>
                            <th class="text-white border-0 py-3">Contact</th>
                            <th class="text-white border-0 py-3">Room Type</th>
                            <th class="text-white border-0 py-3">Check-In</th>
                            <th class="text-white border-0 py-3">Check-Out</th>
                            <th class="text-white border-0 py-3">Status</th>
                            <th class="text-white border-0 py-3 text-center">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="resTableBody">
                        <%
                        if (errorMessage != null) {
                        %>
                        <tr>
                            <td colspan="9" class="text-center text-danger py-4">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                Error loading data: <%= errorMessage %>
                            </td>
                        </tr>
                        <%
                        } else if (reservations != null && !reservations.isEmpty()) {
                            for (Reservation res : reservations) {
                                String editUrl = "UpdateReservation.jsp?id=" + res.getId() +
                                                "&guestName=" + java.net.URLEncoder.encode(res.getGuestName(), "UTF-8") +
                                                "&address=" + java.net.URLEncoder.encode(res.getAddress(), "UTF-8") +
                                                "&contactNumber=" + java.net.URLEncoder.encode(res.getContactNumber(), "UTF-8") +
                                                "&roomType=" + java.net.URLEncoder.encode(res.getRoomType(), "UTF-8") +
                                                "&checkIn=" + res.getCheckIn() +
                                                "&checkOut=" + res.getCheckOut();
                        
                                String statusBadge;
                                if ("Paid".equals(res.getStatus())) {
                                    statusBadge = "<span class=\"badge bg-success\">Paid</span>";
                                } else if ("Payment Pending".equals(res.getStatus())) {
                                    statusBadge = "<span class=\"badge bg-info\">Payment Pending</span>";
                                } else {
                                    statusBadge = "<span class=\"badge bg-warning\">" + (res.getStatus() != null ? res.getStatus() : "Payment Pending") + "</span>";
                                }
                        %>
                        <tr class="text-white align-middle animate__animated animate__fadeIn" id="row-<%= res.getId() %>"> 
                            <td class="fw-bold text-primary"><%= String.format("%04d", res.getId()) %></td>
                            <td><%= res.getGuestName() %></td>
                            <td><%= res.getAddress() %></td>
                            <td><%= res.getContactNumber() %></td>
                            <td><span class="badge bg-info text-dark"><%= res.getRoomType() %></span></td>
                            <td><%= res.getCheckIn() %></td>
                            <td><%= res.getCheckOut() %></td>
                            <td><%= statusBadge %></td>
                            <td class="text-center">
                                <a href="<%= editUrl %>" class="btn btn-sm btn-outline-warning border-0 me-1">
                                    <i class="fa-solid fa-pen-to-square"></i>
                                </a>
                                <button data-reservation-id="<%= res.getId() %>" class="btn btn-sm btn-outline-danger border-0 delete-btn">
                                    <i class="fa-solid fa-trash"></i>
                                </button>
                            </td>
                        </tr>
                        <%
                            }
                        } else {
                        %>
                        <tr>
                            <td colspan="9" class="text-center py-5 text-white animate__animated animate__fadeIn">
                                <i class="fa-solid fa-folder-open d-block mb-3 fs-1 text-white-50"></i>
                                <span class="fw-bold">No reservations found.</span>
                            </td>
                        </tr>
                        <%
                        }
                        %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content glass-modal bg-dark text-white border-0 shadow-lg">
                <div class="modal-body p-5 text-center">
                    <div class="mb-4">
                        <i class="fa-solid fa-circle-exclamation text-danger animate__animated animate__pulse animate__infinite" style="font-size: 4rem;"></i>
                    </div>
                    <h4 class="fw-bold">Are you sure?</h4>
                    <p class="text-white-50 mb-4">This action will permanently remove the reservation from the system. This cannot be undone.</p>
                    <div class="d-flex justify-content-center gap-3">
                        <button type="button" class="btn btn-link text-white text-decoration-none px-4" data-bs-dismiss="modal">Cancel</button>
                        <button id="confirmDeleteLink" class="btn btn-danger px-5 rounded-pill fw-bold shadow-sm">Delete Now</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 2000;">
        <div id="statusToast" class="toast align-items-center text-white border-0 shadow-lg" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body d-flex align-items-center py-3">
                    <i id="toastIcon" class="fa-solid me-2 fs-5"></i>
                    <span id="toastMessage"></span>
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="../StyleSheets/Js/Sidebar.js"></script>
    <script src="../StyleSheets/Js/ViewReservations.js?v=2"></script>

    <script>
    document.addEventListener('DOMContentLoaded', function() {
        // Handle Toast from previous page actions (if any)
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has('deleted')) {
            showStatusToast(true, "Reservation deleted successfully.");
            window.history.replaceState({}, document.title, window.location.pathname);
        }
    });
    </script>
</body>
</html>