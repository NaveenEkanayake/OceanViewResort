// Payment Processing JavaScript

(function() {
    'use strict';
    
    // Global variables
    let currentReservation = null;
    let currentPaymentData = null;
    
    // DOM Elements
    const paymentReservationTable = document.getElementById('paymentReservationTable');
    const paymentFormContainer = document.getElementById('paymentFormContainer');
    const cashAmountInput = document.getElementById('cashAmount');
    const changeAmountInput = document.getElementById('changeAmount');
    const extraNightsInput = document.getElementById('extraNightsInput');
    const extraChargesSelect = document.getElementById('extraChargesSelect');
    const cashPaymentForm = document.getElementById('cashPaymentForm');
    const cardPaymentForm = document.getElementById('cardPaymentForm');
    
    // Initialize when DOM is ready
    document.addEventListener('DOMContentLoaded', function() {
        initializePaymentSystem();
    });
    
    // Initialize the payment system
    function initializePaymentSystem() {
        setupEventListeners();
        setupTableEventHandlers();
        initializeToastNotifications();
    }
    
    // Setup all event listeners
    function setupEventListeners() {
        // Cash amount change calculation
        if (cashAmountInput) {
            cashAmountInput.addEventListener('input', calculateChange);
        }
        
        // Form submissions
        if (cashPaymentForm) {
            cashPaymentForm.addEventListener('submit', handleCashPayment);
        }
        
        if (cardPaymentForm) {
            cardPaymentForm.addEventListener('submit', handleCardPayment);
        }
        
        // Tab switching
        setupTabSwitching();
    }
    
    // Setup table event handlers with event delegation
    function setupTableEventHandlers() {
        // Use event delegation for dynamically added content
        document.addEventListener('click', function(e) {
            // Handle Pay button clicks
            if (e.target.closest('.pay-btn') && !e.target.closest('.pay-btn').disabled) {
                e.preventDefault();
                const payButton = e.target.closest('.pay-btn');
                const row = payButton.closest('tr');
                if (row && row.dataset.reservationId) {
                    processPaymentFromButton(payButton);
                }
            }
        });
    }
    
    // Setup tab switching functionality
    function setupTabSwitching() {
        const tabButtons = document.querySelectorAll('[data-bs-toggle="tab"]');
        tabButtons.forEach(button => {
            button.addEventListener('click', function(e) {
                e.preventDefault();
                const target = this.getAttribute('data-bs-target');
                switchTab(target);
            });
        });
    }
    
    // Switch between tabs
    function switchTab(targetTab) {
        // Hide all tab panes
        document.querySelectorAll('.tab-pane').forEach(pane => {
            pane.classList.remove('show', 'active');
        });
        
        // Remove active class from all tab buttons
        document.querySelectorAll('.nav-link').forEach(button => {
            button.classList.remove('active');
        });
        
        // Show target tab
        const targetPane = document.querySelector(targetTab);
        if (targetPane) {
            targetPane.classList.add('show', 'active');
        }
        
        // Activate clicked tab button
        const activeButton = document.querySelector(`[data-bs-target="${targetTab}"]`);
        if (activeButton) {
            activeButton.classList.add('active');
        }
        
        showToast('Switched to ' + (targetTab.includes('cash') ? 'Cash' : 'Card') + ' Payment', 'info');
    }
    
    // Process payment from button click
    function processPaymentFromButton(button) {
        const row = button.closest('tr');
        const reservationId = parseInt(row.dataset.reservationId);
        const guestName = row.querySelector('td:nth-child(2)').textContent.trim();
        const roomType = row.querySelector('td:nth-child(3)').textContent || 
                        row.querySelector('td:nth-child(2) small')?.textContent || 'Unknown';
        const checkIn = row.querySelector('td:nth-child(4)').textContent;
        const checkOut = row.querySelector('td:nth-child(5)').textContent;
        const nights = parseInt(row.querySelector('td:nth-child(6)').textContent);
        const amountText = row.querySelector('td:nth-child(7)').textContent;
        const amount = parseFloat(amountText.replace('Rs. ', ''));
        
        currentReservation = {
            id: reservationId,
            guestName: guestName,
            roomType: roomType,
            checkIn: checkIn,
            checkOut: checkOut,
            nights: nights,
            amount: amount
        };
        
        displayReservationDetails(currentReservation);
        togglePaymentView('payment');
        showToast('Reservation details loaded successfully', 'success');
    }
    
    // Display reservation details in payment form
    function displayReservationDetails(reservation) {
        // Update reservation details display
        const guestNameEl = document.getElementById('paymentGuestName');
        const roomTypeEl = document.getElementById('paymentRoomType');
        const checkInEl = document.getElementById('paymentCheckIn');
        const checkOutEl = document.getElementById('paymentCheckOut');
        const roomRateEl = document.getElementById('paymentRoomRate');
        
        if (guestNameEl) guestNameEl.textContent = reservation.guestName;
        if (roomTypeEl) roomTypeEl.textContent = reservation.roomType;
        if (checkInEl) checkInEl.textContent = reservation.checkIn;
        if (checkOutEl) checkOutEl.textContent = reservation.checkOut;
        
        // Set room rate based on room type
        if (roomRateEl) {
            let roomRate = 0;
            switch(reservation.roomType) {
                case 'Single Room': roomRate = 5000.00; break;
                case 'Double Room': roomRate = 8000.00; break;
                case 'Ocean Suite': roomRate = 15000.00; break;
                default: roomRate = 5000.00; // Default rate
            }
            roomRateEl.textContent = 'Rs. ' + roomRate.toFixed(2) + '/night';
        }
        
        // Initialize payment data
        let roomRate = 0;
        switch(reservation.roomType) {
            case 'Single Room': roomRate = 5000.00; break;
            case 'Double Room': roomRate = 8000.00; break;
            case 'Ocean Suite': roomRate = 15000.00; break;
            default: roomRate = 5000.00;
        }
        
        currentPaymentData = {
            reservationId: reservation.id,
            guestName: reservation.guestName,
            roomType: reservation.roomType,
            checkIn: reservation.checkIn,
            checkOut: reservation.checkOut,
            nightsStayed: reservation.nights,
            roomRatePerNight: roomRate,
            extraNights: 0,
            extraCharges: 0.00,
            roomTotal: reservation.nights * roomRate,
            totalAmount: reservation.nights * roomRate
        };
        
        // Update display
        updatePaymentCalculation();
        
        // Set cash amount to total amount for easy calculation
        if (cashAmountInput) {
            cashAmountInput.value = currentPaymentData.totalAmount.toFixed(2);
            calculateChange();
        }
    }
    
    // Update payment calculation
    function updatePaymentCalculation() {
        if (!currentPaymentData) return;
        
        const extraNights = parseInt(extraNightsInput?.value) || 0;
        const extraCharges = parseFloat(extraChargesSelect?.value) || 0.00;
        
        currentPaymentData.extraNights = extraNights;
        currentPaymentData.extraCharges = extraCharges;
        currentPaymentData.roomTotal = (currentPaymentData.nightsStayed + extraNights) * currentPaymentData.roomRatePerNight;
        currentPaymentData.totalAmount = currentPaymentData.roomTotal + extraCharges;
        
        // Update display elements
        const roomTotalEl = document.getElementById('roomTotal');
        const extraChargesEl = document.getElementById('extraChargesDisplay');
        const finalTotalEl = document.getElementById('finalTotal');
        
        if (roomTotalEl) roomTotalEl.textContent = 'Rs. ' + currentPaymentData.roomTotal.toFixed(2);
        if (extraChargesEl) extraChargesEl.textContent = 'Rs. ' + extraCharges.toFixed(2);
        if (finalTotalEl) finalTotalEl.textContent = 'Rs. ' + currentPaymentData.totalAmount.toFixed(2);
        
        // Update cash amount if needed
        if (cashAmountInput && (cashAmountInput.value === '' || parseFloat(cashAmountInput.value) < currentPaymentData.totalAmount)) {
            cashAmountInput.value = currentPaymentData.totalAmount.toFixed(2);
            calculateChange();
        }
    }
    
    // Calculate change amount
    function calculateChange() {
        if (!cashAmountInput || !changeAmountInput || !currentPaymentData) return;
        
        const cashAmount = parseFloat(cashAmountInput.value) || 0;
        const totalAmount = currentPaymentData.totalAmount;
        const change = cashAmount - totalAmount;
        
        changeAmountInput.value = change >= 0 ? change.toFixed(2) : '0.00';
        changeAmountInput.classList.toggle('payment-change-amount', change >= 0);
    }
    
    // Handle cash payment submission
    function handleCashPayment(e) {
        e.preventDefault();
        
        if (!currentPaymentData) {
            showToast('No payment data available', 'error');
            return;
        }
        
        const cashAmount = parseFloat(cashAmountInput.value);
        if (cashAmount < currentPaymentData.totalAmount) {
            showToast('Insufficient cash amount', 'error');
            return;
        }
        
        const paymentData = {
            action: 'processPayment',
            reservationId: currentPaymentData.reservationId,
            paymentMethod: 'Cash',
            extraNights: currentPaymentData.extraNights,
            extraCharges: currentPaymentData.extraCharges,
            guestEmail: document.getElementById('guestEmail')?.value || ''
        };
        
        submitPayment(paymentData, 'cashPaymentForm');
    }
    
    // Handle card payment submission
    function handleCardPayment(e) {
        e.preventDefault();
        
        if (!currentPaymentData) {
            showToast('No payment data available', 'error');
            return;
        }
        
        const cardData = {
            action: 'processPayment',
            reservationId: currentPaymentData.reservationId,
            paymentMethod: 'Card',
            extraNights: currentPaymentData.extraNights,
            extraCharges: currentPaymentData.extraCharges,
            cardNumber: document.getElementById('cardNumber')?.value || '',
            expiryDate: document.getElementById('expiryDate')?.value || '',
            cvv: document.getElementById('cvv')?.value || '',
            cardHolderName: document.getElementById('cardHolderName')?.value || '',
            guestEmail: document.getElementById('cardGuestEmail')?.value || ''
        };
        
        // Validate card data
        if (!validateCardData(cardData)) {
            return;
        }
        
        submitPayment(cardData, 'cardPaymentForm');
    }
    
    // Validate card data
    function validateCardData(cardData) {
        if (!cardData.cardNumber || cardData.cardNumber.replace(/\s/g, '').length < 16) {
            showToast('Invalid card number', 'error');
            return false;
        }
        
        if (!cardData.expiryDate || !/^\d{2}\/\d{2}$/.test(cardData.expiryDate)) {
            showToast('Invalid expiry date format (MM/YY)', 'error');
            return false;
        }
        
        if (!cardData.cvv || cardData.cvv.length < 3) {
            showToast('Invalid CVV', 'error');
            return false;
        }
        
        if (!cardData.cardHolderName) {
            showToast('Card holder name is required', 'error');
            return false;
        }
        
        if (!cardData.guestEmail) {
            showToast('Guest email is required', 'error');
            return false;
        }
        
        return true;
    }
    
    // Submit payment to server
    function submitPayment(paymentData, formId) {
        const form = document.getElementById(formId);
        const submitBtn = form?.querySelector('button[type="submit"]');
        const originalText = submitBtn?.innerHTML;
        
        // Show loading state
        if (submitBtn) {
            submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status"></span> Processing...';
            submitBtn.disabled = true;
        }
        
        // Use annotated servlet path with context
        const contextPathMeta = document.querySelector('meta[name="contextPath"]');
        let url;
        if (contextPathMeta && contextPathMeta.getAttribute('content')) {
            url = contextPathMeta.getAttribute('content') + '/PaymentServlet';
        } else {
            // Fallback to relative path
            url = '../PaymentServlet';
        }
        console.log('Attempting to fetch:', url);
        console.log('Payment data being sent:', paymentData);
        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams(paymentData)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Server error: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                showToast(data.message, 'success');
                resetPaymentForm();
                togglePaymentView('table');
                // Refresh the payment table to show updated status
                setTimeout(() => {
                    location.reload();
                }, 2000);
            } else {
                showToast('Payment failed: ' + data.message, 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Network error occurred: ' + error.message, 'error');
        })
        .finally(() => {
            // Reset button
            if (submitBtn) {
                submitBtn.innerHTML = originalText;
                submitBtn.disabled = false;
            }
        });
    }
    
    // Reset payment form
    function resetPaymentForm() {
        if (cashPaymentForm) cashPaymentForm.reset();
        if (cardPaymentForm) cardPaymentForm.reset();
        if (changeAmountInput) changeAmountInput.value = '';
        if (extraNightsInput) extraNightsInput.value = '0';
        if (extraChargesSelect) extraChargesSelect.value = '0';
        currentReservation = null;
        currentPaymentData = null;
    }
    
    // Toggle between table and payment views
    function togglePaymentView(view) {
        if (view === 'payment') {
            document.querySelector('.payment-table-container')?.classList.add('d-none');
            paymentFormContainer?.classList.remove('d-none');
            paymentFormContainer?.classList.add('view-fade-in');
        } else {
            paymentFormContainer?.classList.add('d-none');
            document.querySelector('.payment-table-container')?.classList.remove('d-none');
            document.querySelector('.payment-table-container')?.classList.add('view-fade-in');
        }
    }
    
    // View reservation details
    function viewReservationDetails(reservationId) {
        // This would show a modal with reservation details
        showToast('Viewing details for reservation #' + reservationId, 'info');
    }
    
    // Initialize toast notifications
    function initializeToastNotifications() {
        // Create toast container if it doesn't exist
        if (!document.querySelector('.toast-container')) {
            const toastContainer = document.createElement('div');
            toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
            toastContainer.style.zIndex = '1055';
            document.body.appendChild(toastContainer);
        }
    }
    
    // Show toast notification
    function showToast(message, type = 'info', duration = 5000) {
        const toastContainer = document.querySelector('.toast-container');
        if (!toastContainer) return;
        
        const toastId = 'toast_' + Date.now();
        const toastHtml = `
            <div id="${toastId}" class="toast payment-toast payment-toast-${type} align-items-center text-white border-0" role="alert">
                <div class="d-flex">
                    <div class="toast-body">
                        <i class="fas fa-${getToastIcon(type)} me-2"></i>
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        `;
        
        toastContainer.insertAdjacentHTML('beforeend', toastHtml);
        const toastElement = document.getElementById(toastId);
        const toast = new bootstrap.Toast(toastElement, { delay: duration });
        toast.show();
        
        // Remove toast from DOM after hiding
        toastElement.addEventListener('hidden.bs.toast', function() {
            toastElement.remove();
        });
    }
    
    // Get appropriate icon for toast type
    function getToastIcon(type) {
        switch (type) {
            case 'success': return 'check-circle';
            case 'error': return 'exclamation-circle';
            case 'warning': return 'exclamation-triangle';
            default: return 'info-circle';
        }
    }
    
    // Global functions (made available to HTML)
    window.processPaymentFromButton = processPaymentFromButton;
    window.togglePaymentView = togglePaymentView;
    window.viewReservationDetails = viewReservationDetails;
    window.updatePaymentCalculation = updatePaymentCalculation;
    window.showToast = showToast;
    
})();