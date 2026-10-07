/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.AuditDao;
import dao.UserDao;
import javax.servlet.annotation.WebServlet;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;

/**
 *
 * @author hoanh
 */
@WebServlet(name = "RegisterController", urlPatterns = {"/RegisterController", "/register"})
public class RegisterController extends HttpServlet {

    private UserDao userDao;
    private AuditDao auditDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDao();
        auditDao = new AuditDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");

        username = (username != null) ? username.trim() : "";
        fullName = (fullName != null) ? fullName.trim() : "";

        if (username.isEmpty() || password == null || password.isEmpty()
                || fullName.isEmpty()) {
            request.setAttribute("error", "All fields are required!");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match!");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (userDao.checkUserNameExists(username)) {
            request.setAttribute("error", "Username already exists. Please choose another one.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        String passWordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        User newUser = new User(0, username, passWordHash, fullName);

        User registeredUser = userDao.registerUser(newUser);

        if (registeredUser != null) {
            try {
                auditDao.append(registeredUser.getUserId(), "REGISTER", "USER", registeredUser.getUserId(), "User registered successfully");
            } catch (Exception e) {
                e.printStackTrace();
            }

            request.setAttribute("message", "Registration successful! Please log in.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Registration failed due to a system error. Please try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
