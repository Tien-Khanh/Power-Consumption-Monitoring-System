<%-- 
    Document   : dashboard
    Created on : Sep 27, 2026, 12:15:41 AM
    Author     : hoanh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Dashboard - NILM System</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 0;
                padding: 0;
                background-color: #f9f9f9;
            }
            .header {
                background-color: #1f77b4;
                color: white;
                padding: 15px 20px;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .header h2 {
                margin: 0;
                font-size: 20px;
            }
            .header-right {
                display: flex;
                align-items: center;
                gap: 15px;
            }
            .btn-logout {
                background: #d9534f;
                color: white;
                border: none;
                padding: 6px 12px;
                border-radius: 4px;
                font-weight: bold;
                cursor: pointer;
            }
            .btn-logout:hover {
                background: #c9302c;
            }
            .content {
                padding: 20px;
            }
            .menu-box {
                border: 1px solid #ddd;
                padding: 20px;
                width: 360px;
                border-radius: 5px;
                background: white;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
            .menu-box a {
                display: block;
                margin: 10px 0;
                padding: 12px;
                background: #f4f4f4;
                text-decoration: none;
                color: #333;
                border-radius: 3px;
                font-weight: bold;
                border-left: 4px solid #1f77b4;
            }
            .menu-box a:hover {
                background: #e9e9e9;
            }
        </style>
    </head>
    <body>
        <div class="header">
            <h2>Power Consumption Monitoring System (NILM)</h2>
            <div class="header-right">
                <span>Welcome, <strong>${sessionScope.LOGIN_USER.fullName}</strong> (Role: <em>${sessionScope.LOGIN_USER.roleName}</em>)</span>

                <%-- Dùng Form POST cho chức năng Logout --%>
                <form action="${pageContext.request.contextPath}/logout" method="POST" style="margin: 0;">
                    <button type="submit" class="btn-logout">Logout</button>
                </form>
            </div>
        </div>

        <div class="content">
            <h3>Main Dashboard</h3>
            <div class="menu-box">
                <h4>Management Features</h4>
                <c:if test="${sessionScope.LOGIN_USER.roleName == 'ADMIN'}">
                    <a href="UserController">User Management</a>
                </c:if>
                <c:if test="${sessionScope.LOGIN_USER.roleName == 'AUDITOR'}">
                    <a href="UserController">User Management (View only)</a>
                </c:if>
                <c:if test="${sessionScope.LOGIN_USER.roleName == 'ADMIN' or sessionScope.LOGIN_USER.roleName == 'TECHNICIAN'}">
                    <a href="ApplianceController">Appliance Management</a>
                </c:if>
                <a href="#">Appliance Cycle Management</a>
                <a href="#">Reports & Statistics</a>
            </div>
        </div>
    </body>
</html>

