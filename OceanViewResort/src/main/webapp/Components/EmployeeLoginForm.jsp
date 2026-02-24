<%-- EmployeeLoginForm.jsp --%>
<form action="LoginServlet" method="POST" class="el-login-form">
    <div class="el-input-group animate__animated animate__fadeInLeft animate__delay-1s">
        <input type="text" name="username" class="el-input" placeholder="Employee Username" required>
        <i class="fa-solid fa-user"></i>
    </div>

    <div class="el-input-group animate__animated animate__fadeInRight animate__delay-1s">
        <input type="password" name="password" class="el-input" placeholder=" Employee Password" required>
        <i class="fa-solid fa-lock"></i>
    </div>
    <%@ include file="LoginButton.jsp" %>
    
     <div class="el-reset-container animate__animated animate__fadeInUp animate__delay-1s">
        <a href="ForgotPassword.jsp" class="el-reset-link">Forgot Password?</a>
    </div>
</form>