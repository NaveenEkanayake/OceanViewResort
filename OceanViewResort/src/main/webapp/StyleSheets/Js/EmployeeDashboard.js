document.addEventListener('DOMContentLoaded', function() {
    console.log("Employee Dashboard Initialized");
    
    // Fetch and update room status data
    function updateRoomStatus() {
        const basePath = window.location.pathname.includes('/Pages/') ? '../' : '';
        const url = basePath + 'submitReservation?action=getStatus';
        
        console.log("Fetching room status from:", url);
        
        fetch(url)
            .then(response => {
                console.log("Response status:", response.status);
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Received data:", data);
                const singleCount = data.single || 0;
                const singleMax = 20;
                const singlePercentage = (singleCount / singleMax) * 100;
                document.getElementById('singleAvailable').textContent = singleCount;
                document.getElementById('singleBar').style.width = singlePercentage + '%';
                console.log("Single Room: ", singleCount, "occupants, ", singlePercentage, "%");
                
                // Update Double Room status
                const doubleCount = data.double || 0;
                const doubleMax = 15;
                const doublePercentage = (doubleCount / doubleMax) * 100;
                document.getElementById('doubleAvailable').textContent = doubleCount;
                document.getElementById('doubleBar').style.width = doublePercentage + '%';
                console.log("Double Room: ", doubleCount, "occupants, ", doublePercentage, "%");
                
                // Update Ocean Suite status
                const suiteCount = data.suite || 0;
                const suiteMax = 5;
                const suitePercentage = (suiteCount / suiteMax) * 100;
                document.getElementById('suiteAvailable').textContent = suiteCount;
                document.getElementById('suiteBar').style.width = suitePercentage + '%';
                console.log("Ocean Suite: ", suiteCount, "occupants, ", suitePercentage, "%");
                
                console.log("Room status updated successfully");
            })
            .catch(error => {
                console.error('Error fetching room status:', error);
                // Show error in UI
                document.getElementById('singleAvailable').textContent = 'Error';
                document.getElementById('doubleAvailable').textContent = 'Error';
                document.getElementById('suiteAvailable').textContent = 'Error';
            });
    }
    
    // Initial load
    console.log("Initial room status update");
    updateRoomStatus();
    
    // Refresh every 10 seconds for testing (change to 30 seconds in production)
    setInterval(updateRoomStatus, 10000);
    
    // Optional: Add animation delay to cards if they exist in EmployeeDashboardContent.jsp
    const cards = document.querySelectorAll('.room-status-glass-card');
    cards.forEach((card, index) => {
        card.style.animationDelay = (index * 0.1) + 's';
    });

    // Ensure no manual refresh button is created
    // All refresh functionality is handled automatically
});