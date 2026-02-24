<%-- AdminRegisterForm.jsp --%>
<form action="${pageContext.request.contextPath}/AdminServlet?action=register" method="POST" class="animate__animated animate__fadeIn">
        <div class="al-input-group">
            <input type="text" name="username" class="al-input" placeholder="Username" required>
            <i class="fa-solid fa-user"></i>
        </div>
        
        <div class="al-input-group">
            <input type="email" name="email" class="al-input" placeholder="Admin Email" required>
            <i class="fa-solid fa-envelope"></i>
        </div>
        
        <div class="al-input-group">
            <input type="password" name="password" class="al-input" placeholder="Password" required>
            <i class="fa-solid fa-key"></i>
        </div>
        
        <%-- Setting unique label for the modular button --%>
        <% request.setAttribute("btnLabel", "Register Admin"); %>
        <% request.setAttribute("btnId", "regBtn"); %>
        <% request.setAttribute("btnClass", "al-btn-register"); %>
        <%@ include file="AdminRegisterBtn.jsp" %>
    </form>