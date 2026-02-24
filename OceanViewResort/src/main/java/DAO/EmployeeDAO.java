package DAO;

import Model.Employee;
import DB.DBConnect;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    
    // Add new employee with auto-generated credentials
    public boolean addEmployee(Employee employee, String username, String hashedPassword) {
        String employeeQuery = "INSERT INTO employees (firstName, lastName, email, phone, position, salary) VALUES (?, ?, ?, ?, ?, ?)";
        String loginQuery = "INSERT INTO employee_login (employeeId, username, password) VALUES (?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement empStmt = null;
        PreparedStatement loginStmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false); 
            
            empStmt = conn.prepareStatement(employeeQuery, Statement.RETURN_GENERATED_KEYS);
            empStmt.setString(1, employee.getFirstName());
            empStmt.setString(2, employee.getLastName());
            empStmt.setString(3, employee.getEmail());
            empStmt.setString(4, employee.getPhone());
            empStmt.setString(5, employee.getPosition());
            empStmt.setBigDecimal(6, employee.getSalary());
            
            int affectedRows = empStmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Creating employee failed.");
            
            generatedKeys = empStmt.getGeneratedKeys();
            int employeeId;
            if (generatedKeys.next()) {
                employeeId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating employee failed, no ID obtained.");
            }
            
            // Only create login credentials if username and password are provided
            if (username != null && hashedPassword != null) {
                loginStmt = conn.prepareStatement(loginQuery);
                loginStmt.setInt(1, employeeId);
                loginStmt.setString(2, username);
                loginStmt.setString(3, hashedPassword);
                loginStmt.executeUpdate();
            }
            
            conn.commit(); 
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, empStmt, loginStmt, generatedKeys);
        }
    }
    
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT e.*, el.username, el.isActive AS is_active FROM employees e " +
                       "LEFT JOIN employee_login el ON e.employeeId = el.employeeId ORDER BY e.employeeId DESC";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public Employee getEmployeeById(int employeeId) {
        String query = "SELECT e.*, el.username, el.isActive AS is_active FROM employees e " +
                       "LEFT JOIN employee_login el ON e.employeeId = el.employeeId WHERE e.employeeId = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToEmployee(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Employee getEmployeeByEmail(String email) {
        String query = "SELECT e.*, el.username, el.isActive AS is_active FROM employees e " +
                       "LEFT JOIN employee_login el ON e.employeeId = el.employeeId WHERE e.email = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToEmployee(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateEmployee(Employee employee) {
        Connection conn = null;
        PreparedStatement empStmt = null;
        PreparedStatement loginStmt = null;
        
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);
            
            String empQuery = "UPDATE employees SET firstName = ?, lastName = ?, email = ?, phone = ?, position = ?, salary = ? WHERE employeeId = ?";
            empStmt = conn.prepareStatement(empQuery);
            empStmt.setString(1, employee.getFirstName());
            empStmt.setString(2, employee.getLastName());
            empStmt.setString(3, employee.getEmail());
            empStmt.setString(4, employee.getPhone());
            empStmt.setString(5, employee.getPosition());
            empStmt.setBigDecimal(6, employee.getSalary());
            empStmt.setInt(7, employee.getEmployeeId());
            empStmt.executeUpdate();
            
            if (employee.getUsername() != null && !employee.getUsername().isEmpty()) {
                String loginQuery = "UPDATE employee_login SET username = ?, isActive = ? WHERE employeeId = ?";
                loginStmt = conn.prepareStatement(loginQuery);
                loginStmt.setString(1, employee.getUsername());
                loginStmt.setBoolean(2, employee.isActive());
                loginStmt.setInt(3, employee.getEmployeeId());
                loginStmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, empStmt, loginStmt, null);
        }
    }

    public boolean hardDeleteEmployee(int employeeId) {
        System.out.println("=== hardDeleteEmployee START ===");
        System.out.println("Attempting to delete employee with ID: " + employeeId);
        
        Connection conn = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        try {
            System.out.println("Getting database connection");
            conn = DBConnect.getConnection();
            if (conn == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            System.out.println("Database connection successful");
            
            conn.setAutoCommit(false);
            
            // First delete from employee_login table
            stmt1 = conn.prepareStatement("DELETE FROM employee_login WHERE employeeId = ?");
            stmt1.setInt(1, employeeId);
            int loginResult = stmt1.executeUpdate();
            System.out.println("Deleted " + loginResult + " rows from employee_login for employeeId: " + employeeId);
            
            // Then delete from employees table
            stmt2 = conn.prepareStatement("DELETE FROM employees WHERE employeeId = ?");
            stmt2.setInt(1, employeeId);
            int result = stmt2.executeUpdate();
            System.out.println("Deleted " + result + " rows from employees for employeeId: " + employeeId);
            
            conn.commit();
            System.out.println("Delete transaction committed successfully");
            return result > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error in hardDeleteEmployee: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try { 
                    conn.rollback(); 
                    System.out.println("Transaction rolled back");
                } catch (SQLException ex) { 
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                    ex.printStackTrace(); 
                }
            }
            return false;
        } finally {
            System.out.println("Closing resources");
            closeResources(conn, stmt1, stmt2, null);
            System.out.println("=== hardDeleteEmployee END ===");
        }
    }

    public boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM employees WHERE email = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean isUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM employee_login WHERE username = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // NEW FUNCTION: Get total employee count
    public int getEmployeeCount() {
        String query = "SELECT COUNT(*) FROM employees";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employeeId"));
        employee.setFirstName(rs.getString("firstName"));
        employee.setLastName(rs.getString("lastName"));
        employee.setEmail(rs.getString("email"));
        employee.setPhone(rs.getString("phone"));
        employee.setPosition(rs.getString("position"));
        employee.setSalary(rs.getBigDecimal("salary"));
        employee.setHireDate(rs.getTimestamp("hireDate"));
        employee.setStatus(rs.getString("status"));
        
        // Handle optional login fields
        employee.setUsername(rs.getString("username"));
        employee.setActive(rs.getBoolean("is_active")); 
        return employee;
    }

    private void closeResources(Connection conn, Statement s1, Statement s2, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (s1 != null) s1.close();
            if (s2 != null) s2.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}