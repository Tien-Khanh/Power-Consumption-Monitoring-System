<%-- 
    Document   : nilm_form
    Created on : Oct 7, 2026, 10:47:06 AM
    Author     : hoanh
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${editing ? 'Edit' : 'Add'} Appliance - NILM System</title>
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
            .content {
                padding: 20px;
                display: flex;
                justify-content: center;
            }
            .form-card {
                background: white;
                padding: 25px 30px;
                border-radius: 6px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                width: 100%;
                max-width: 480px;
            }
            .form-card h3 {
                margin-top: 0;
                color: #1f77b4;
            }
            .error-box {
                background: #f8d7da;
                color: #842029;
                border: 1px solid #f5c2c7;
                padding: 10px 14px;
                border-radius: 4px;
                margin-bottom: 15px;
                font-size: 14px;
            }
            .form-group {
                margin-bottom: 16px;
            }
            .form-group label {
                display: block;
                margin-bottom: 6px;
                font-weight: bold;
                color: #333;
                font-size: 14px;
            }
            .form-group input {
                width: 100%;
                padding: 9px 10px;
                border: 1px solid #ccc;
                border-radius: 4px;
                font-size: 14px;
                box-sizing: border-box;
            }
            .form-group input:focus {
                outline: none;
                border-color: #1f77b4;
            }
            .form-actions {
                display: flex;
                gap: 10px;
                margin-top: 22px;
            }
            .btn-save {
                background-color: #28a745;
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 4px;
                font-weight: bold;
                cursor: pointer;
                font-size: 14px;
            }
            .btn-save:hover {
                background-color: #218838;
            }
            .btn-cancel {
                background-color: #e9ecef;
                color: #333;
                text-decoration: none;
                padding: 10px 20px;
                border-radius: 4px;
                font-weight: bold;
                font-size: 14px;
            }
            .btn-cancel:hover {
                background-color: #dde1e4;
            }
        </style>
    </head>
    <body>
        <div class="header">
            <h2>NILM System</h2>
        </div>
 
        <div class="content">
            <div class="form-card">
                <h3>${editing ? 'Edit Appliance' : '+ Add New Appliance'}</h3>
 
                <c:if test="${not empty error}">
                    <div class="error-box">${error}</div>
                </c:if>
 
                <c:url var="cancelUrl" value="ApplianceController">
                    <c:param name="page" value="${empty param.page ? 1 : param.page}" />
                    <c:if test="${param.search == '1'}">
                        <c:param name="search" value="1" />
                        <c:param name="householdId" value="${param.householdId}" />
                        <c:param name="room" value="${param.room}" />
                        <c:param name="q" value="${param.q}" />
                    </c:if>
                </c:url>
                <form method="post" action="ApplianceController">
                    <input type="hidden" name="action" value="${editing ? 'update' : 'create'}">
                    <c:if test="${editing}">
                        <input type="hidden" name="id" value="<c:out value='${appliance.appliance_id}' />">
                    </c:if>
                    <input type="hidden" name="page" value="${empty param.page ? 1 : param.page}">
                    <c:if test="${param.search == '1'}">
                        <input type="hidden" name="search" value="1">
                        <input type="hidden" name="householdId" value="<c:out value='${param.householdId}' />">
                        <input type="hidden" name="room" value="<c:out value='${param.room}' />">
                        <input type="hidden" name="q" value="<c:out value='${param.q}' />">
                    </c:if>
 
                    <div class="form-group">
                        <label for="appliance_name">Appliance name</label>
                        <input type="text" id="appliance_name" name="appliance_name"
                               maxlength="50" required value="<c:out value='${param.appliance_name != null ? param.appliance_name : appliance.appliance_name}' />">
                    </div>
 
                    <div class="form-group">
                        <label for="room">Room</label>
                        <input type="text" id="room" name="room"
                               maxlength="30" required value="<c:out value='${editing ? appliance.room : param.room}' />">
                    </div>
 
                    <div class="form-group">
                        <label for="household_id">Household ID</label>
                        <input type="number" id="household_id" name="household_id"
                               min="1" required value="<c:out value='${param.household_id != null ? param.household_id : appliance.household_id}' />">
                    </div>
 
                    <div class="form-group">
                        <label for="rated_w">Rated power (W)</label>
                        <input type="number" id="rated_w" name="rated_w"
                               min="0.1" step="0.1" required value="<c:out value='${param.rated_w != null ? param.rated_w : appliance.rated_w}' />">
                    </div>
 
                    <div class="form-actions">
                        <button type="submit" class="btn-save">Save</button>
                        <a href="${cancelUrl}" class="btn-cancel">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>
