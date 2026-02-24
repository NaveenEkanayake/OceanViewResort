document.addEventListener('DOMContentLoaded', function() {
    const adminSidebar = document.getElementById('admin-sidebar');
    const adminToggleBtn = document.getElementById('toggleBtn');
    const body = document.body;

    if (!adminSidebar) return;

    // Load saved state
    const savedState = localStorage.getItem('adminSidebarState');
    if (savedState === 'collapsed') {
        body.classList.add('sidebar-is-collapsed');
        if (adminToggleBtn) adminToggleBtn.classList.add('rotate-icon');
    }

    // Handle initial submenu state
    const openSubId = localStorage.getItem('adminOpenSubmenu');
    if (openSubId && !body.classList.contains('sidebar-is-collapsed')) {
        const sub = document.getElementById(openSubId);
        const chev = document.getElementById(openSubId.replace('Submenu', 'Chevron'));
        if (sub) {
            sub.classList.add('show');
            sub.style.maxHeight = '200px';
            sub.style.opacity = '1';
        }
        if (chev) chev.classList.add('rotated');
    }

    // Toggle button click handler
    if (adminToggleBtn) {
        adminToggleBtn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            // Toggle sidebar state
            body.classList.toggle('sidebar-is-collapsed');
            adminToggleBtn.classList.toggle('rotate-icon');
            
            const isCollapsed = body.classList.contains('sidebar-is-collapsed');
            localStorage.setItem('adminSidebarState', isCollapsed ? 'collapsed' : 'expanded');

            // Close all submenus when collapsing
            if (isCollapsed) {
                document.querySelectorAll('.admin-submenu').forEach(s => {
                    s.classList.remove('show');
                    s.style.maxHeight = '0px';
                    s.style.opacity = '0';
                });
                document.querySelectorAll('.admin-chevron').forEach(c => c.classList.remove('rotated'));
                localStorage.removeItem('adminOpenSubmenu');
            }
        });
    }

    // Add keyboard support (optional)
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && !body.classList.contains('sidebar-is-collapsed')) {
            body.classList.add('sidebar-is-collapsed');
            if (adminToggleBtn) adminToggleBtn.classList.add('rotate-icon');
            localStorage.setItem('adminSidebarState', 'collapsed');
            
            // Close submenus
            document.querySelectorAll('.admin-submenu').forEach(s => {
                s.classList.remove('show');
                s.style.maxHeight = '0px';
                s.style.opacity = '0';
            });
            document.querySelectorAll('.admin-chevron').forEach(c => c.classList.remove('rotated'));
            localStorage.removeItem('adminOpenSubmenu');
        }
    });
});

window.toggleSubmenu = function(submenuId, chevronId) {
    const body = document.body;
    const submenu = document.getElementById(submenuId);
    const chevron = document.getElementById(chevronId);

    // Validate elements exist
    if (!submenu || !chevron) {
        console.warn('Submenu or chevron element not found');
        return;
    }

    // Expand sidebar first if it's currently collapsed
    if (body.classList.contains('sidebar-is-collapsed')) {
        body.classList.remove('sidebar-is-collapsed');
        const toggleBtn = document.getElementById('toggleBtn');
        if (toggleBtn) toggleBtn.classList.remove('rotate-icon');
        // Save the expanded state
        localStorage.setItem('adminSidebarState', 'expanded');
    }

    const isShowing = submenu.classList.contains('show');

    // Close other open submenus (Accordion behavior)
    document.querySelectorAll('.admin-submenu').forEach(s => {
        if (s !== submenu && s.classList.contains('show')) {
            s.classList.remove('show');
            s.style.maxHeight = '0px';
            s.style.opacity = '0';
        }
    });
    document.querySelectorAll('.admin-chevron').forEach(c => {
        if (c !== chevron && c.classList.contains('rotated')) {
            c.classList.remove('rotated');
        }
    });

    // Toggle the clicked menu
    if (!isShowing) {
        // Open submenu
        submenu.classList.add('show');
        submenu.style.maxHeight = '200px';
        submenu.style.opacity = '1';
        chevron.classList.add('rotated');
        localStorage.setItem('adminOpenSubmenu', submenuId);
    } else {
        // Close submenu
        submenu.classList.remove('show');
        submenu.style.maxHeight = '0px';
        submenu.style.opacity = '0';
        chevron.classList.remove('rotated');
        localStorage.removeItem('adminOpenSubmenu');
    }
};

