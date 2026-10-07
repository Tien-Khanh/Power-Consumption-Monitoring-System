/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.AuditDao;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;

/**
 *
 * @author hoanh
 */

@WebServlet(name = "LogoutController", urlPatterns = {"/LogoutController", "/logout"})
public class LogoutController extends HttpServlet {
    
    private AuditDao auditDao;
    
    @Override
    public void init() throws ServletException{
        auditDao = new AuditDao();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("LOGIN_USER");
            
            if (user != null) {
                try {
                    auditDao.append(user.getUserId(), "LOGOUT", "USER", user.getUserId(), "User logged out successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login.jsp?message=You+have+been+logged+out+successfully.");
    }
}
