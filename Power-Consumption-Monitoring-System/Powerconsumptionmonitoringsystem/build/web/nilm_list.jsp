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
        .content { padding: 20px; }
        .top-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
        .btn-back { text-decoration: none; color: #1f77b4; font-weight: bold; }
        .btn-add { background-color: #28a745; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px; font-weight: bold; }
        .btn-add:hover { background-color: #218838; }
        table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #1f77b4; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        tr:hover { background-color: #e9e9e9; }
        .action-links a { text-decoration: none; margin-right: 10px; font-weight: bold; }
        .btn-edit { color: #ffc107; }
        .btn-delete { color: #dc3545; }
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
            
            <%-- Nút Thêm mới thiết bị --%>
            <a href="ApplianceController?action=add" class="btn-add">+ Add New Appliance</a>
        </div>

        <h3>Appliance Management</h3>

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
                            <a href="ApplianceController?action=edit&id=${app.appliance_id}" class="btn-edit">Edit</a>
                            <a href="ApplianceController?action=delete&id=${app.appliance_id}" class="btn-delete" onclick="return confirm('Are you sure you want to delete this appliance?');">Delete</a>
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
    </div>
</body>
</html>
