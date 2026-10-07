/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SecurityUtils {
    public static boolean isValidDeviceKey(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM Device WHERE api_key_hash = ? AND status = 'ACTIVE'";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, apiKey); 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static final Set<String> PUBLIC_PATHS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    "/login.jsp",
                    "/register.jsp",
                    "/LoginController",
                    "/RegisterController",
                    "/LogoutController",
                    "/logout"
            ))
    );
}