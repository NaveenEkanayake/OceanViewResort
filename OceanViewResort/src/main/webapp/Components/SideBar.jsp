<nav id="sidebar">
    <div class="sidebar-header">
        <div class="d-flex align-items-center">
            <i class="fa-solid fa-anchor me-2"></i> 
            <strong class="resort-text">Ocean View Resort</strong>
        </div>
        <button id="toggleBtn" class="btn btn-sm"><i class="fa-solid fa-bars-staggered"></i></button>
    </div>

    <%-- Display logged-in employee username --%>
    <%@ page import="jakarta.servlet.http.Cookie" %>
    <div class="profile-area">
        <i class="fa-solid fa-circle-user"></i>
        <div class="profile-info">
            <% 
                // Get username from employeeUser cookie
                String sidebarUsername = null;
                Cookie[] sidebarCookies = request.getCookies();
                if (sidebarCookies != null) {
                    for (Cookie cookie : sidebarCookies) {
                        if ("employeeUser".equals(cookie.getName())) {
                            sidebarUsername = cookie.getValue();
                            break;
                        }
                    }
                }
            %>
            <% if (sidebarUsername != null && !sidebarUsername.isEmpty()) { %>
                <span class="username-text"><%= sidebarUsername %></span>
            <% } else { %>
                <span class="username-text">Guest</span>
            <% } %>
        </div>
    </div>

    <ul class="nav-menu">
        <li>
            <a href="EmployeeDashboard.jsp" class="nav-link-item">
                <i class="fa-solid fa-house"></i>
                <span class="link-text">Home</span>
            </a>
        </li>

        <li>
            <div class="nav-link-item" onclick="toggleSubmenu('resSubmenu', 'resChevron')">
                <i class="fa-solid fa-calendar-check"></i>
                <span class="link-text">Reservation</span>
                <i class="fa-solid fa-chevron-right chevron-icon" id="resChevron"></i>
            </div>
            <ul class="submenu" id="resSubmenu">
                <li>
                    <a href="MakeReservation.jsp" class="nav-link-item sub-link">
                        <i class="fa-solid fa-calendar-plus"></i>
                        <span class="link-text">New Reservation</span>
                    </a>
                </li>
                <li>
                    <a href="ViewReservation.jsp" class="nav-link-item sub-link">
                        <i class="fa-solid fa-rectangle-list"></i>
                        <span class="link-text">View Reservation</span>
                    </a>
                </li>
            </ul>
        </li>

        <li>
            <a href="PaymentProcessing.jsp" class="nav-link-item">
                <i class="fa-solid fa-calculator"></i>
                <span class="link-text">Calculate Bill</span>
            </a>
        </li>
        <li>
            <a href="#" class="nav-link-item">
                <i class="fa-solid fa-circle-question"></i>
                <span class="link-text">Help Section</span>
            </a>
        </li>
    </ul>

    <div class="logout-box">
        <a href="${pageContext.request.contextPath}/LoginServlet?action=logout" class="nav-link-item text-danger">
            <i class="fa-solid fa-right-from-bracket"></i>
            <span class="link-text">Logout</span>
        </a>
    </div>
</nav>