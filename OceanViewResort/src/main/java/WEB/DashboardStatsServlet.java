package WEB;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.Gson;
import DAO.ReservationDao;
import DAO.PaymentDao;
import DAO.EmployeeDAO;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/Pages/DashboardStatsServlet")
public class DashboardStatsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private ReservationDao reservationDao;
    private PaymentDao paymentDao;
    private EmployeeDAO employeeDao;
    
    @Override
    public void init() throws ServletException {
        reservationDao = new ReservationDao();
        paymentDao = new PaymentDao();
        employeeDao = new EmployeeDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Get total reservations
            int totalReservations = reservationDao.getAllReservations().size();
            stats.put("totalReservations", totalReservations);
            
            // Get total employees from employee_login table
            int totalEmployees = employeeDao.getEmployeeCount();
            stats.put("totalEmployees", totalEmployees);
            
            // Get total payments in LKR
            double totalPayments = paymentDao.getTotalRevenue();
            stats.put("totalPayments", totalPayments);
            stats.put("totalPaymentsLKR", "LKR " + String.format("%,.2f", totalPayments));
            
            // Success
            stats.put("success", true);
            
        } catch (Exception e) {
            e.printStackTrace();
            stats.put("success", false);
            stats.put("error", "Failed to fetch dashboard statistics");
        }
        
        PrintWriter out = response.getWriter();
        out.print(new Gson().toJson(stats));
        out.flush();
    }
}