/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import util.DbContext;

public class UserDao {

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.full_name, u.status, r.role_name "
                + "FROM Users u LEFT JOIN UserRole ur ON u.user_id = ur.user_id "
                + "LEFT JOIN Roles r ON ur.role_id = r.role_id ORDER BY u.user_id";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setStatus(rs.getString("status"));
                user.setRoleName(rs.getString("role_name"));
                users.add(user);
            }
        }
        return users;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT u.user_id, u.username, u.full_name, u.status, r.role_name "
                + "FROM Users u LEFT JOIN UserRole ur ON u.user_id = ur.user_id "
                + "LEFT JOIN Roles r ON ur.role_id = r.role_id WHERE u.user_id = ?";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullName(rs.getString("full_name"));
                    user.setStatus(rs.getString("status"));
                    user.setRoleName(rs.getString("role_name"));
                    return user;
                }
            }
        }
        return null;
    }

    public List<String> getAllRoles() throws SQLException {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT role_name FROM Roles ORDER BY role_name";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(rs.getString("role_name"));
            }
        }
        return roles;
    }

    public boolean usernameExists(String username, int exceptUserId) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE username = ? AND user_id <> ?";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, exceptUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void saveManagedUser(User user, String passwordHash) throws SQLException {
        try (Connection conn = DbContext.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (user.getUserId() == 0) {
                    String sql = "INSERT INTO Users (username, password_hash, full_name, status) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, user.getUsername());
                        ps.setString(2, passwordHash);
                        ps.setString(3, user.getFullName());
                        ps.setString(4, user.getStatus());
                        ps.executeUpdate();
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (!rs.next()) {
                                throw new SQLException("Unable to retrieve the new user ID");
                            }
                            user.setUserId(rs.getInt(1));
                        }
                    }
                } else {
                    String sql = passwordHash == null
                            ? "UPDATE Users SET username = ?, full_name = ?, status = ? WHERE user_id = ?"
                            : "UPDATE Users SET username = ?, full_name = ?, status = ?, password_hash = ? WHERE user_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, user.getUsername());
                        ps.setString(2, user.getFullName());
                        ps.setString(3, user.getStatus());
                        if (passwordHash == null) {
                            ps.setInt(4, user.getUserId());
                        } else {
                            ps.setString(4, passwordHash);
                            ps.setInt(5, user.getUserId());
                        }
                        if (ps.executeUpdate() != 1) {
                            throw new SQLException("User not found");
                        }
                    }
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM UserRole WHERE user_id = ?")) {
                        ps.setInt(1, user.getUserId());
                        ps.executeUpdate();
                    }
                }

                String roleSql = "INSERT INTO UserRole (user_id, role_id) "
                        + "SELECT ?, role_id FROM Roles WHERE role_name = ?";
                try (PreparedStatement ps = conn.prepareStatement(roleSql)) {
                    ps.setInt(1, user.getUserId());
                    ps.setString(2, user.getRoleName());
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Selected role does not exist");
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean deleteManagedUser(int userId) throws SQLException {
        try (Connection conn = DbContext.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM UserRole WHERE user_id = ?")) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
                int deleted;
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Users WHERE user_id = ?")) {
                    ps.setInt(1, userId);
                    deleted = ps.executeUpdate();
                }
                conn.commit();
                return deleted == 1;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public User checkLogin(String username, String password) throws SQLException {
        String sql = "SELECT u.user_id, u.username, u.password_hash, u.full_name, r.role_name "
                + "FROM Users u JOIN UserRole ur ON u.user_id = ur.user_id "
                + "JOIN Roles r ON r.role_id = ur.role_id "
                + "WHERE u.username = ? AND u.status = 'ACTIVE'";
        try ( Connection conn = DbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next() && BCrypt.checkpw(password, rs.getString("password_hash"))) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setFullName(rs.getString("full_name"));
                    user.setRoleName(rs.getString("role_name"));
                    return user;
                }
            }
        }
        return null;
    }

    public boolean checkUserNameExists(String username) {
        String sql = "SELECT 1 FROM Users WHERE username = ?";
        try ( Connection conn = DbContext.getConnection();  PreparedStatement ptm = conn.prepareStatement(sql)) {
            ptm.setString(1, username);
            try ( ResultSet rs = ptm.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User registerUser(User user) {
    String sqlUser = "INSERT INTO Users (username, password_hash, full_name) VALUES (?, ?, ?)";
    String sqlRole = "INSERT INTO UserRole (user_id, role_id) "
            + "SELECT ?, role_id FROM Roles WHERE role_name = 'END_USER'";
    try (Connection conn = DbContext.getConnection()) {
        conn.setAutoCommit(false);
        try {
            int newId;
            try (PreparedStatement ps = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPasswordHash());
                ps.setString(3, user.getFullName());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return null;
                    }
                    newId = rs.getInt(1);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlRole)) {
                ps.setInt(1, newId);
                ps.executeUpdate();
            }
            conn.commit();
            user.setUserId(newId);
            user.setRoleName("END_USER");
            return user;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}
}
