<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="../StyleSheets/Css/RoomAvailability.css">

<div class="room-status-wrapper">
    <div class="row g-3">
        <div class="col-12 col-md-4">
            <div class="room-status-glass-card animate__animated animate__fadeIn">
                <i class="fa-solid fa-user room-status-icon-box room-status-clr-single"></i>
                <div class="room-status-label-text">Single Rooms Occupied</div>
                <div class="room-status-val-display" id="singleAvailable">0</div>
                <div class="room-status-prog-track">
                    <div id="singleBar" class="room-status-prog-fill room-status-bg-single" style="width: 0%;"></div>
                </div>
                <div class="text-white-50 small">Max Capacity: 20 Units</div>
            </div>
        </div>

        <div class="col-12 col-md-4">
            <div class="room-status-glass-card animate__animated animate__fadeIn" style="animation-delay: 0.1s">
                <i class="fa-solid fa-users room-status-icon-box room-status-clr-double"></i>
                <div class="room-status-label-text">Double Rooms Occupied</div>
                <div class="room-status-val-display" id="doubleAvailable">0</div>
                <div class="room-status-prog-track">
                    <div id="doubleBar" class="room-status-prog-fill room-status-bg-double" style="width: 0%;"></div>
                </div>
                <div class="text-white-50 small">Max Capacity: 15 Units</div>
            </div>
        </div>

        <div class="col-12 col-md-4">
            <div class="room-status-glass-card animate__animated animate__fadeIn" style="animation-delay: 0.2s">
                <i class="fa-solid fa-crown room-status-icon-box room-status-clr-suite"></i>
                <div class="room-status-label-text">Ocean Suites Occupied</div>
                <div class="room-status-val-display" id="suiteAvailable">0</div>
                <div class="room-status-prog-track">
                    <div id="suiteBar" class="room-status-prog-fill room-status-bg-suite" style="width: 0%;"></div>
                </div>
                <div class="text-white-50 small">Max Capacity: 5 Units</div>
            </div>
        </div>
    </div>
</div>