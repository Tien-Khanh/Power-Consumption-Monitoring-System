<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><c:choose><c:when test="${editing}">Edit User</c:when><c:otherwise>Add User</c:otherwise></c:choose> - NILM System</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background: #f5f7f9; color: #263238; }
        .header { background: #1f77b4; color: #fff; padding: 15px 20px; }
        .header h2 { margin: 0; font-size: 20px; }
        .content { max-width: 620px; margin: 24px auto; padding: 0 18px; }
        .top-actions { margin-bottom: 16px; }
        a { color: #1769aa; text-decoration: none; font-weight: bold; }
        form { background: #fff; border: 1px solid #d8dee3; padding: 20px; }
        .field { margin-bottom: 15px; }
        label { display: block; margin-bottom: 6px; font-weight: bold; }
        input, select { box-sizing: border-box; width: 100%; min-height: 38px; padding: 8px 10px; border: 1px solid #aeb9c2; border-radius: 3px; font: inherit; }
        input:focus, select:focus { outline: 2px solid #8ac4e8; border-color: #1f77b4; }
        .hint { margin-top: 5px; color: #687780; font-size: 13px; }
        .error { padding: 10px 12px; margin-bottom: 16px; background: #fff0ef; color: #a33b35; border: 1px solid #e8b7b3; }
        .actions { display: flex; gap: 10px; margin-top: 20px; }
        .button { display: inline-block; border: 0; border-radius: 4px; padding: 9px 14px; font: inherit; font-weight: bold; cursor: pointer; }
        .save { color: #fff; background: #218838; }
        .cancel { color: #263238; background: #e8eef2; }
        @media (max-width: 640px) { .content { margin: 16px auto; } form { padding: 16px; } }
    </style>
</head>
<body>
    <header class="header"><h2>Power Consumption Monitoring System (NILM)</h2></header>
    <main class="content">
        <div class="top-actions"><a href="${pageContext.request.contextPath}/UserController">Back to User Management</a></div>
        <h3><c:choose><c:when test="${editing}">Edit User</c:when><c:otherwise>Add User</c:otherwise></c:choose></h3>
        <c:if test="${not empty error}"><div class="error"><c:out value="${error}"/></div></c:if>
        <form action="${pageContext.request.contextPath}/UserController" method="POST">
            <input type="hidden" name="action" value="save">
            <input type="hidden" name="userId" value="<c:out value='${user.userId}'/>">
            <div class="field">
                <label for="username">Username</label>
                <input id="username" name="username" type="text" maxlength="100" required value="<c:out value='${user.username}'/>">
            </div>
            <div class="field">
                <label for="fullName">Full name</label>
                <input id="fullName" name="fullName" type="text" maxlength="200" required value="<c:out value='${user.fullName}'/>">
            </div>
            <div class="field">
                <label for="password">Password <c:if test="${editing}">(leave blank to keep current)</c:if></label>
                <input id="password" name="password" type="password" autocomplete="new-password" <c:if test="${not editing}">required</c:if>>
            </div>
            <div class="field">
                <label for="confirmPassword">Confirm password</label>
                <input id="confirmPassword" name="confirmPassword" type="password" autocomplete="new-password">
            </div>
            <div class="field">
                <label for="roleName">Role</label>
                <c:choose>
                    <c:when test="${protectAccess}">
                        <input type="hidden" name="roleName" value="<c:out value='${user.roleName}'/>">
                        <select id="roleName" disabled>
                            <option selected><c:out value="${user.roleName}"/></option>
                        </select>
                    </c:when>
                    <c:otherwise>
                        <select id="roleName" name="roleName" required>
                            <option value="">Select a role</option>
                            <c:forEach items="${roles}" var="role">
                                <option value="<c:out value='${role}'/>" <c:if test="${role == user.roleName}">selected</c:if>><c:out value="${role}"/></option>
                            </c:forEach>
                        </select>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="field">
                <label for="status">Account status</label>
                <c:choose>
                    <c:when test="${protectAccess}">
                        <input type="hidden" name="status" value="<c:out value='${user.status}'/>">
                        <select id="status" disabled><option selected><c:out value="${user.status}"/></option></select>
                    </c:when>
                    <c:otherwise>
                        <select id="status" name="status" required>
                            <option value="ACTIVE" <c:if test="${user.status == 'ACTIVE'}">selected</c:if>>ACTIVE</option>
                            <option value="INACTIVE" <c:if test="${user.status == 'INACTIVE'}">selected</c:if>>INACTIVE</option>
                        </select>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="actions">
                <button type="submit" class="button save">Save</button>
                <a class="button cancel" href="${pageContext.request.contextPath}/UserController">Cancel</a>
            </div>
        </form>
    </main>
</body>
</html>