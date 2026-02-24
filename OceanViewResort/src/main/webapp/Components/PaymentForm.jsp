<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="card bg-dark text-white d-none" id="paymentFormContainer">
    <div class="card-header bg-secondary d-flex flex-column flex-md-row justify-content-between align-items-md-center align-items-start gap-2">
        <div>
            <i class="fas fa-credit-card me-1"></i>
            Payment Processing
        </div>
        <button class="btn btn-sm btn-outline-light" onclick="togglePaymentView('table')">
            <i class="fas fa-table"></i> Back to Reservations
        </button>
    </div>
    <div class="card-body">
        <!-- Reservation Details Display -->
        <div class="row mb-4">
            <div class="col-12">
                <h5 class="text-white">Reservation Details</h5>
                <div class="bg-secondary p-3 rounded">
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>Guest Name:</strong> <span id="paymentGuestName"></span></p>
                            <p><strong>Room Type:</strong> <span id="paymentRoomType"></span></p>
                            <p><strong>Check-In:</strong> <span id="paymentCheckIn"></span></p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>Check-Out:</strong> <span id="paymentCheckOut"></span></p>
                            <p><strong>Room Rate:</strong> <span id="paymentRoomRate"></span></p>
                            <p><strong>Extra Nights:</strong> 
                                <input type="number" class="form-control form-control-sm d-inline-block w-25" 
                                       id="extraNightsInput" min="0" value="0" onchange="updatePaymentCalculation()">
                            </p>
                            <p><strong>Extra Charges:</strong>
                                <select class="form-control form-control-sm d-inline-block w-50" 
                                        id="extraChargesSelect" onchange="updatePaymentCalculation()">
                                    <option value="0">None</option>
                                    <option value="1000">Rs. 1,000</option>
                                    <option value="2000">Rs. 2,000</option>
                                    <option value="5000">Rs. 5,000</option>
                                </select>
                            </p>
                        </div>
                    </div>
                    <div class="row mt-3">
                        <div class="col-12">
                            <div class="alert alert-info">
                                <h6 class="d-md-none">Total:</h6>
                                <h6 class="d-none d-md-block">Total Calculation:</h6>
                                <p><strong>Room Total:</strong> <span id="roomTotal">Rs. 0.00</span></p>
                                <p><strong>Extra Charges:</strong> <span id="extraChargesDisplay">Rs. 0.00</span></p>
                                <p class="fw-bold fs-5 mb-0"><strong>Final Total:</strong> <span id="finalTotal" class="text-success">Rs. 0.00</span></p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Payment Tabs -->
        <ul class="nav nav-tabs" id="paymentTabs" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="cash-tab" data-bs-toggle="tab" data-bs-target="#cash-payment" type="button" role="tab">
                    <i class="fas fa-money-bill-wave"></i> <span class="d-none d-sm-inline">Cash Payment</span>
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="card-tab" data-bs-toggle="tab" data-bs-target="#card-payment" type="button" role="tab">
                    <i class="fas fa-credit-card"></i> <span class="d-none d-sm-inline">Card Payment</span>
                </button>
            </li>
        </ul>

        <div class="tab-content mt-3" id="paymentTabContent">
            <!-- Cash Payment Tab -->
            <div class="tab-pane fade show active" id="cash-payment" role="tabpanel">
                <form id="cashPaymentForm">
                    <div class="mb-3">
                        <label class="form-label">Amount Received</label>
                        <div class="input-group">
                            <span class="input-group-text">Rs.</span>
                            <input type="number" class="form-control bg-dark text-white" id="cashAmount" name="cashAmount" step="0.01" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Change Due</label>
                        <div class="input-group">
                            <span class="input-group-text">Rs.</span>
                            <input type="text" class="form-control bg-dark text-white" id="changeAmount" readonly>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Guest Email</label>
                        <input type="email" class="form-control bg-dark text-white" id="guestEmail" name="guestEmail" 
                               placeholder="guest@example.com" required>
                    </div>
                    <button type="submit" class="btn btn-success w-100" id="cashPaymentBtn">
                        <i class="fas fa-check-circle me-2"></i>Process Cash Payment
                    </button>
                </form>
            </div>

            <!-- Card Payment Tab -->
            <div class="tab-pane fade" id="card-payment" role="tabpanel">
                <form id="cardPaymentForm">
                    <div class="mb-3">
                        <label class="form-label">Card Number</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-credit-card"></i></span>
                            <input type="text" class="form-control bg-dark text-white" id="cardNumber" name="cardNumber" 
                                   placeholder="1234 5678 9012 3456" required>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Expiry Date</label>
                            <input type="text" class="form-control bg-dark text-white" id="expiryDate" name="expiryDate" 
                                   placeholder="MM/YY" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">CVV</label>
                            <input type="text" class="form-control bg-dark text-white" id="cvv" name="cvv" 
                                   placeholder="123" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Card Holder Name</label>
                        <input type="text" class="form-control bg-dark text-white" id="cardHolderName" name="cardHolderName" 
                               placeholder="John Doe" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Guest Email</label>
                        <input type="email" class="form-control bg-dark text-white" id="cardGuestEmail" name="guestEmail" 
                               placeholder="guest@example.com" required>
                    </div>
                    <button type="submit" class="btn btn-primary w-100" id="cardPaymentBtn">
                        <i class="fas fa-check-circle me-2"></i>Process Card Payment
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>