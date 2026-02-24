<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Employee - Ocean View Resort</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/AdminSidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/AddEmployee.css">
</head>

<body>
    <%@ include file="../Components/AdminSidebar.jsp" %>

    <div class="ae-viewport-wrapper">
        <main>
            <div class="container-fluid px-3 px-md-4 pt-4">
                <div class="mb-4">
                    <h1 class="ae-page-title text-white mb-1">Add New Employee</h1>
                    <p class="text-white-50 small">Fill in the information below to register a new staff member into the management system.</p>
                </div>
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
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        <%= warning %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>
                
                <div class="ae-form-container">
                    <div class="ae-form-header">
                        <i class="fas fa-user-plus me-2"></i> Employee Details
                    </div>
                    <div class="ae-form-body">
                        <form action="${pageContext.request.contextPath}/Pages/EmployeeServlet" method="POST">
                            <input type="hidden" name="action" value="add">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="ae-form-label">First Name</label>
                                    <input type="text" class="ae-form-input" name="firstName" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="ae-form-label">Last Name</label>
                                    <input type="text" class="ae-form-input" name="lastName" required>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label class="ae-form-label">Email Address</label>
                                <input type="email" class="ae-form-input" name="email" required>
                            </div>
                            
                            <div class="mb-3">
                                <label class="ae-form-label">Phone Number</label>
                                <input type="tel" class="ae-form-input" name="phone" required>
                            </div>
                            
                            <div class="mb-3">
                                <label class="ae-form-label">Position</label>
                                <select class="ae-form-input ae-custom-select" name="position" required>
                                    <option value="">Select Position</option>
                                    <option value="Manager">Manager</option>
                                    <option value="Receptionist">Receptionist</option>
                                    <option value="Housekeeping">Housekeeping</option>
                                    <option value="Security">Security</option>
                                </select>
                            </div>
                            
                            <div class="mb-4">
                                <label class="ae-form-label">Salary</label>
                                <div class="d-flex align-items-center">
                                    <span class="me-2 text-white">LKR</span>
                                    <input type="number" class="ae-form-input flex-grow-1" name="salary" placeholder="Enter salary amount" required>
                                </div>
                            </div>
                            
                            <div class="ae-button-group">
                                <button type="submit" class="ae-navy-btn">
                                    <span class="btn-text">Add Employee</span>
                                </button>
                                <a href="ViewEmployee.jsp" class="ae-cancel-link text-center d-block mt-2">Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/StyleSheets/Js/AddEmployee.js"></script>
    <script src="${pageContext.request.contextPath}/StyleSheets/Js/AdminSidebar.js"></script>
</body>
</html>