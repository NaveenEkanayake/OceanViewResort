package WEB;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import DB.DBConnect;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("logout".equals(action)) {
            // Clear employee cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("employeeUser".equals(cookie.getName())) {
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                        break;
                    }
                }
            }
            response.sendRedirect("Pages/MainPage.jsp");
        } else {
            response.sendRedirect("Pages/EmployeeLogin.jsp");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (validateEmployee(username, password)) {
            // Success: Set Cookie
            Cookie employeeCookie = new Cookie("employeeUser", username);
            employeeCookie.setMaxAge(60 * 60); // 1 hour session
            employeeCookie.setPath("/"); 
            response.addCookie(employeeCookie);
            
            response.sendRedirect("Pages/EmployeeLogin.jsp?success=login");
        } else {
            // Fail: Error parameter
            response.sendRedirect("Pages/EmployeeLogin.jsp?error=login_fail");
        }
    }
    
    private boolean validateEmployee(String username, String plainPassword) {
        if (username == null || plainPassword == null || username.isEmpty()) {
            return false; 
        }
        
        String sql = "SELECT password FROM employee_login WHERE username = ? AND isActive = true";
        
        try (Connection con = DBConnect.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPw = rs.getString("password");
                    return BCrypt.checkpw(plainPassword, hashedPw);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; 
    }
}
