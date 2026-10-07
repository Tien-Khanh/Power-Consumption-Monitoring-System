/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import util.DbContext;

public class AuditDao {
    public void append(int actor, String action, String objectType, long objectId, String diff) throws SQLException {
        String sql = "{CALL Audit_Append(?, ?, ?, ?, ?)}";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, actor);
            ps.setString(2, action);
            ps.setString(3, objectType);
            ps.setLong(4, objectId);
            ps.setString(5, diff);
            ps.executeUpdate();
        }
    }
}
