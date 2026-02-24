document.addEventListener('DOMContentLoaded', function() {
    const sidebar = document.getElementById('sidebar');
    const toggleBtn = document.getElementById('toggleBtn');

    // If there is no sidebar on the current page, exit to avoid errors
    if (!sidebar) return;

    // Check saved state
    if (localStorage.getItem('sidebarState') === 'collapsed') {
        sidebar.classList.add('collapsed');
        if (toggleBtn) toggleBtn.classList.add('rotate-icon');
    }

    const openSubmenuId = localStorage.getItem('openSubmenu');
    if (openSubmenuId && !sidebar.classList.contains('collapsed')) {
        const submenu = document.getElementById(openSubmenuId);
        const chevron = document.getElementById(openSubmenuId.replace('Submenu', 'Chevron'));
        if (submenu) submenu.classList.add('show');
        if (chevron) chevron.classList.add('rotated');
    }

    if (toggleBtn) {
        toggleBtn.addEventListener('click', function() {
            sidebar.classList.toggle('collapsed');
            toggleBtn.classList.toggle('rotate-icon');
            
            const isCollapsed = sidebar.classList.contains('collapsed');
            localStorage.setItem('sidebarState', isCollapsed ? 'collapsed' : 'expanded');

            if (isCollapsed) {
                document.querySelectorAll('.submenu').forEach(s => s.classList.remove('show'));
                document.querySelectorAll('.chevron-icon').forEach(c => c.classList.remove('rotated'));
                document.documentElement.classList.remove('sidebar-is-collapsed');
            } else {
                const savedSub = localStorage.getItem('openSubmenu');
                if (savedSub) {
                    const subEl = document.getElementById(savedSub);
                    const chevEl = document.getElementById(savedSub.replace('Submenu', 'Chevron'));
                    if(subEl) subEl.classList.add('show');
                    if(chevEl) chevEl.classList.add('rotated');
                }
            }
        });
    }
});

window.toggleSubmenu = function(submenuId, chevronId) {
    const sidebar = document.getElementById('sidebar');
    if (!sidebar) return;

    if (sidebar.classList.contains('collapsed')) {
        sidebar.classList.remove('collapsed');
        const toggleBtn = document.getElementById('toggleBtn');
        if (toggleBtn) toggleBtn.classList.remove('rotate-icon');
        localStorage.setItem('sidebarState', 'expanded');
    }

    const submenu = document.getElementById(submenuId);
    const chevron = document.getElementById(chevronId);
    if (!submenu) return;

    const isShowing = submenu.classList.contains('show');
    
    document.querySelectorAll('.submenu').forEach(el => el.classList.remove('show'));
    document.querySelectorAll('.chevron-icon').forEach(el => el.classList.remove('rotated'));

    if (!isShowing) {
        submenu.classList.add('show');
        if (chevron) chevron.classList.add('rotated');
        localStorage.setItem('openSubmenu', submenuId);
    } else {
        localStorage.removeItem('openSubmenu');
    }
};