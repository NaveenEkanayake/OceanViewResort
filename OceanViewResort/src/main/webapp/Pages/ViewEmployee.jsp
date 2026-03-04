<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.Cookie" %>
<%
    // Check if admin is logged in via cookie
    boolean isAdminLoggedIn = false;
    String adminUsername = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("adminUser".equals(cookie.getName())) {
                isAdminLoggedIn = true;
                adminUsername = cookie.getValue();
                break;
            }
        }
    }
    if (!isAdminLoggedIn) {
        response.sendRedirect("AdminLogin.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="contextPath" content="${pageContext.request.contextPath}">
    <title>Employee Table - Ocean View Resort</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/AdminSidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/ViewEmployees.css">
    <style>
        /* Responsive styles for employee table */
        @media (max-width: 767px) {
            .resort-admin-table-row td {
                font-size: 0.85rem;
            }
        }
        @media (max-width: 576px) {
            .resort-admin-table-row td {
                font-size: 0.8rem;
                padding: 0.5rem !important;
            }
        }
        /* Salary column styling */
        .salary-cell {
            font-weight: 500;
            color: #28a745;
        }
        /* Phone column styling */
        .phone-cell {
            font-family: 'Courier New', monospace;
        }
    </style>
</head>

<body>
    <%@ include file="../Components/AdminSidebar.jsp" %>

    <div class="resort-admin-content-viewport">
        <main>
            <div class="container-fluid px-3 px-md-4 pt-5">
                <!-- Display messages -->
                <%
                    String error = request.getParameter("error");
                    String success = request.getParameter("success");
                    String warning = request.getParameter("warning");
                    if (error != null) {
                %>
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i>
                        <%= error %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } else if (success != null) { %>
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>
                        <%= success %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } else if (warning != null) { %>
                    <div class="alert alert-warning alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        <%= warning %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>
                
                <div class="resort-admin-table-container">
                    <div class="table-responsive">
                        <table class="table resort-admin-custom-table align-middle">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Employee</th>
                                    <th class="d-none d-lg-table-cell">Contact</th>
                                    <th class="d-none d-md-table-cell">Phone</th>
                                    <th class="d-none d-sm-table-cell">Position</th>
                                    <th class="d-none d-xl-table-cell">Salary</th>
                                    <th>Username</th>
                                    <th>Status</th>
                                    <th class="text-end">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    java.util.List<Model.Employee> employees = (java.util.List<Model.Employee>) request.getAttribute("employees");
                                    if (employees != null && !employees.isEmpty()) {
                                        for (Model.Employee emp : employees) {
                                            String statusClass = "ACTIVE".equals(emp.getStatus()) ? "pill-active" : "pill-inactive";
                                            String statusText = "ACTIVE".equals(emp.getStatus()) ? "Active" : "Inactive";
                                            String initials = emp.getFirstName().substring(0, 1).toUpperCase() + 
                                                           emp.getLastName().substring(0, 1).toUpperCase();
                                %>
                                <tr class="resort-admin-table-row" data-employee-id="<%= emp.getEmployeeId() %>">
                                    <td><%= util.CredentialGenerator.generateEmployeeId(emp.getEmployeeId()) %></td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <div class="resort-admin-user-avatar"><%= initials %></div>
                                            <div class="ms-2">
                                                <div class="fw-bold text-white"><%= emp.getFullName() %></div>
                                                <small class="resort-admin-mobile-meta d-lg-none"><%= emp.getEmail() %></small>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="d-none d-lg-table-cell resort-admin-text-white"><%= emp.getEmail() %></td>
                                    <td class="d-none d-md-table-cell resort-admin-text-white phone-cell"><%= emp.getPhone() != null ? emp.getPhone() : "N/A" %></td>
                                    <td class="d-none d-sm-table-cell resort-admin-text-white"><%= emp.getPosition() %></td>
                                    <td class="d-none d-xl-table-cell resort-admin-text-white salary-cell"><%= emp.getSalary() != null ? "LKR " + String.format("%.2f", emp.getSalary()) : "N/A" %></td>
                                    <td class="resort-admin-text-white"><%= emp.getUsername() != null ? emp.getUsername() : "N/A" %></td>
                                    <td><span class="resort-admin-status-pill <%= statusClass %>"><%= statusText %></span></td>
                                    <td class="text-end">
                                        <a href="${pageContext.request.contextPath}/Pages/EmployeeServlet?action=edit&id=<%= emp.getEmployeeId() %>" class="resort-admin-action-btn btn-edit">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <button type="button" 
                                                class="resort-admin-action-btn btn-delete" 
                                                data-employee-id="<%= emp.getEmployeeId() %>"
                                                data-employee-name="<%= emp.getFullName() %>">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                                <% 
                                        }
                                    } else {
                                %>
                                <tr>
                                    <td colspan="9" class="text-center py-5">
                                        <div class="text-white-50">
                                            <i class="fas fa-users fa-3x mb-3"></i>
                                            <h4>No Employees Found</h4>
                                            <p>There are no employees in the system yet.</p>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteEmployeeModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content glass-modal bg-dark text-white border-0 shadow-lg">
                <div class="modal-body p-5 text-center">
                    <div class="mb-4">
                        <i class="fa-solid fa-circle-exclamation text-danger animate__animated animate__pulse animate__infinite" style="font-size: 4rem;"></i>
                    </div>
                    <h4 class="fw-bold">Delete Employee?</h4>
                    <p class="text-white-50 mb-4">Are you sure you want to delete <span id="employeeName" class="fw-bold text-white"></span>? This will permanently remove all their data and login credentials from the system.</p>
                    <div class="d-flex justify-content-center gap-3">
                        <button type="button" class="btn btn-link text-white text-decoration-none px-4" data-bs-dismiss="modal">Cancel</button>
                        <button id="confirmDeleteBtn" class="btn btn-danger px-5 rounded-pill fw-bold shadow-sm">Delete Employee</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Toast Container -->
    <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 2000;">
        <div id="employeeToast" class="toast align-items-center text-white border-0 shadow-lg" role="alert" aria-live="assertive" aria-atomic="true">
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
    <script src="${pageContext.request.contextPath}/StyleSheets/Js/ViewEmployee.js"></script>
    <script src="${pageContext.request.contextPath}/StyleSheets/Js/AdminSidebar.js"></script>
</body>
</html>