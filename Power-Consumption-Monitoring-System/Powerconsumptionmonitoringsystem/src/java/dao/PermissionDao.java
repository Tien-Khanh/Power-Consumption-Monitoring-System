/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DbContext;

public class PermissionDao {
    public boolean hasPermission(int userId, String resource, String action) {
        String sql = "SELECT 1 FROM Users u "
                   + "JOIN UserRole ur ON u.user_id = ur.user_id "
                   + "JOIN RolePerm rp ON ur.role_id = rp.role_id "
                   + "JOIN Permissions p ON rp.perm_id = p.perm_id "
                   + "WHERE u.user_id = ? AND p.resource = ? AND p.action = ?";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, resource);
            ps.setString(3, action);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}