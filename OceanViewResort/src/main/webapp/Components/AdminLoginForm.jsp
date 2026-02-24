<%-- AdminLoginForm.jsp --%>
<form action="${pageContext.request.contextPath}/AdminServlet?action=login" method="POST" class="animate__animated animate__fadeIn">
    <div class="al-input-group">
        <input type="text" name="username" class="al-input" placeholder="Admin Username" required>
        <i class="fa-solid fa-user-shield"></i>
    </div>
    
    <div class="al-input-group">
        <input type="password" name="password" class="al-input" placeholder="Password" required>
        <i class="fa-solid fa-lock"></i>
    </div>

    <%-- Reusing the modular button logic --%>
    <% request.setAttribute("btnLabel", "Login to Portal"); %>
    <%@ include file="AdminLoginBtn.jsp" %>
</form>