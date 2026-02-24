const maxCapacities = { single: 20, double: 15, suite: 5 };

function updateRoomBars() {
    // Pointing to the Servlet with the 'getStatus' action
    fetch('../submitReservation?action=getStatus')
        .then(response => response.json())
        .then(data => {
            // Updating the numbers and the progress bar width
            updateUI('singleAvailable', 'singleBar', data.single, maxCapacities.single);
            updateUI('doubleAvailable', 'doubleBar', data.double, maxCapacities.double);
            updateUI('suiteAvailable', 'suiteBar', data.suite, maxCapacities.suite);
        })
        .catch(err => console.error("Error fetching room status:", err));
}

function updateUI(textId, barId, current, max) {
    const count = current || 0; // Ensures it shows 0 if no data exists
    const percent = (count / max) * 100;
    
    document.getElementById(textId).innerText = count;
    document.getElementById(barId).style.width = percent + "%";
}

// Initial load
document.addEventListener('DOMContentLoaded', updateRoomBars);