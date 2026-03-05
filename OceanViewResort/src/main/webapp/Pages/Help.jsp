<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Help & Documentation - Ocean View Resort</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 40px 20px;
        }
        
        .help-container {
            max-width: 1200px;
            margin: 0 auto;
        }
        
        .header-section {
            text-align: center;
            color: white;
            margin-bottom: 40px;
        }
        
        .help-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            margin-bottom: 30px;
            overflow: hidden;
        }
        
        .card-header-custom {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            font-size: 1.3rem;
            font-weight: 600;
        }
        
        .card-body-custom {
            padding: 30px;
        }
        
        .step-item {
            display: flex;
            align-items: flex-start;
            margin-bottom: 20px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 10px;
            border-left: 4px solid #667eea;
        }
        
        .step-number {
            background: #667eea;
            color: white;
            width: 35px;
            height: 35px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            margin-right: 15px;
            flex-shrink: 0;
        }
        
        .back-btn {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1000;
        }
        
        .info-box {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <a href="javascript:history.back()" class="btn btn-outline-light back-btn">
        <i class="fas fa-arrow-left me-2"></i>Back
    </a>

    <div class="help-container">
        <div class="header-section">
            <h1><i class="fas fa-question-circle me-2"></i>Ocean View Resort Help Center</h1>
            <p class="lead">Complete guide to using the resort management system</p>
        </div>

        <!-- Admin Section -->
        <div class="help-card">
            <div class="card-header-custom">
                <i class="fas fa-user-tie me-2"></i>Admin Guide
            </div>
            <div class="card-body-custom">
                <h5 class="mb-3">Dashboard Overview:</h5>
                <div class="step-item">
                    <div class="step-number">1</div>
                    <div>
                        <strong>Add Employee:</strong> Create new employee accounts. System automatically sends email with login credentials (username + temporary password).
                    </div>
                </div>
                <div class="step-item">
                    <div class="step-number">2</div>
                    <div>
                        <strong>View Employees:</strong> See all staff members, edit details, activate/deactivate accounts. Track individual performance metrics.
                    </div>
                </div>
                <div class="step-item">
                    <div class="step-number">3</div>
                    <div>
                        <strong>Monitor Analytics:</strong> Real-time dashboard shows total revenue, room occupancy rates, payment trends (daily/weekly/monthly/yearly charts).
                    </div>
                </div>
                <div class="step-item">
                    <div class="step-number">4</div>
                    <div>
                        <strong>Data Isolation:</strong> Each employee can ONLY see their own reservations and payments. Admin sees ALL data across the system.
                    </div>
                </div>
            </div>
        </div>

        <!-- Employee Section -->
        <div class="help-card">
            <div class="card-header-custom bg-success">
                <i class="fas fa-user-circle me-2"></i>Employee Guide
            </div>
            <div class="card-body-custom">
                <h5 class="mb-3">Your Daily Workflow:</h5>
                <div class="step-item">
                    <div class="step-number">1</div>
                    <div>
                        <strong>Make Reservation:</strong> Enter guest details, select room type (Single: Rs. 5000, Double: Rs. 8000, Ocean Suite: Rs. 15000), choose dates. System auto-calculates nights.
                    </div>
                </div>
                <div class="step-item">
                    <div class="step-number">2</div>
                    <div>
                        <strong>View Reservations:</strong> See ONLY your own created reservations. Filter by status (Pending/Paid), search by guest name, or view all.
                    </div>
                </div>
                <div class="step-item">
                    <div class="step-number">3</div>
                    <div>
                        <strong>Update/Delete:</strong> Modify reservation dates, upgrade rooms, change guest info, or cancel bookings. Changes reflect instantly.
                    </div>
                </div>
                <div class="step-item">
                    <div class="step-number">4</div>
                    <div>
                        <strong>Calculate Bill:</strong> Select reservation → Base bill auto-calculated → Add extra charges (Gym, Mini-fridge, Spa, etc.) → Choose Cash or Card payment.
                    </div>
                </div>
                <div class="step-item">
                    <div class="step-number">5</div>
                    <div>
                        <strong>Payment Processing:</strong> 
                        <ul class="mt-2 mb-0">
                            <li><strong>Cash:</strong> Enter amount, system calculates change</li>
                            <li><strong>Card:</strong> Secure form with validation</li>
                            <li><strong>After Payment:</strong> Status updates to "Paid" + Invoice email sent to guest automatically</li>
                        </ul>
                    </div>
                </div>
                
                <div class="alert alert-warning mt-4">
                    <h6><i class="fas fa-exclamation-triangle me-2"></i>Important Privacy Rules:</h6>
                    <ul class="mb-0">
                        <li>You can ONLY see reservations YOU created (filtered by your username)</li>
                        <li>You can ONLY see YOUR OWN payment statistics in charts</li>
                        <li>Other employees' data is completely private and inaccessible</li>
                        <li>This ensures fair performance tracking and data security</li>
                    </ul>
                </div>
            </div>
        </div>

        <!-- Extra Charges Info -->
        <div class="help-card">
            <div class="card-header-custom bg-warning text-dark">
                <i class="fas fa-plus-circle me-2"></i>Extra Charges Guide
            </div>
            <div class="card-body-custom">
                <p>Extra charges are additional fees for guest amenities and services. Common examples:</p>
                <div class="row">
                    <div class="col-md-6">
                        <ul class="list-unstyled">
                            <li><i class="fas fa-dumbbell text-primary me-2"></i>Gym Access: Rs. 500/day</li>
                            <li><i class="fas fa-snowflake text-info me-2"></i>Mini-Fridge: Rs. 800/day</li>
                            <li><i class="fas fa-spa text-success me-2"></i>Spa Services: Rs. 2000+</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <ul class="list-unstyled">
                            <li><i class="fas fa-concierge-bell text-warning me-2"></i>Room Service: Rs. 1000+</li>
                            <li><i class="fas fa-wifi text-danger me-2"></i>Premium WiFi: Rs. 300/day</li>
                            <li><i class="fas fa-parking text-secondary me-2"></i>Valet Parking: Rs. 500/day</li>
                        </ul>
                    </div>
                </div>
                <p class="text-muted mt-3"><small>These amounts are added to the base room charge when calculating the final bill.</small></p>
            </div>
        </div>

        <!-- Email Templates Info -->
        <div class="help-card">
            <div class="card-header-custom bg-info text-white">
                <i class="fas fa-envelope-open-text me-2"></i>Email Notifications
            </div>
            <div class="card-body-custom">
                <div class="row">
                    <div class="col-md-6">
                        <h6>Employee Welcome Email:</h6>
                        <ul>
                            <li>Sent when admin creates new employee</li>
                            <li>Contains username + temporary password</li>
                            <li>Includes direct login link</li>
                            <li>Professional HTML template</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h6>Payment Confirmation Email:</h6>
                        <ul>
                            <li>Sent automatically after successful payment</li>
                            <li>Includes full invoice with itemized charges</li>
                            <li>Reservation details and payment method</li>
                            <li>Thank you message and contact info</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div class="info-box text-center">
            <h4><i class="fas fa-headset me-2"></i>Need Technical Support?</h4>
            <p class="mb-0">Contact the IT department for system issues or the HR department for account-related questions.</p>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
