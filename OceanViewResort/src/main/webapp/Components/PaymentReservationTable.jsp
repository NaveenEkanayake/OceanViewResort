<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, Model.Reservation, DAO.ReservationDao, java.sql.Connection, DB.DBConnect" %>
<%
    ReservationDao reservationDao = new ReservationDao();
    List<Reservation> reservations = null;
    String errorMessage = null;
    String dbStatus = "Unknown";
    try {
        // Test database connection first
        Connection conn = DBConnect.getConnection();
        if (conn != null && !conn.isClosed()) {
            dbStatus = "Connected";
            conn.close();
        } else {
            dbStatus = "Failed to connect";
        }
        
        reservations = reservationDao.getAllReservations();
        request.setAttribute("reservations", reservations);
        System.out.println("PaymentReservationTable: Loaded " + (reservations != null ? reservations.size() : 0) + " reservations");
    } catch (Exception e) {
        errorMessage = e.getMessage();
        System.out.println("Error loading reservations: " + e.getMessage());
        e.printStackTrace();
        reservations = java.util.Collections.emptyList();
    }
%>

<div class="card bg-dark text-white shadow-lg">
    <div class="card-header bg-secondary d-flex flex-column flex-md-row justify-content-between align-items-md-center align-items-start gap-2">
        <div>
            <i class="fas fa-table me-1"></i>
            Reservation Records for Payment
        </div>
        <div class="d-flex gap-2">
        </div>
    </div>
    <div class="card-body">
        <!-- Loading Spinner -->
        <div id="reservation-loading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-2 text-white">Loading reservation data...</p>
        </div>
        
        <div id="reservation-data" class="table-responsive" style="display: none;">
            <table class="table table-dark table-striped table-hover payment-table" id="paymentReservationTable">
                <thead>
                    <tr>
                        <th data-label="ID">ID</th>
                        <th data-label="Guest">Guest</th>
                        <th data-label="Room">Room</th>
                        <th data-label="Check-In">Check-In</th>
                        <th data-label="Check-Out">Check-Out</th>
                        <th data-label="Nights">Nights</th>
                        <th data-label="Amount">Amount</th>
                        <th data-label="Status">Status</th>
                        <th data-label="Actions">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                    if (errorMessage != null) {
                    %>
                    <tr>
                        <td colspan="9" class="text-center text-danger">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            Error loading reservations: <%= errorMessage %><br>
                            <small>DB Status: <%= dbStatus %></small>
                        </td>
                    </tr>
                    <%
                    } else if (reservations != null && !reservations.isEmpty()) {
                        for (Reservation res : reservations) {
                            long nights = (res.getCheckOut().getTime() - res.getCheckIn().getTime()) / (1000 * 60 * 60 * 24);
                            double roomRate = 0.0;
                            switch(res.getRoomType()) {
                                case "Single Room": roomRate = 5000.00; break;
                                case "Double Room": roomRate = 8000.00; break;
                                case "Ocean Suite": roomRate = 15000.00; break;
                            }
                            double totalAmount = nights * roomRate;
                    %>
                    <tr data-reservation-id="<%= res.getId() %>">
                        <td data-label="ID"><%= res.getId() %></td>
                        <td data-label="Guest">
                            <div class="d-none d-md-block"><%= res.getGuestName() %></div>
                            <div class="d-md-none">
                                <div><strong><%= res.getGuestName() %></strong></div>
                            </div>
                        </td>
                        <td data-label="Room" class="d-none d-md-table-cell"><%= res.getRoomType() %></td>
                        <td data-label="Check-In"><%= res.getCheckIn() %></td>
                        <td data-label="Check-Out"><%= res.getCheckOut() %></td>
                        <td data-label="Nights"><%= nights %></td>
                        <td data-label="Amount">Rs. <%= String.format("%.2f", totalAmount) %></td>
                        <td data-label="Status">
                            <%
                                String status = res.getStatus();
                                String badgeClass = "bg-warning";
                                boolean isPaid = "Paid".equals(status);
                                if (isPaid) {
                                    badgeClass = "bg-success";
                                } else if ("Payment Pending".equals(status)) {
                                    badgeClass = "bg-info";
                                }
                            %>
                            <span class="badge <%= badgeClass %>"><%= status != null ? status : "Payment Pending" %></span>
                        </td>
                        <td data-label="Actions">
                            <div class="btn-group btn-group-sm" role="group">
                                <% if (!"Paid".equals(res.getStatus())) { %>
                                    <button class="btn btn-success pay-btn" title="Pay">
                                        <i class="fas fa-credit-card"></i>
                                        <span class="d-none d-md-inline ms-1">Pay</span>
                                    </button>
                                <% } else { %>
                                    <button class="btn btn-success disabled" title="Already Paid" disabled>
                                        <i class="fas fa-check-circle"></i>
                                        <span class="d-none d-md-inline ms-1">Paid</span>
                                    </button>
                                <% } %>
                            </div>
                        </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="9" class="text-center">
                            No reservations found<br>
                            <small class="text-muted">DB Status: <%= dbStatus %> | Count: <%= reservations != null ? reservations.size() : 0 %></small>
                        </td>
                    </tr>
                    <%
                    }
                    %>
                </tbody>
            </table>
        </div>
        
        <script>
            // Hide loading spinner and show data when page loads
            document.addEventListener('DOMContentLoaded', function() {
                console.log('PaymentReservationTable: DOM loaded');
                const loadingDiv = document.getElementById('reservation-loading');
                const dataDiv = document.getElementById('reservation-data');
                
                if (loadingDiv && dataDiv) {
                    // Small delay to show the spinner for better UX
                    setTimeout(function() {
                        loadingDiv.style.display = 'none';
                        dataDiv.style.display = 'block';
                        console.log('PaymentReservationTable: Data displayed');
                    }, 500); // 500ms delay to ensure data is rendered
                } else {
                    console.log('PaymentReservationTable: loadingDiv or dataDiv not found');
                }
            });
            
            // Ensure toast notifications are properly initialized
            if (typeof initializeToastNotifications === 'function') {
                initializeToastNotifications();
            }
        </script>
    </div>
</div>