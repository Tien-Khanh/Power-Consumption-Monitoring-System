/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.AuditDao;
import dao.UserDao;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.User;

/**
 *
 * @author hoanh
 */
@WebServlet(name = "LoginController", urlPatterns = {"/LoginController", "/login"})
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            User user = new UserDao().checkLogin(req.getParameter("username"), req.getParameter("password"));
            if (user == null) {
                req.setAttribute("error", "Invalid username or password");
                req.getRequestDispatcher("login.jsp").forward(req, res);
                return;
            }
            if ("INACTIVE".equals(user.getStatus())) {
                req.setAttribute("error", "INACTIVE ACCOUNT!");
                req.getRequestDispatcher("login.jsp").forward(req, res);
                return;
            }
            req.getSession().setAttribute("LOGIN_USER", user);
            new AuditDao().append(user.getUserId(), "LOGIN", "USER", user.getUserId(), null);
            res.sendRedirect("DashboardController");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

