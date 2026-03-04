package WEB;

import Model.Employee;
import DAO.EmployeeDAO;
import util.CredentialGenerator;
import util.EmailUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Pages/EmployeeServlet")
public class EmployeeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EmployeeDAO employeeDAO;
    
    public void init() throws ServletException {
        super.init();
        employeeDAO = new EmployeeDAO();
        System.out.println("EmployeeServlet initialized successfully");
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "list";
        
        switch (action) {
            case "list":
                listEmployees(request, response);
                break;
            case "view":
                viewEmployee(request, response);
                break;
            case "delete":
                deleteEmployee(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            default:
                listEmployees(request, response);
                break;
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "add";
        
        switch (action) {
            case "add":
                addEmployee(request, response);
                break;
            case "update":
                updateEmployee(request, response);
                break;
            case "delete":
                deleteEmployee(request, response);
                break;
            default:
                addEmployee(request, response);
                break;
        }
    }
    
    private void listEmployees(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Get logged-in admin username from cookie
        String adminUsername = getAdminUsernameFromCookie(request);
        
        // Get employees created by this admin only
        List<Employee> employees;
        if (adminUsername != null) {
            employees = employeeDAO.getEmployeesByCreator(adminUsername);
        } else {
            employees = new ArrayList<>();
        }
        request.setAttribute("employees", employees);
        request.getRequestDispatcher("/Pages/ViewEmployee.jsp").forward(request, response);
    }
    
    private void viewEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int employeeId = Integer.parseInt(request.getParameter("id"));
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        
        if (employee != null) {
            request.setAttribute("employee", employee);
            request.getRequestDispatcher("/Pages/ViewEmployeeDetail.jsp").forward(request, response);
        } else {
            response.sendRedirect("EmployeeServlet?action=list&error=Employee not found");
        }
    }

    private void addEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String firstName = request.getParameter("firstName").trim();
            String lastName = request.getParameter("lastName").trim();
            String email = request.getParameter("email").trim();
            String phone = request.getParameter("phone").trim();
            String position = request.getParameter("position").trim();
            BigDecimal salary = new BigDecimal(request.getParameter("salary").trim());
            
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || position.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/Pages/AddEmployee.jsp?error=All fields are required");
                return;
            }
            
            if (employeeDAO.isEmailExists(email)) {
                response.sendRedirect(request.getContextPath() + "/Pages/AddEmployee.jsp?error=Email already exists");
                return;
            }
            
            // Get logged-in admin username from cookie
            String createdBy = getAdminUsernameFromCookie(request);
            
            Employee employee = new Employee(firstName, lastName, email, phone, position, salary);
            
            // Only generate login credentials for Receptionist position
            String username = null;
            String password = null;
            String hashedPassword = null;
            
            if ("Receptionist".equalsIgnoreCase(position)) {
                username = CredentialGenerator.generateUsername(firstName, lastName);
                password = CredentialGenerator.generatePassword();
                hashedPassword = CredentialGenerator.hashPassword(password);
                
                int counter = 1;
                String originalUsername = username;
                while (employeeDAO.isUsernameExists(username)) {
                    username = originalUsername + counter;
                    counter++;
                }
            }
            
            boolean success = employeeDAO.addEmployee(employee, username, hashedPassword, createdBy);
            if (success) {
                // Send welcome email with login credentials ONLY to Receptionist
                if ("Receptionist".equalsIgnoreCase(position)) {
                    String emailContent = createWelcomeEmail(firstName, lastName, username, password);
                    EmailUtil.sendEmail(email, "Welcome to Ocean View Resort - Your Login Credentials", emailContent);
                    request.setAttribute("toastMessage", "Employee added successfully. Credentials sent to employee email.");
                } else {
                    request.setAttribute("toastMessage", "Employee added successfully (no login credentials created for this role)");
                }
                request.setAttribute("toastType", "success");
                listEmployees(request, response);
            } else {
                request.setAttribute("toastMessage", "Failed to add employee.");
                request.setAttribute("toastType", "error");
                request.getRequestDispatcher("/Pages/AddEmployee.jsp").forward(request, response);
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/Pages/AddEmployee.jsp?error=An error occurred.");
        }
    }

    private void updateEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            String firstName = request.getParameter("firstName").trim();
            String lastName = request.getParameter("lastName").trim();
            String email = request.getParameter("email").trim();
            String phone = request.getParameter("phone").trim();
            String position = request.getParameter("position").trim();
            BigDecimal salary = new BigDecimal(request.getParameter("salary").trim());
            
            // Get logged-in admin username
            String adminUsername = getAdminUsernameFromCookie(request);
            
            // Verify this admin created this employee
            Employee existingEmployee = employeeDAO.getEmployeeByIdAndCreator(employeeId, adminUsername);
            if (existingEmployee == null) {
                response.sendRedirect("EmployeeServlet?action=list&error=Employee not found or access denied");
                return;
            }
            
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || position.isEmpty()) {
                response.sendRedirect("EmployeeServlet?action=edit&id=" + employeeId + "&error=All fields are required");
                return;
            }
            
            Employee existingWithEmail = employeeDAO.getEmployeeByEmail(email);
            if (existingWithEmail != null && existingWithEmail.getEmployeeId() != employeeId) {
                response.sendRedirect("EmployeeServlet?action=edit&id=" + employeeId + "&error=Email already exists");
                return;
            }
            
            Employee employee = new Employee(firstName, lastName, email, phone, position, salary);
            employee.setEmployeeId(employeeId);
            
            if (employeeDAO.updateEmployee(employee)) {
                request.setAttribute("toastMessage", "Employee updated successfully");
                request.setAttribute("toastType", "success");
            } else {
                request.setAttribute("toastMessage", "Failed to update employee");
                request.setAttribute("toastType", "error");
            }
            listEmployees(request, response);
        } catch (Exception e) {
            response.sendRedirect("EmployeeServlet?action=list&error=An error occurred");
        }
    }

    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        System.out.println("=== Employee Delete Request START ===");
        System.out.println("Delete employee called with ID: " + request.getParameter("id"));
        System.out.println("Request method: " + request.getMethod());
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Query string: " + request.getQueryString());
        System.out.println("Content type: " + request.getContentType());
        System.out.println("Accept header: " + request.getHeader("Accept"));
        
        try {
            // Always return JSON for AJAX requests
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            System.out.println("=== DELETE EMPLOYEE METHOD START ===");
            System.out.println("About to parse employee ID");
            String idParam = request.getParameter("id");
            System.out.println("ID parameter: " + idParam);
            
            if (idParam == null || idParam.trim().isEmpty()) {
                System.out.println("ID parameter is null or empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                java.io.PrintWriter out = response.getWriter();
                out.print("{\"success\":false,\"message\":\"Employee ID is required\"}");
                out.flush();
                return;
            }
            
            int employeeId = Integer.parseInt(idParam);
            System.out.println("Parsed employee ID: " + employeeId);
            
            System.out.println("About to get employee by ID");
            
            // Get logged-in admin username
            String adminUsername = getAdminUsernameFromCookie(request);
            
            // Only get employee if created by this admin
            Employee employee = employeeDAO.getEmployeeByIdAndCreator(employeeId, adminUsername);
            System.out.println("Got employee: " + (employee != null ? employee.getFullName() : "null"));
            
            if (employee == null) {
                System.out.println("Employee not found with ID: " + employeeId + " or access denied");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                java.io.PrintWriter out = response.getWriter();
                out.print("{\"success\":false,\"message\":\"Employee not found or access denied\"}");
                out.flush();
                return;
            }
            
            String employeeName = employee.getFullName();
            System.out.println("Employee name: " + employeeName);
            
            System.out.println("About to call hardDeleteEmployeeByCreator");
            boolean deleteResult = employeeDAO.hardDeleteEmployeeByCreator(employeeId, adminUsername);
            System.out.println("hardDeleteEmployeeByCreator result: " + deleteResult);
            System.out.println("=== DELETE EMPLOYEE METHOD END ===");
            
            if (deleteResult) {
                // Check if it's an AJAX request
                String contentType = request.getHeader("Content-Type");
                String accept = request.getHeader("Accept");
                boolean isAjax = (contentType != null && contentType.contains("application/json")) || 
                                (accept != null && accept.contains("application/json")) ||
                                "POST".equals(request.getMethod());
                
                if (isAjax) {
                    // Return JSON response for AJAX requests
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    java.io.PrintWriter out = response.getWriter();
                    out.print("{\"success\":true,\"message\":\""+ employeeName + " deleted successfully\"}");
                    out.flush();
                    return;
                } else {
                    // Traditional redirect for non-AJAX requests
                    request.setAttribute("toastMessage", employeeName + " deleted successfully");
                    request.setAttribute("toastType", "success");
                    listEmployees(request, response);
                }
            } else {
                System.out.println("Delete failed, returning error JSON");
                // Always return JSON for delete requests (they're AJAX)
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                java.io.PrintWriter out = response.getWriter();
                out.print("{\"success\":false,\"message\":\"Failed to delete " + employeeName + "\"}");
                out.flush();
                System.out.println("Error JSON sent successfully");
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("=== NUMBER FORMAT EXCEPTION IN DELETE EMPLOYEE ===");
            System.err.println("Invalid employee ID format: " + e.getMessage());
            e.printStackTrace();
            
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            java.io.PrintWriter out = response.getWriter();
            out.print("{\"success\":false,\"message\":\"Invalid employee ID format\"}");
            out.flush();
            return;
        } catch (Exception e) {
            System.err.println("=== EXCEPTION IN DELETE EMPLOYEE ===");
            System.err.println("Error in deleteEmployee: " + e.getMessage());
            e.printStackTrace();
            
            System.out.println("About to send error JSON");
            // Always return JSON for delete requests (they're AJAX)
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            java.io.PrintWriter out = response.getWriter();
            out.print("{\"success\":false,\"message\":\"An error occurred while deleting employee: " + e.getMessage() + "\"}");
            out.flush();
            System.out.println("Error JSON sent successfully from exception handler");
            return;
        }
        
        System.out.println("=== Employee Delete Request END ===");
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int employeeId = Integer.parseInt(request.getParameter("id"));
            
            // Get logged-in admin username
            String adminUsername = getAdminUsernameFromCookie(request);
            
            // Only get employee if created by this admin
            Employee employee = employeeDAO.getEmployeeByIdAndCreator(employeeId, adminUsername);
            
            if (employee != null) {
                // Pass the entire object to the JSP
                request.setAttribute("employee", employee);
                request.getRequestDispatcher("/Pages/EditEmployee.jsp").forward(request, response);
            } else {
                response.sendRedirect("EmployeeServlet?action=list&error=Employee not found or access denied");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("EmployeeServlet?action=list&error=Invalid ID format");
        }
    }
    
    // Helper method to get admin username from cookie
    private String getAdminUsernameFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("adminUser".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String createWelcomeEmail(String firstName, String lastName, String username, String password) {
        return "<html><body><h2>Welcome!</h2><p>User: " + username + "<br>Pass: " + password + "</p></body></html>";
    }
}