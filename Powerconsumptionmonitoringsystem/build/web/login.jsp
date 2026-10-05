<%-- 
    Document   : login
    Created on : Sep 26, 2026, 4:30:05 PM
    Author     : TIEN KHANH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Login - Power Consumption Monitoring System</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 50px; background-color: #f4f6f9; display: flex; justify-content: center; }
        .login-box { width: 320px; padding: 25px; border: 1px solid #ddd; border-radius: 8px; background: white; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        .login-box h2 { text-align: center; margin-bottom: 20px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; font-size: 14px; }
        .form-group input { width: 100%; padding: 8px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
        .error { color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 10px; border-radius: 4px; margin-bottom: 15px; font-size: 14px; }
        .success { color: #155724; background-color: #d4edda; border: 1px solid #c3e6cb; padding: 10px; border-radius: 4px; margin-bottom: 15px; font-size: 14px; }
        button { width: 100%; padding: 10px; background-color: #1f77b4; color: white; border: none; border-radius: 4px; font-size: 16px; cursor: pointer; }
        button:hover { background-color: #155d8f; }
    </style>
</head>
<body>
    <div class="login-box">
        <h2>Account Login</h2>
        
        <%-- 1. Hiển thị thông báo LỖI (Ví dụ: Sai username/password) --%>
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <%-- 2. Hiển thị thông báo THÀNH CÔNG (Ví dụ: Đăng ký thành công hoặc Đăng xuất thành công) --%>
        <c:if test="${not empty param.message}">
            <div class="success">${param.message}</div>
        </c:if>

        <form action="LoginController" method="POST">
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" value="${param.username}" required>
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit">Login</button>
        </form>
        
        <p style="margin-top: 15px; font-size: 14px; text-align: center;">
            Don't have an account? <a href="register.jsp">Register here</a>
        </p>
    </div>
</body>
</html>

