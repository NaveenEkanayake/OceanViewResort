<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<link rel="stylesheet" href="../StyleSheets/Css/EmployeeDashStat.css">

<div class="payment-stats-container animate__animated animate__fadeInUp">
    <div class="filter-bar mb-4">
        <div class="btn-group shadow-sm" role="group" aria-label="Payment Filter">
            <button type="button" class="btn btn-filter active" onclick="updateChart('daily')">Daily</button>
            <button type="button" class="btn btn-filter" onclick="updateChart('weekly')">Weekly</button>
            <button type="button" class="btn btn-filter" onclick="updateChart('monthly')">Monthly</button>
            <button type="button" class="btn btn-filter" onclick="updateChart('yearly')">Yearly</button>
        </div>
    </div>

    <div class="payment-card">
        <div class="card-body">
            <canvas id="paymentChart"></canvas>
        </div>
    </div>
</div>

<script>
    // Pass context path to JavaScript
    const contextPath = '<%=request.getContextPath()%>';
</script>

<script src="../StyleSheets/Js/EmployeeDashStat.js"></script>