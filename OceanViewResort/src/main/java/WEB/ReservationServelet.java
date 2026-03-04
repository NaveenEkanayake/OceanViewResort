package WEB;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import Model.Reservation;
import Model.RoomStatus;
import DAO.ReservationDao;

@WebServlet("/submitReservation")
public class ReservationServelet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReservationDao dao;

    @Override
    public void init() {
        dao = new ReservationDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // 1. Handle Status Fetch (For Dashboard Progress Bars)
        if ("getStatus".equals(action)) {
            System.out.println("getStatus action called");
            RoomStatus status = dao.getCurrentRoomStatus();
            
            System.out.println("Room status data - Single: " + status.getSingleUsed() + 
                             ", Double: " + status.getDoubleUsed() + 
                             ", Suite: " + status.getSuiteUsed());
            
            // Generate JSON summary for all three room types
            String json = String.format(
                "{\"single\":%d, \"double\":%d, \"suite\":%d}",
                status.getSingleUsed(), 
                status.getDoubleUsed(), 
                status.getSuiteUsed()
            );

            System.out.println("Sending JSON response: " + json);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
            return;
        }
        
        // 2. Handle Delete logic
        else if ("delete".equals(action)) {
            try {
                String idStr = request.getParameter("id");
                if (idStr != null && !idStr.trim().isEmpty()) {
                    int id = Integer.parseInt(idStr);
                    if (dao.deleteReservation(id)) {
                        // Redirect directly to the JSP file so relative paths stay intact
                        response.sendRedirect("Pages/ViewReservation.jsp?deleted=true");
                        return; 
                    } else {
                        response.sendError(500, "Database deletion failed");
                    }
                } else {
                    response.sendError(400, "Invalid ID: Parameter was empty");
                }
            } catch (Exception e) {
                response.sendError(400, "Invalid ID format");
            }
        }

        // 3. Handle AJAX Fetch Data action (For the Table UI)
        else if ("fetchData".equals(action)) {
            // Get logged-in employee username
            String employeeUsername = getEmployeeUsernameFromCookie(request);
            
            // Get reservations created by this employee only
            List<Reservation> reservations;
            if (employeeUsername != null) {
                reservations = dao.getReservationsByCreator(employeeUsername);
            } else {
                reservations = new ArrayList<>();
            }
            
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < reservations.size(); i++) {
                Reservation r = reservations.get(i);
                String status = r.getStatus() != null ? r.getStatus() : "Confirmed";
                json.append(String.format(
                    "{\"id\":%d, \"guestName\":\"%s\", \"address\":\"%s\", \"contactNumber\":\"%s\", \"roomType\":\"%s\", \"checkIn\":\"%s\", \"checkOut\":\"%s\", \"status\":\"%s\"}",
                    r.getId(), r.getGuestName(), r.getAddress(), r.getContactNumber(), r.getRoomType(), r.getCheckIn(), r.getCheckOut(), status
                ));
                if (i < reservations.size() - 1) json.append(",");
            }
            json.append("]");

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json.toString());
        } 

        // 4. Default: Forward to the View Page
        else {
            request.getRequestDispatcher("Pages/ViewReservation.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            String roomType = request.getParameter("roomType");
            
            // CAPACITY CHECK: Only run this for NEW reservations
            if (idParam == null || idParam.trim().isEmpty()) {
                int currentBooked = dao.getBookedCount(roomType);
                int maxCapacity = getMaxCapacity(roomType); // Helper method defined below

                if (currentBooked >= maxCapacity) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
                    response.getWriter().write("Error: Fully booked!");
                    return;
                }
            }

            Reservation res = new Reservation(
                request.getParameter("guestName"),
                request.getParameter("address"),
                request.getParameter("contactNumber"),
                roomType,
                Date.valueOf(request.getParameter("checkIn")),
                Date.valueOf(request.getParameter("checkOut"))
            );

            // Get logged-in employee username
            String createdBy = getEmployeeUsernameFromCookie(request);

            boolean success;
            if (idParam != null && !idParam.trim().isEmpty()) {
                res.setId(Integer.parseInt(idParam));
                success = dao.updateReservation(res);
            } else {
                success = dao.addReservation(res, createdBy);
            }

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Success");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database Error");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Data Provided");
        }
    }

    // Missing Helper Method to define room capacities
    private int getMaxCapacity(String type) {
        if ("Single Room".equals(type)) return 20;
        if ("Double Room".equals(type)) return 15;
        if ("Ocean Suite".equals(type)) return 5;
        return 0;
    }
    
    // Helper method to get employee username from cookie
    private String getEmployeeUsernameFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("employeeUser".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}