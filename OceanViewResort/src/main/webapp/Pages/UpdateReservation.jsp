<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.Cookie" %>
<%
    // Check if employee is logged in via cookie
    boolean isEmployeeLoggedIn = false;
    String employeeUsername = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("employeeUser".equals(cookie.getName())) {
                isEmployeeLoggedIn = true;
                employeeUsername = cookie.getValue();
                break;
            }
        }
    }
    
    // Redirect to MainPage if not authenticated
    if (!isEmployeeLoggedIn) {
        response.sendRedirect("MainPage.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ocean Resort - Update Reservation</title>
    <meta name="contextPath" content="${pageContext.request.contextPath}">
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>
    
    <link rel="stylesheet" href="../StyleSheets/Css/SideBar.css">
    <link rel="stylesheet" href="../StyleSheets/Css/MakeReservation.css">
</head>

<body>
    <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 1055;">
        <div id="reservationToast" class="toast align-items-center text-white border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body" id="toastMsg"></div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    </div>

    <%@ include file="../Components/SideBar.jsp" %>

    <div class="content-wrapper">
        <div class="container d-flex justify-content-center reservation-container">
            <div class="col-12 col-md-8 col-lg-6 animate__animated animate__fadeInUp">
                <div class="glass-form">
                    <h2 class="text-center mb-4"><i class="fa-solid fa-hotel me-2"></i> Update Reservation Form</h2>
                    
                    <form id="resForm" action="../submitReservation" method="post" class="needs-validation" novalidate>
                        
                        <input type="hidden" name="id" id="resId">

                        <div class="mb-3">
                            <label class="form-label">Guest Name</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fa-solid fa-user"></i></span>
                                <input type="text" class="form-control" name="guestName" id="guestName" placeholder="Enter full name" required>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Address</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fa-solid fa-location-dot"></i></span>
                                <input type="text" class="form-control" name="address" id="address" placeholder="123 Ocean Street" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Contact Number</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fa-solid fa-phone"></i></span>
                                <input type="tel" class="form-control" name="contactNumber" id="contactNumber" placeholder="+94 77 123 4567" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Room Type</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fa-solid fa-bed"></i></span>
                                <select class="form-select" name="roomType" id="roomType" required>
                                    <option selected disabled value="">Choose room type...</option>
                                    <option value="Single Room">Single Room</option>
                                    <option value="Double Room">Double Room</option>
                                    <option value="Ocean Suite">Ocean Suite</option>
                                </select>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Check-In</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fa-solid fa-calendar-plus"></i></span>
                                    <input type="date" class="form-control" name="checkIn" id="checkIn" required>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Check-Out</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fa-solid fa-calendar-minus"></i></span>
                                    <input type="date" class="form-control" name="checkOut" id="checkOut" required>
                                </div>
                            </div>
                        </div>

                        <button type="submit" id="submitBtn" class="btn btn-primary w-100 mt-3 py-2 shadow-sm d-flex justify-content-center align-items-center">
                            <span id="btnText">Update Reservation</span>
                            <div id="btnSpinner" class="spinner-border spinner-border-sm ms-2 d-none" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="../StyleSheets/Js/Sidebar.js"></script>
    <script src="../StyleSheets/Js/MakeReservation.js"></script>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Extracts parameters from the URL (passed from ViewReservations.js)
            const params = new URLSearchParams(window.location.search);
            
            if (params.has('id')) {
                // Populate the hidden ID field for the Servlet update logic
                const resIdField = document.getElementById('resId');
                if(resIdField) resIdField.value = params.get('id');

                // Helper to safely populate fields by ID or name selector
                const setField = (selector, param) => {
                    const el = document.getElementById(selector) || document.querySelector(`[name="${selector}"]`);
                    if (el) el.value = params.get(param) || '';
                };

                setField('guestName', 'guestName');
                setField('address', 'address');
                setField('contactNumber', 'contactNumber');
                setField('roomType', 'roomType');
                setField('checkIn', 'checkIn');
                setField('checkOut', 'checkOut');
            }
        });
    </script>
</body>
</html>