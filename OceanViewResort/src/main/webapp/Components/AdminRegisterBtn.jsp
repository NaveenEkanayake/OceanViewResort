<button type="submit" id="<%= request.getAttribute("btnId") %>" class="<%= request.getAttribute("btnClass") %> animate__animated animate__pulse">
    <span id="regSpinner" class="spinner-border spinner-border-sm d-none me-2" role="status"></span>
    <span id="regText"><%= request.getAttribute("btnLabel") %></span>
</button>