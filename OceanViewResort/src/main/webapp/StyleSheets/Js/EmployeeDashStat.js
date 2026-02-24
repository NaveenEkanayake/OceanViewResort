let paymentChart;

function initChart() {
    const ctx = document.getElementById('paymentChart').getContext('2d');
    
    paymentChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
                label: 'Revenue (LKR)',
                data: [],
                backgroundColor: 'rgba(13, 202, 240, 0.6)',
                borderColor: '#0dcaf0',
                borderWidth: 2,
                borderRadius: 8
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { labels: { color: '#fff' } }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { 
                        color: 'rgba(255,255,255,0.7)',
                        callback: function(value) {
                            return 'Rs. ' + value.toLocaleString();
                        }
                    },
                    grid: { color: 'rgba(255,255,255,0.1)' }
                },
                x: {
                    ticks: { color: 'rgba(255,255,255,0.7)' },
                    grid: { display: false }
                }
            }
        }
    });
    
    // Load initial data
    loadChartData('daily');
}

function loadChartData(timeframe) {
    // Prepare URL based on context path
    let url;
    if (typeof contextPath !== 'undefined' && contextPath) {
        url = contextPath + '/PaymentServlet?action=getPaymentStats&timeframe=' + timeframe;
    } else {
        url = '../PaymentServlet?action=getPaymentStats&timeframe=' + timeframe;
    }
    
    fetch(url, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            updateChartWithData(timeframe, data);
        } else {
            console.error('Error loading chart data:', data.message);
        }
    })
    .catch(error => {
        console.error('Error fetching chart data:', error);
    });
}

function updateChartWithData(timeframe, data) {
    const chartData = data.chartData;
    
    // Update chart data
    let labels = [...chartData.labels];
    let chartDataValues = [...chartData.data];
    
    // For daily view, ensure we have all 7 days of data
    if (timeframe === 'daily') {
        // Server now sends data in correct order: Sun, Mon, Tue, Wed, Thu, Fri, Sat
        // Just ensure we have 7 data points
        if (chartDataValues.length < 7) {
            const paddedData = new Array(7).fill(0);
            for (let i = 0; i < chartDataValues.length; i++) {
                paddedData[i] = chartDataValues[i];
            }
            chartDataValues = paddedData;
        }
    }
    
    paymentChart.data.labels = labels;
    paymentChart.data.datasets[0].data = chartDataValues;
    paymentChart.data.datasets[0].label = chartData.label;
    
    // Change color based on timeframe for variety
    if(timeframe === 'yearly' || timeframe === 'monthly') {
        paymentChart.data.datasets[0].backgroundColor = 'rgba(255, 193, 7, 0.6)';
    } else if(timeframe === 'weekly') {
        paymentChart.data.datasets[0].backgroundColor = 'rgba(102, 16, 242, 0.6)';
    } else {
        paymentChart.data.datasets[0].backgroundColor = 'rgba(13, 202, 240, 0.6)';
    }

    paymentChart.update();
}

function updateChart(timeframe, event) {
    // Update active button state
    document.querySelectorAll('.btn-filter').forEach(btn => btn.classList.remove('active'));
    if(event && event.target) {
        event.target.classList.add('active');
    }
    
    // Load chart data for the selected timeframe
    loadChartData(timeframe);
}

document.addEventListener('DOMContentLoaded', initChart);