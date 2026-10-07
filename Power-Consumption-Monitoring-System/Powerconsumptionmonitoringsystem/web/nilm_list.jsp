<%-- 
    Document   : nilm_list
    Created on : Sep 27, 2026, 12:17:21 AM
    Author     : hoanh
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Appliance List - NILM System</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f9f9f9; }
        .header { background-color: #1f77b4; color: white; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; }
        .header h2 { margin: 0; font-size: 20px; }
        .header-right { display: flex; align-items: center; gap: 15px; }
        .btn-logout { background: #d9534f; color: white; border: none; padding: 6px 12px; border-radius: 4px; font-weight: bold; cursor: pointer; }
        .btn-logout:hover { background: #c9302c; }
        .content { padding: 20px 20px 84px; }
        .top-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
        .btn-back { text-decoration: none; color: #1f77b4; font-weight: bold; }
        .btn-add { background-color: #28a745; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px; font-weight: bold; }
        .btn-add:hover { background-color: #218838; }
        .top-right-actions { display: flex; align-items: center; gap: 10px; }
        .search-form { display: flex; align-items: center; gap: 8px; margin: 0; }
        .search-form select, .btn-search { padding: 8px 10px; border: 1px solid #ccc; border-radius: 4px; }
        .btn-search { background: #1f77b4; color: white; border: none; font-weight: bold; cursor: pointer; }
        .btn-search:hover:not(:disabled) { background: #155d8b; }
        .btn-search:disabled { opacity: 0.5; cursor: not-allowed; }
        .search-error { color: #dc3545; margin: 0 0 12px; }
        table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #1f77b4; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        tr:hover { background-color: #e9e9e9; }
        .action-links a { text-decoration: none; margin-right: 10px; font-weight: bold; }
        .btn-edit { color: #ffc107; }
        .btn-delete { color: #dc3545; }
        .action-links form { display: inline; margin: 0; }
        .action-links button { border: 0; padding: 0; background: none; font: inherit; font-weight: bold; cursor: pointer; }
        .pagination { position: fixed; bottom: 0; left: 0; right: 0; z-index: 10; display: grid; grid-template-columns: 1fr auto 1fr; align-items: center; min-height: 56px; padding: 8px 16px; margin: 0; background: rgba(249, 249, 249, 0.96); box-shadow: 0 -2px 6px rgba(0,0,0,0.12); }
        .pagination .previous { justify-self: end; margin-right: 12px; }
        .pagination .page-label { grid-column: 2; }
        .pagination .next { justify-self: start; margin-left: 12px; }
        .pagination a { background: #1f77b4; color: white; padding: 8px 14px; border-radius: 4px; text-decoration: none; font-weight: bold; }
        .pagination a:hover { background: #155d8b; }
        @media (max-width: 800px) {
            .top-actions, .top-right-actions { align-items: flex-start; flex-direction: column; }
            .search-form { flex-wrap: wrap; }
        }
    </style>
</head>
<body>
    <div class="header">
        <h2>Power Consumption Monitoring System (NILM)</h2>
        <div class="header-right">
            <span>Welcome, <strong>${sessionScope.LOGIN_USER.fullName}</strong></span>
            
            <%-- Form Logout đồng bộ --%>
            <form action="${pageContext.request.contextPath}/logout" method="POST" style="margin: 0;">
                <button type="submit" class="btn-logout">Logout</button>
            </form>
        </div>
    </div>
    
    <div class="content">
        <div class="top-actions">
            <a href="DashboardController" class="btn-back">← Back to Dashboard</a>
            
            <div class="top-right-actions">
                <form action="ApplianceController" method="get" class="search-form" id="appliance-search-form">
                    <input type="hidden" name="search" value="1">
                    <label for="household-select">House ID</label>
                    <select id="household-select" name="householdId">
                        <option value="" ${empty householdId ? 'selected' : ''}>All Appliance</option>
                        <c:forEach items="${householdIds}" var="id">
                            <option value="<c:out value='${id}' />" ${householdId == id ? 'selected' : ''}><c:out value="${id}" /></option>
                        </c:forEach>
                    </select>
                    <label for="room-select">Room</label>
                    <select id="room-select" name="room" ${empty householdId ? 'disabled' : ''}>
                        <option value="">All Room</option>
                        <c:forEach items="${rooms}" var="roomOption">
                            <option value="<c:out value='${roomOption}' />" ${room == roomOption ? 'selected' : ''}><c:out value="${roomOption}" /></option>
                        </c:forEach>
                    </select>
                    <button type="submit" class="btn-search" id="search-button">Search</button>
                </form>
                <a href="ApplianceController?action=add" class="btn-add">+ Add New Appliance</a>
            </div>
        </div>

        <h3>Appliance Management</h3>
        <c:if test="${not empty searchError}">
            <p class="search-error">${searchError}</p>
        </c:if>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Appliance Name</th>
                    <th>Household ID</th>
                    <th>Rated Power (W)</th>
                    <th>Room / Location</th>
                    <th>Created At</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${items}" var="app">
                    <tr>
                        <%-- Khai báo thuộc tính theo đúng chuẩn camelCase của Java Entity --%>
                        <td>${app.appliance_id}</td>
                        <td>${app.appliance_name}</td>
                        <td>${app.household_id}</td>
                        <td>${app.rated_w}</td>
                        <td>${app.room}</td>
                        <td>${app.createdAt}</td>
                        <td class="action-links">
                            <c:url var="editUrl" value="ApplianceController">
                                <c:param name="action" value="edit" />
                                <c:param name="id" value="${app.appliance_id}" />
                                <c:param name="page" value="${page}" />
                                <c:param name="search" value="1" />
                                <c:param name="householdId" value="${householdId}" />
                                <c:param name="room" value="${room}" />
                                <c:param name="q" value="${q}" />
                            </c:url>
                            <a href="${editUrl}" class="btn-edit">Edit</a>
                            <form action="ApplianceController" method="post"
                                  onsubmit="return confirm('Are you sure you want to delete this appliance?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${app.appliance_id}">
                                <input type="hidden" name="page" value="${page}">
                                <input type="hidden" name="search" value="1">
                                <input type="hidden" name="householdId" value="<c:out value='${householdId}' />">
                                <input type="hidden" name="room" value="<c:out value='${room}' />">
                                <input type="hidden" name="q" value="<c:out value='${q}' />">
                                <button type="submit" class="btn-delete">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                
                <%-- Dòng thông báo nếu danh sách trống --%>
                <c:if test="${empty items}">
                    <tr>
                        <td colspan="7" style="text-align: center; color: #888;">No appliance records found in the database.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>

        <div class="pagination">
            <c:if test="${page > 1}">
                <c:url var="previousPageUrl" value="ApplianceController">
                    <c:param name="page" value="${page - 1}" />
                    <c:param name="q" value="${q}" />
                    <c:param name="search" value="1" />
                    <c:param name="householdId" value="${householdId}" />
                    <c:param name="room" value="${room}" />
                </c:url>
                <a class="previous" href="${previousPageUrl}">Previous</a>
            </c:if>
            <c:if test="${page <= 1}">
                <span></span>
            </c:if>
            <span class="page-label">Page ${page}</span>
            <c:if test="${hasNextPage}">
                <c:url var="nextPageUrl" value="ApplianceController">
                    <c:param name="page" value="${page + 1}" />
                    <c:param name="q" value="${q}" />
                    <c:param name="search" value="1" />
                    <c:param name="householdId" value="${householdId}" />
                    <c:param name="room" value="${room}" />
                </c:url>
                <a class="next" href="${nextPageUrl}">Next</a>
            </c:if>
            <c:if test="${not hasNextPage}">
                <span></span>
            </c:if>
        </div>
    </div>
    <script>
        const householdSelect = document.getElementById('household-select');
        const roomSelect = document.getElementById('room-select');
        const searchButton = document.getElementById('search-button');
        const roomEndpoint = '${pageContext.request.contextPath}/ApplianceController?action=rooms&householdId=';

        householdSelect.addEventListener('change', async function () {
            const householdId = householdSelect.value;
            roomSelect.innerHTML = '<option value="">All Room</option>';
            roomSelect.disabled = !householdId;
            searchButton.disabled = false;
            if (!householdId) {
                return;
            }

            roomSelect.disabled = true;
            searchButton.disabled = true;
            try {
                const response = await fetch(roomEndpoint + encodeURIComponent(householdId));
                if (!response.ok) {
                    throw new Error('Unable to load rooms.');
                }
                const rooms = await response.json();
                rooms.forEach(function (room) {
                    const option = document.createElement('option');
                    option.value = room;
                    option.textContent = room;
                    roomSelect.appendChild(option);
                });
                roomSelect.disabled = false;
                searchButton.disabled = false;
            } catch (error) {
                roomSelect.disabled = true;
                searchButton.disabled = true;
                alert(error.message);
            }
        });
    </script>
</body>
</html>
