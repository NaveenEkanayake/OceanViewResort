<nav id="admin-sidebar" class="admin-sidebar-container">
    <div class="admin-sidebar-header p-3 d-flex align-items-center justify-content-between">
        <div class="d-flex align-items-center admin-brand">
            <i class="fa-solid fa-anchor me-2 text-white admin-icon"></i> 
            <strong class="admin-resort-text text-white">Ocean View Admin</strong>
        </div>
        <button id="toggleBtn" class="btn btn-sm" title="Toggle Sidebar"><i class="fa-solid fa-bars-staggered"></i></button>
    </div>

    <div class="admin-profile-area text-center py-3">
        <i class="fa-solid fa-circle-user fs-1 text-white admin-icon"></i>
        <div class="admin-profile-info mt-2">
            <a href="#" class="admin-edit-profile text-info text-decoration-none small">Edit Profile</a>
        </div>
    </div>

    <ul class="admin-nav-menu list-unstyled flex-grow-1">
        <li class="admin-nav-item">
            <a href="AdminDashboard.jsp" class="admin-link-item">
                <i class="fa-solid fa-house admin-icon"></i>
                <span class="admin-link-text">Home</span>
            </a>
        </li>

        <li class="admin-nav-item">
            <div class="admin-link-item" onclick="toggleSubmenu('empSubmenu', 'empChevron')">
                <i class="fa-solid fa-users-gear admin-icon"></i>
                <span class="admin-link-text">Employee</span>
                <i class="fa-solid fa-chevron-right ms-auto admin-chevron" id="empChevron"></i>
            </div>
            <ul class="admin-submenu list-unstyled" id="empSubmenu">
                <li>
                    <a href="AddEmployee.jsp" class="admin-sub-link">
                        <i class="fa-solid fa-user-plus admin-icon"></i>
                        <span class="admin-link-text">Add Employee</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/Pages/EmployeeServlet?action=list" class="admin-sub-link">
                        <i class="fa-solid fa-rectangle-list admin-icon"></i>
                        <span class="admin-link-text">View Employee</span>
                    </a>
                </li>
            </ul>
        </li>

    <div class="admin-logout-box">
        <a href="logout.jsp" class="admin-link-item text-danger">
            <i class="fa-solid fa-right-from-bracket admin-icon"></i>
            <span class="admin-link-text">Logout</span>
        </a>
    </div>
</nav>