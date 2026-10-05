<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Management - NILM System</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background: #f5f7f9; color: #263238; }
        .header { background: #1f77b4; color: #fff; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; }
        .header h2 { margin: 0; font-size: 20px; }
        .header-right { display: flex; align-items: center; gap: 15px; }
        .btn-logout { background: #d9534f; color: #fff; border: 0; padding: 7px 12px; border-radius: 4px; font-weight: bold; cursor: pointer; }
        .content { padding: 20px; }
        .top-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
        a { text-decoration: none; }
        .btn-back { color: #1f77b4; font-weight: bold; }
        .btn-add { background: #218838; color: #fff; padding: 9px 14px; border-radius: 4px; font-weight: bold; }
        .notice { padding: 10px 12px; margin: 12px 0; background: #e7f4ea; color: #216e39; border: 1px solid #b8dfc1; }
        table { width: 100%; border-collapse: collapse; background: #fff; }
        th, td { border: 1px solid #d8dee3; padding: 11px 12px; text-align: left; }
        th { background: #e8eef2; color: #263238; }
        tr:nth-child(even) { background: #fafbfc; }
        .status-active { color: #217a3c; font-weight: bold; }
        .status-inactive { color: #a33b35; font-weight: bold; }
        .edit-link { color: #1769aa; font-weight: bold; }
        .action-form { display: inline; margin-left: 8px; }
        .delete-button { border: 0; padding: 0; background: none; color: #a33b35; font: inherit; font-weight: bold; cursor: pointer; }
        @media (max-width: 700px) {
            .header { align-items: flex-start; gap: 12px; flex-direction: column; }
            .header-right { width: 100%; justify-content: space-between; }
            .content { padding: 14px; overflow-x: auto; }
            table { min-width: 620px; }
        }
    </style>
</head>
<body>
    <div class="header">
        <h2>Power Consumption Monitoring System (NILM)</h2>
        <div class="header-right">
            <span>Welcome, <strong><c:out value="${sessionScope.LOGIN_USER.fullName}"/></strong></span>
            <form action="${pageContext.request.contextPath}/logout" method="POST" style="margin: 0;">
                <button type="submit" class="btn-logout">Logout</button>
            </form>
        </div>
    </div>
    <main class="content">
        <div class="top-actions">
            <a href="${pageContext.request.contextPath}/DashboardController" class="btn-back">Back to Dashboard</a>
            <c:if test="${sessionScope.LOGIN_USER.roleName == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/UserController?action=add" class="btn-add">Add User</a>
            </c:if>
        </div>
        <h3>User Management</h3>
        <c:if test="${param.message == 'saved'}"><div class="notice">User information saved.</div></c:if>
        <c:if test="${param.message == 'deleted'}"><div class="notice">User account deleted.</div></c:if>
        <table>
            <thead>
                <tr><th>ID</th><th>Username</th><th>Full name</th><th>Role</th><th>Status</th><th>Actions</th></tr>
            </thead>
            <tbody>
                <c:forEach items="${userList}" var="account">
                    <tr>
                        <td><c:out value="${account.userId}"/></td>
                        <td><c:out value="${account.username}"/></td>
                        <td><c:out value="${account.fullName}"/></td>
                        <td><c:out value="${account.roleName}"/></td>
                        <td class="${account.status == 'ACTIVE' ? 'status-active' : 'status-inactive'}"><c:out value="${account.status}"/></td>
                        <td>
                            <c:if test="${sessionScope.LOGIN_USER.roleName == 'ADMIN'}">
                                <a class="edit-link" href="${pageContext.request.contextPath}/UserController?action=edit&amp;id=${account.userId}">Edit</a>
                                <c:if test="${sessionScope.LOGIN_USER.userId != account.userId}">
                                    <form class="action-form" action="${pageContext.request.contextPath}/UserController" method="POST"
                                          onsubmit="return confirm('Delete this user account? This action cannot be undone.');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="userId" value="<c:out value='${account.userId}'/>">
                                        <button type="submit" class="delete-button">Delete</button>
                                    </form>
                                </c:if>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty userList}">
                    <tr><td colspan="6" style="text-align: center; color: #687780;">No user accounts found.</td></tr>
                </c:if>
            </tbody>
        </table>
    </main>
</body>
</html>