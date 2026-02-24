document.addEventListener('DOMContentLoaded', function() {
    const sidebar = document.getElementById('msb-sidebar');
    const toggleBtn = document.getElementById('msb-toggleBtn');

    if (!sidebar || !toggleBtn) return;

    // Restore saved state
    if (localStorage.getItem('msbState') === 'collapsed') {
        sidebar.classList.add('msb-collapsed');
        toggleBtn.classList.add('msb-rotate');
    }

    // Toggle Sidebar
    toggleBtn.addEventListener('click', function() {
        sidebar.classList.toggle('msb-collapsed');
        toggleBtn.classList.toggle('msb-rotate');
        
        const isCollapsed = sidebar.classList.contains('msb-collapsed');
        localStorage.setItem('msbState', isCollapsed ? 'collapsed' : 'expanded');
    });
});