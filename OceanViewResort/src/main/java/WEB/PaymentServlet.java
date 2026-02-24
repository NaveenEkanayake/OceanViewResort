package WEB;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;
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
                    List<Payment> payments = paymentDao.getAllPayments();
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
                    getPaymentChartData(timeframe, response);
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
        try {
            int reservationId = Integer.parseInt(request.getParameter("reservationId"));
            Reservation reservation = reservationDao.getReservationById(reservationId);
            
            if (reservation == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Reservation not found");
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

            boolean success = paymentDao.addPayment(payment);
            
            if (success) {
                reservationDao.updateReservationStatus(reservationId, "Paid");
                EmailUtil.sendHTMLPaymentConfirmationEmail(
                    payment.getGuestEmail(), payment.getId(), payment.getGuestName(),
                    payment.getRoomType(), payment.getCheckIn(), payment.getCheckOut(),
                    payment.getTotalAmount(), payment.getPaymentMethod()
                );
                
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(new PaymentResponse(true, "Payment processed successfully", payment)));
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
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
    
    private void getPaymentChartData(String timeframe, HttpServletResponse response) throws IOException {
        try {
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
            
            // Now populate with actual data from database
            populateChartDataWithActualData(chartData, timeframe);
            
            response.getWriter().write(gson.toJson(chartData));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error getting chart data: " + e.getMessage());
        }
    }
    
    private void populateChartDataWithActualData(ChartDataResult chartData, String timeframe) {
        try (java.sql.Connection conn = DB.DBConnect.getConnection()) {
            String sql = "";
            switch (timeframe.toLowerCase()) {
                case "daily":
                    sql = "SELECT DAYOFWEEK(paymentDate) as dayOfWeek, SUM(totalAmount) as revenue FROM payments WHERE isPaid = true AND YEARWEEK(paymentDate, 1) = YEARWEEK(NOW(), 1) GROUP BY DAYOFWEEK(paymentDate) ORDER BY DAYOFWEEK(paymentDate)";
                    break;
                case "weekly":
                    // Get weekly data for current month (Week 1-4 based on day of month)
                    sql = "SELECT FLOOR((DAY(paymentDate)-1)/7) + 1 as weekNum, SUM(totalAmount) as revenue FROM payments WHERE isPaid = true AND YEAR(paymentDate) = YEAR(NOW()) AND MONTH(paymentDate) = MONTH(NOW()) GROUP BY FLOOR((DAY(paymentDate)-1)/7) + 1 ORDER BY weekNum";
                    break;
                case "monthly":
                    sql = "SELECT MONTH(paymentDate) as monthNum, SUM(totalAmount) as revenue FROM payments WHERE isPaid = true AND YEAR(paymentDate) = YEAR(NOW()) GROUP BY MONTH(paymentDate) ORDER BY monthNum";
                    break;
                case "yearly":
                    sql = "SELECT YEAR(paymentDate) as year, SUM(totalAmount) as revenue FROM payments WHERE isPaid = true GROUP BY YEAR(paymentDate) ORDER BY year LIMIT 4";
                    break;
                default:
                    return;
            }
            
            // Initialize all data values to 0
            java.util.Arrays.fill(chartData.chartData.data, 0.0);
            
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                 java.sql.ResultSet rs = stmt.executeQuery()) {
                
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
}