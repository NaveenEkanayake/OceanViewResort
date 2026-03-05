package WEB;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import Model.Payment;
import Model.Reservation;
import DAO.PaymentDao;
import DAO.ReservationDao;
import util.EmailUtil;

@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PaymentDao paymentDao;
    private ReservationDao reservationDao;
    private Gson gson;

    @Override
    public void init() {
        paymentDao = new PaymentDao();
        reservationDao = new ReservationDao();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // New synchronization logic for fetching table data
            if ("fetchData".equals(action)) {
                List<Reservation> reservations = reservationDao.getAllReservations();
                
                // Sync payment status for the JSON response
                for (Reservation res : reservations) {
                    List<Payment> payments = paymentDao.getPaymentsByReservationId(res.getId());
                    if (payments != null && payments.stream().anyMatch(Payment::isPaid)) {
                        res.setStatus("Paid");
                    }
                }
                response.getWriter().write(gson.toJson(reservations));
                return; // Exit early as we've handled the custom action
            }

            // Existing standard actions
            switch (action) {
                case "getAll":
                    // Get logged-in employee username
                    String employeeUsername = getEmployeeUsernameFromCookie(request);
                    List<Payment> payments;
                    if (employeeUsername != null) {
                        payments = paymentDao.getPaymentsByCreator(employeeUsername);
                    } else {
                        payments = new ArrayList<>();
                    }
                    response.getWriter().write(gson.toJson(payments));
                    break;
                    
                case "getById":
                    int id = Integer.parseInt(request.getParameter("id"));
                    Payment payment = paymentDao.getPaymentById(id);
                    response.getWriter().write(gson.toJson(payment));
                    break;
                    
                case "getByReservation":
                    int reservationId = Integer.parseInt(request.getParameter("reservationId"));
                    List<Payment> reservationPayments = paymentDao.getPaymentsByReservationId(reservationId);
                    response.getWriter().write(gson.toJson(reservationPayments));
                    break;
                    
                case "getStats":
                    PaymentDao.PaymentStats stats = paymentDao.getPaymentStats();
                    response.getWriter().write(gson.toJson(stats));
                    break;
                                
                case "getPaymentStats":
                    String timeframe = request.getParameter("timeframe");
                    getPaymentChartData(request, timeframe, response);
                    break;
                                
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                    break;
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            switch (action) {
                case "processPayment":
                    processPayment(request, response);
                    break;
                case "addExtraNights":
                    addExtraNights(request, response);
                    break;
                case "addExtraCharges":
                    addExtraCharges(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                    break;
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
        }
    }

    private void processPayment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            int reservationId = Integer.parseInt(request.getParameter("reservationId"));
            Reservation reservation = reservationDao.getReservationById(reservationId);
            
            if (reservation == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(new PaymentResponse(false, "Reservation not found", null)));
                return;
            }
            
            Payment payment = new Payment();
            payment.setReservationId(reservationId);
            payment.setGuestName(reservation.getGuestName());
            payment.setGuestEmail(request.getParameter("guestEmail"));
            payment.setRoomType(reservation.getRoomType());
            payment.setCheckIn(reservation.getCheckIn().toString());
            payment.setCheckOut(reservation.getCheckOut().toString());
            payment.setPaymentMethod(request.getParameter("paymentMethod"));
            payment.setPaymentDate(new java.sql.Timestamp(System.currentTimeMillis()));
            payment.setPaid(true); 
            
            if ("Card".equals(request.getParameter("paymentMethod"))) {
                String cardNumber = request.getParameter("cardNumber");
                if (cardNumber != null && cardNumber.length() >= 4) {
                    String lastDigits = cardNumber.replaceAll("\\s+", "").substring(cardNumber.length() - 4);
                    payment.setCardLastDigits(lastDigits);
                }
            }

            double roomRate = Payment.getRoomRate(reservation.getRoomType());
            long nights = (reservation.getCheckOut().getTime() - reservation.getCheckIn().getTime()) / (1000 * 60 * 60 * 24);
            
            payment.setNightsStayed((int) nights);
            payment.setRoomRatePerNight(roomRate);
            payment.setRoomTotal(nights * roomRate);
            payment.setTotalAmount(nights * roomRate);

            if (request.getParameter("extraCharges") != null) {
                payment.addExtraCharges(Double.parseDouble(request.getParameter("extraCharges")));
            }
            if (request.getParameter("extraNights") != null) {
                payment.addExtraNights(Integer.parseInt(request.getParameter("extraNights")));
            }

            // Get logged-in employee username
            String createdBy = getEmployeeUsernameFromCookie(request);
            
            // Validate that we have an employee username
            if (createdBy == null || createdBy.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(new PaymentResponse(false, "Employee not authenticated. Please login again.", null)));
                return;
            }
            
            boolean success = paymentDao.addPayment(payment, createdBy);
            
            if (success) {
                reservationDao.updateReservationStatus(reservationId, "Paid");
                EmailUtil.sendHTMLPaymentConfirmationEmail(
                    payment.getGuestEmail(), payment.getId(), payment.getGuestName(),
                    payment.getRoomType(), payment.getCheckIn(), payment.getCheckOut(),
                    payment.getTotalAmount(), payment.getPaymentMethod()
                );
                
                response.getWriter().write(gson.toJson(new PaymentResponse(true, "Payment processed successfully", payment)));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(new PaymentResponse(false, "Failed to process payment. Please check database connection.", null)));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new PaymentResponse(false, "Invalid payment data: " + e.getMessage(), null)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new PaymentResponse(false, "Error: " + e.getMessage(), null)));
        }
    }

    private void addExtraNights(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int paymentId = Integer.parseInt(request.getParameter("paymentId"));
            int extraNights = Integer.parseInt(request.getParameter("extraNights"));
            Payment payment = paymentDao.getPaymentById(paymentId);
            if (payment != null) {
                payment.addExtraNights(extraNights);
                paymentDao.updatePayment(payment);
                response.getWriter().write(gson.toJson(new PaymentResponse(true, "Success", payment)));
            }
        } catch (Exception e) {
            response.sendError(500, e.getMessage());
        }
    }

    private void addExtraCharges(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int paymentId = Integer.parseInt(request.getParameter("paymentId"));
            double extraCharges = Double.parseDouble(request.getParameter("extraCharges"));
            Payment payment = paymentDao.getPaymentById(paymentId);
            if (payment != null) {
                payment.addExtraCharges(extraCharges);
                paymentDao.updatePayment(payment);
                response.getWriter().write(gson.toJson(new PaymentResponse(true, "Success", payment)));
            }
        } catch (Exception e) {
            response.sendError(500, e.getMessage());
        }
    }

    private class PaymentResponse {
        private boolean success;
        private String message;
        private Payment payment;
        public PaymentResponse(boolean success, String message, Payment payment) {
            this.success = success;
            this.message = message;
            this.payment = payment;
        }
    }
    
    private void getPaymentChartData(HttpServletRequest request, String timeframe, HttpServletResponse response) throws IOException {
        try {
            // Get logged-in employee username
            String employeeUsername = getEmployeeUsernameFromCookie(request);
            
            System.out.println("=== PAYMENT CHART DATA REQUEST ===");
            System.out.println("Timeframe: " + timeframe);
            System.out.println("Employee Username from Cookie: " + employeeUsername);
            
            // Prepare chart data based on timeframe
            ChartDataResult chartData = new ChartDataResult();
            chartData.success = true;
            chartData.chartData = new ChartData();
            
            switch (timeframe.toLowerCase()) {
                case "daily":
                    // DAYOFWEEK returns: 1=Sunday, 2=Monday, ..., 7=Saturday
                    chartData.chartData.labels = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
                    chartData.chartData.data = new double[]{0, 0, 0, 0, 0, 0, 0};
                    chartData.chartData.label = "Daily Revenue (LKR)";
                    break;
                
                case "weekly":
                    chartData.chartData.labels = new String[]{"Week 1", "Week 2", "Week 3", "Week 4"};
                    chartData.chartData.data = new double[]{0, 0, 0, 0};
                    chartData.chartData.label = "Weekly Revenue (LKR)";
                    break;
                
                case "monthly":
                    chartData.chartData.labels = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                                         "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                    chartData.chartData.data = new double[12];
                    chartData.chartData.label = "Monthly Revenue (LKR)";
                    break;
                
                case "yearly":
                    chartData.chartData.labels = new String[]{"2023", "2024", "2025", "2026"};
                    chartData.chartData.data = new double[]{0, 0, 0, 0};
                    chartData.chartData.label = "Yearly Revenue (LKR)";
                    break;
                
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid timeframe");
                    return;
            }
            
            // Now populate with actual data from database for this employee only
            populateChartDataWithActualData(chartData, timeframe, employeeUsername);
            
            response.getWriter().write(gson.toJson(chartData));
        } catch (Exception e) {
            System.err.println("=== PAYMENT CHART DATA ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error getting chart data: " + e.getMessage());
        }
    }
    
    private void populateChartDataWithActualData(ChartDataResult chartData, String timeframe, String employeeUsername) {
        try (java.sql.Connection conn = DB.DBConnect.getConnection()) {
            String sql = "";
            boolean hasEmployeeFilter = employeeUsername != null && !employeeUsername.isEmpty();
            
            System.out.println("Populating chart data - Employee Filter: " + hasEmployeeFilter);
            System.out.println("SQL will filter by created_by: " + (hasEmployeeFilter ? employeeUsername : "NO FILTER - ALL DATA"));
            
            switch (timeframe.toLowerCase()) {
                case "daily":
                    if (hasEmployeeFilter) {
                        sql = "SELECT DAYOFWEEK(paymentDate) as dayOfWeek, SUM(totalAmount) as revenue FROM payments WHERE isPaid = true AND created_by = ? AND YEARWEEK(paymentDate, 1) = YEARWEEK(NOW(), 1) GROUP BY DAYOFWEEK(paymentDate) ORDER BY DAYOFWEEK(paymentDate)";
                    } else {
                        sql = "SELECT DAYOFWEEK(paymentDate) as dayOfWeek, SUM(totalAmount) as revenue FROM payments WHERE isPaid = true AND YEARWEEK(paymentDate, 1) = YEARWEEK(NOW(), 1) GROUP BY DAYOFWEEK(paymentDate) ORDER BY DAYOFWEEK(paymentDate)";
                    }
                    break;
                case "weekly":
                    if (hasEmployeeFilter) {
                        sql = "SELECT FLOOR((DAY(paymentDate)-1)/7) + 1 as weekNum, SUM(totalAmount) as revenue FROM payments WHERE isPaid = true AND created_by = ? AND YEAR(paymentDate) = YEAR(NOW()) AND MONTH(paymentDate) = MONTH(NOW()) GROUP BY FLOOR((DAY(paymentDate)-1)/7) + 1 ORDER BY weekNum";
                    } else {
                        sql = "SELECT FLOOR((DAY(paymentDate)-1)/7) + 1 as weekNum, SUM(totalAmount) as revenue FROM payments WHERE isPaid = true AND YEAR(paymentDate) = YEAR(NOW()) AND MONTH(paymentDate) = MONTH(NOW()) GROUP BY FLOOR((DAY(paymentDate)-1)/7) + 1 ORDER BY weekNum";
                    }
                    break;
                case "monthly":
                    if (hasEmployeeFilter) {
                        sql = "SELECT MONTH(paymentDate) as monthNum, SUM(totalAmount) as revenue FROM payments WHERE isPaid = true AND created_by = ? AND YEAR(paymentDate) = YEAR(NOW()) GROUP BY MONTH(paymentDate) ORDER BY monthNum";
                    } else {
                        sql = "SELECT MONTH(paymentDate) as monthNum, SUM(totalAmount) as revenue FROM payments WHERE isPaid = true AND YEAR(paymentDate) = YEAR(NOW()) GROUP BY MONTH(paymentDate) ORDER BY monthNum";
                    }
                    break;
                case "yearly":
                    if (hasEmployeeFilter) {
                        sql = "SELECT YEAR(paymentDate) as year, SUM(totalAmount) as revenue FROM payments WHERE isPaid = true AND created_by = ? GROUP BY YEAR(paymentDate) ORDER BY year LIMIT 4";
                    } else {
                        sql = "SELECT YEAR(paymentDate) as year, SUM(totalAmount) as revenue FROM payments WHERE isPaid = true GROUP BY YEAR(paymentDate) ORDER BY year LIMIT 4";
                    }
                    break;
                default:
                    return;
            }
            
            // Initialize all data values to 0
            java.util.Arrays.fill(chartData.chartData.data, 0.0);
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Set employee username parameter if filtering
                if (hasEmployeeFilter) {
                    stmt.setString(1, employeeUsername);
                }
                
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    
                    while (rs.next()) {
                    int periodIndex;
                    double revenue = rs.getDouble("revenue");
                    
                    switch (timeframe.toLowerCase()) {
                        case "daily":
                            periodIndex = rs.getInt("dayOfWeek") - 1; // DAYOFWEEK returns 1-7, arrays are 0-indexed
                            if (periodIndex >= 0 && periodIndex < chartData.chartData.data.length) {
                                chartData.chartData.data[periodIndex] = revenue;
                            }
                            break;
                        case "weekly":
                            int weekNum = rs.getInt("weekNum");
                            // weekNum from SQL is 1-4, convert to 0-3 for array index
                            int adjustedWeek = weekNum - 1;
                            if (adjustedWeek >= 0 && adjustedWeek < chartData.chartData.data.length) {
                                chartData.chartData.data[adjustedWeek] = revenue;
                            }
                            break;
                        case "monthly":
                            periodIndex = rs.getInt("monthNum") - 1; // MONTH returns 1-12, arrays are 0-indexed
                            if (periodIndex >= 0 && periodIndex < chartData.chartData.data.length) {
                                chartData.chartData.data[periodIndex] = revenue;
                            }
                            break;
                        case "yearly":
                            int year = rs.getInt("year");
                            int baseYear = 2023;
                            periodIndex = year - baseYear;
                            if (periodIndex >= 0 && periodIndex < chartData.chartData.data.length) {
                                chartData.chartData.data[periodIndex] = revenue;
                            }
                            break;
                    }
                }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static class ChartDataResult {
        boolean success;
        ChartData chartData;
    }
    
    private static class ChartData {
        String[] labels;
        double[] data;
        String label;
    }
    
    // Helper method to get employee username from cookie
    private String getEmployeeUsernameFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        System.out.println("Getting cookies from request...");
        if (cookies != null) {
            System.out.println("Found " + cookies.length + " cookies");
            for (Cookie cookie : cookies) {
                System.out.println("Cookie: " + cookie.getName() + " = " + cookie.getValue());
                if ("employeeUser".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    System.out.println("Found employeeUser cookie: " + value);
                    return value;
                }
            }
        } else {
            System.out.println("No cookies found in request!");
        }
        System.out.println("Returning null for employee username");
        return null;
    }
}