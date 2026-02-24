<div class="container-fluid">
    <!-- Stats Overview -->
    <div class="row mb-4">
        <div class="col-xl-4 col-md-6 mb-4">
            <div class="stat-card bg-primary text-white">
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h3 id="total-reservations">0</h3>
                            <p class="mb-0">Total Reservations</p>
                        </div>
                        <div class="align-self-center">
                            <i class="fa-solid fa-calendar-check display-4 opacity-75"></i>
                        </div>
                    </div>
                    <div class="progress mt-3" style="height: 5px;">
                        <div class="progress-bar bg-light" id="reservation-progress" style="width: 0%;"></div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-xl-4 col-md-6 mb-4">
            <div class="stat-card bg-success text-white">
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h3 id="total-employees">0</h3>
                            <p class="mb-0">Registered Employees</p>
                        </div>
                        <div class="align-self-center">
                            <i class="fa-solid fa-users display-4 opacity-75"></i>
                        </div>
                    </div>
                    <div class="progress mt-3" style="height: 5px;">
                        <div class="progress-bar bg-light" id="employee-progress" style="width: 0%;"></div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-xl-4 col-md-6 mb-4">
            <div class="stat-card bg-warning text-dark">
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h3 id="total-payments">$0</h3>
                            <p class="mb-0">Total Payments</p>
                        </div>
                        <div class="align-self-center">
                            <i class="fa-solid fa-credit-card display-4 opacity-75"></i>
                        </div>
                    </div>
                    <div class="progress mt-3" style="height: 5px;">
                        <div class="progress-bar bg-dark" id="payment-progress" style="width: 0%;"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Payment History Chart -->
    <div class="card">
        <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Payment History</h5>
            <div class="btn-group" role="group">
                <button type="button" class="btn btn-outline-light btn-sm active" onclick="filterPayments('weekly')">Weekly</button>
                <button type="button" class="btn btn-outline-light btn-sm" onclick="filterPayments('monthly')">Monthly</button>
                <button type="button" class="btn btn-outline-light btn-sm" onclick="filterPayments('yearly')">Yearly</button>
            </div>
        </div>
        <div class="card-body">
            <canvas id="paymentChart" height="200"></canvas>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    // Sample data for the charts
    let weeklyData = {
        labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
        datasets: [{
            label: 'Payment Amount (LKR)',
            data: [24000, 38000, 30000, 36000, 44000, 70000, 56000],
            backgroundColor: 'rgba(54, 162, 235, 0.2)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 2,
            tension: 0.4
        }]
    };

    let monthlyData = {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
        datasets: [{
            label: 'Payment Amount (LKR)',
            data: [240000, 300000, 360000, 280000, 440000, 500000, 560000, 480000, 400000, 360000, 440000, 520000],
            backgroundColor: 'rgba(54, 162, 235, 0.2)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 2,
            tension: 0.4
        }]
    };

    let yearlyData = {
        labels: ['2022', '2023', '2024'],
        datasets: [{
            label: 'Payment Amount (LKR)',
            data: [3600000, 4400000, 5000000],
            backgroundColor: 'rgba(54, 162, 235, 0.2)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 2,
            tension: 0.4
        }]
    };

    let currentFilter = 'weekly';
    let paymentChart;

    // Initialize the chart
    document.addEventListener('DOMContentLoaded', function() {
        initializeCharts();
        loadStats();
    });

    function initializeCharts() {
        const ctx = document.getElementById('paymentChart').getContext('2d');
        paymentChart = new Chart(ctx, {
            type: 'line',
            data: weeklyData,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        labels: {
                            color: '#333'
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.1)'
                        },
                        ticks: {
                            color: '#333',
                            callback: function(value) {
                                return 'LKR ' + value.toLocaleString();
                            }
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        },
                        ticks: {
                            color: '#333'
                        }
                    }
                }
            }
        });
        
        // Load real data from server
        loadChartData('weekly');
    }
    
    function loadChartData(timeframe) {
        fetch('${pageContext.request.contextPath}/PaymentServlet?action=getPaymentStats&timeframe=' + timeframe)
            .then(response => response.json())
            .then(data => {
                if (data.success && data.chartData) {
                    // Update the chart data object
                    const newData = {
                        labels: data.chartData.labels,
                        datasets: [{
                            label: data.chartData.label,
                            data: data.chartData.data,
                            backgroundColor: 'rgba(54, 162, 235, 0.2)',
                            borderColor: 'rgba(54, 162, 235, 1)',
                            borderWidth: 2,
                            tension: 0.4
                        }]
                    };
                    
                    // Update the stored data objects based on current filter
                    if (timeframe === 'weekly') {
                        weeklyData = newData;
                    } else if (timeframe === 'monthly') {
                        monthlyData = newData;
                    } else if (timeframe === 'yearly') {
                        yearlyData = newData;
                    }
                    
                    // Update the chart
                    paymentChart.data = newData;
                    paymentChart.update();
                }
            })
            .catch(error => {
                console.error('Error loading chart data:', error);
            });
    }

    function filterPayments(filter) {
        // Update active button
        document.querySelectorAll('.btn-group .btn').forEach(btn => {
            btn.classList.remove('active');
        });
        event.target.classList.add('active');

        // Load real data from server for the selected filter
        loadChartData(filter);
        currentFilter = filter;
    }

    function loadStats() {
        // Fetch real data from the server
        fetch('${pageContext.request.contextPath}/Pages/DashboardStatsServlet')
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Format numbers with commas
                    document.getElementById('total-reservations').textContent = 
                        data.totalReservations.toLocaleString();
                    document.getElementById('total-employees').textContent = 
                        data.totalEmployees.toLocaleString();
                    document.getElementById('total-payments').textContent = 
                        data.totalPaymentsLKR;
                    
                    // Animate progress bars based on actual data
                    const maxReservations = Math.max(100, data.totalReservations);
                    const maxEmployees = Math.max(50, data.totalEmployees);
                    const maxPayments = Math.max(1000000, data.totalPayments);
                    
                    animateProgressBar('reservation-progress', Math.min(100, (data.totalReservations / maxReservations) * 100));
                    animateProgressBar('employee-progress', Math.min(100, (data.totalEmployees / maxEmployees) * 100));
                    animateProgressBar('payment-progress', Math.min(100, (data.totalPayments / maxPayments) * 100));
                } else {
                    console.error('Failed to load stats:', data.error);
                    // Show error state
                    document.getElementById('total-reservations').textContent = 'Error';
                    document.getElementById('total-employees').textContent = 'Error';
                    document.getElementById('total-payments').textContent = 'Error';
                }
            })
            .catch(error => {
                console.error('Error fetching stats:', error);
                // Show error state
                document.getElementById('total-reservations').textContent = 'Error';
                document.getElementById('total-employees').textContent = 'Error';
                document.getElementById('total-payments').textContent = 'Error';
            });
    }

    function animateProgressBar(progressBarId, targetWidth) {
        let width = 0;
        const progressBar = document.getElementById(progressBarId);
        const interval = setInterval(() => {
            if (width >= targetWidth) {
                clearInterval(interval);
            } else {
                width++;
                progressBar.style.width = width + '%';
            }
        }, 20);
    }
</script>