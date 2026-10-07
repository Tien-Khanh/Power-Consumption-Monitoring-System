package controller;

import dao.UserDao;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "UserController", urlPatterns = {"/UserController"})
public class UserController extends HttpServlet {

    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            if ("add".equals(action) || "edit".equals(action)) {
                if (!isAdmin(request)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                showForm(request, response, action);
                return;
            }
            request.setAttribute("userList", userDao.getAllUsers());
            request.getRequestDispatcher("/user_list.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (!"save".equals(action) && !"delete".equals(action)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            if ("delete".equals(action)) {
                deleteUser(request, response);
            } else {
                saveUser(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int userId = parseId(request.getParameter("userId"));
        if (userId <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
            return;
        }
        if (isCurrentUser(request, userId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You cannot delete your own account");
            return;
        }
        if (!userDao.deleteManagedUser(userId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/UserController?message=deleted");
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, String action)
            throws SQLException, ServletException, IOException {
        boolean editing = "edit".equals(action);
        User user = new User();
        if (editing) {
            int userId = parseId(request.getParameter("id"));
            user = userId > 0 ? userDao.getUserById(userId) : null;
            if (user == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } else {
            user.setStatus("ACTIVE");
        }
        request.setAttribute("user", user);
        request.setAttribute("editing", editing);
        request.setAttribute("roles", userDao.getAllRoles());
        request.setAttribute("protectAccess", isCurrentUser(request, user.getUserId()));
        request.getRequestDispatcher("/user_form.jsp").forward(request, response);
    }

    private void saveUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int userId = parseId(request.getParameter("userId"));
        User user = userId > 0 ? userDao.getUserById(userId) : new User();
        if (user == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String username = trim(request.getParameter("username"));
        String fullName = trim(request.getParameter("fullName"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String roleName = trim(request.getParameter("roleName"));
        String status = trim(request.getParameter("status"));

        if (username.isEmpty() || fullName.isEmpty() || roleName.isEmpty()
                || (!"ACTIVE".equals(status) && !"INACTIVE".equals(status))) {
            showFormError(request, response, user, "Please complete all required fields.");
            return;
        }
        if (userId == 0 && (password == null || password.isEmpty())) {
            showFormError(request, response, user, "A password is required for a new account.");
            return;
        }
        if ((password != null && !password.isEmpty()) && !password.equals(confirmPassword)) {
            showFormError(request, response, user, "Passwords do not match.");
            return;
        }
        if (userDao.usernameExists(username, userId)) {
            showFormError(request, response, user, "That username is already in use.");
            return;
        }

        List<String> roles = userDao.getAllRoles();
        if (!roles.contains(roleName)) {
            showFormError(request, response, user, "Please select a valid role.");
            return;
        }
        if (userId > 0 && isCurrentUser(request, userId)) {
            if (!roleName.equals(user.getRoleName()) || !status.equals(user.getStatus())) {
                showFormError(request, response, user, "You cannot change your own role or account status.");
                return;
            }
        }

        user.setUsername(username);
        user.setFullName(fullName);
        user.setRoleName(roleName);
        user.setStatus(status);
        String passwordHash = password == null || password.isEmpty()
                ? null : BCrypt.hashpw(password, BCrypt.gensalt());
        userDao.saveManagedUser(user, passwordHash);
        response.sendRedirect(request.getContextPath() + "/UserController?message=saved");
    }

    private void showFormError(HttpServletRequest request, HttpServletResponse response, User user, String error)
            throws SQLException, ServletException, IOException {
        user.setUsername(trim(request.getParameter("username")));
        user.setFullName(trim(request.getParameter("fullName")));
        user.setRoleName(trim(request.getParameter("roleName")));
        user.setStatus(trim(request.getParameter("status")));
        request.setAttribute("user", user);
        request.setAttribute("editing", user.getUserId() > 0);
        request.setAttribute("protectAccess", isCurrentUser(request, user.getUserId()));
        request.setAttribute("roles", userDao.getAllRoles());
        request.setAttribute("error", error);
        request.getRequestDispatcher("/user_form.jsp").forward(request, response);
    }

    private boolean isCurrentUser(HttpServletRequest request, int userId) {
        HttpSession session = request.getSession(false);
        User currentUser = session == null ? null : (User) session.getAttribute("LOGIN_USER");
        return currentUser != null && currentUser.getUserId() == userId;
    }

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User currentUser = session == null ? null : (User) session.getAttribute("LOGIN_USER");
        return currentUser != null && "ADMIN".equals(currentUser.getRoleName());
    }

    private int parseId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}