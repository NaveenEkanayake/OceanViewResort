<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment Processing - Ocean View Resort</title>
    <meta name="contextPath" content="${pageContext.request.contextPath}">
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/SideBar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/PaymentProcessing.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/StyleSheets/Css/PaymentForm.css">
    
    <script>
        // Prevent flash of uncollapsed sidebar
        if (localStorage.getItem('sidebarState') === 'collapsed') {
            document.documentElement.classList.add('sidebar-is-collapsed');
        }
    </script>
</head>

<body class="ov-payment-page">
    <%@ include file="../Components/SideBar.jsp" %>
   
    <div class="content-wrapper">
        <div class="container-fluid payment-container">
            <div class="row">
                <div class="col-12">
                    <h1 class="text-white mb-4">
                        <i class="fas fa-credit-card me-2"></i>Payment Processing
                    </h1>
                    
                    <div class="payment-table-container">
                        <%@ include file="../Components/PaymentReservationTable.jsp" %>
                    </div>
                    
                    <%@ include file="../Components/PaymentForm.jsp" %>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/StyleSheets/Js/Sidebar.js"></script>
    <script src="${pageContext.request.contextPath}/StyleSheets/Js/PaymentProcessing.js"></script>
</body>
</html>