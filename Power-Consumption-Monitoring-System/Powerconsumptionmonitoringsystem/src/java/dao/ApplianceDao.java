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
import model.Appliance;
import util.DbContext;

/**
 *
 * @author hoanh
 */
public class ApplianceDao {

    public Appliance findById(int id) throws SQLException {
        String sql = "SELECT * FROM Appliance WHERE appliance_id = ?";
        try ( Connection conn = DbContext.getConnection();  PreparedStatement ptm = conn.prepareStatement(sql)) {
            ptm.setInt(1, id);
            try ( ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    Appliance app = new Appliance();
                    app.setAppliance_id(rs.getInt("appliance_id"));
                    app.setAppliance_name(rs.getString("appliance_name"));
                    app.setHousehold_id(rs.getInt("household_id"));
                    app.setRated_w(rs.getFloat("rated_w"));
                    app.setRoom(rs.getString("room"));
                    app.setCreatedAt(rs.getTimestamp("created_at"));
                }
            }
        }
        return null;
    }

    public List<Appliance> search(String keyword, int page, int pageSize) throws SQLException {
        String sql = "SELECT * FROM Appliance WHERE appliance_name LIKE ? ORDER BY appliance_id "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        List<Appliance> items = new ArrayList<>();
        try ( Connection conn = DbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Appliance item = new Appliance();
                    item.setAppliance_id(rs.getInt("appliance_id"));
                    item.setAppliance_name(rs.getString("appliance_name"));
                    item.setHousehold_id(rs.getInt("household_id"));
                    item.setRated_w(rs.getFloat("rated_w"));
                    item.setRoom(rs.getString("room"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public int insert(Appliance item) throws SQLException {
        String sql = "INSERT INTO Appliance (appliance_name, household_id, rated_w, room) VALUES (?, ?, ?, ?)";
        try ( Connection conn = DbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getAppliance_name());
            ps.setInt(2, item.getHousehold_id());
            ps.setDouble(3, item.getRated_w());
            ps.setString(4, item.getRoom());
            ps.executeUpdate();
            try ( ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public void update(Appliance item) throws SQLException {
        String sql = "UPDATE Appliance SET appliance_name = ?, household_id = ?, rated_w = ?, room = ? "
                + "WHERE appliance_id = ?";
        try ( Connection conn = DbContext.getConnection();  PreparedStatement ptm = conn.prepareStatement(sql)) {
            ptm.setString(1, item.getAppliance_name());
            ptm.setInt(2, item.getHousehold_id());
            ptm.setDouble(3, item.getRated_w());
            ptm.setString(4, item.getRoom());
            ptm.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Appliance WHERE appliance_id = ?";
        try ( Connection conn = DbContext.getConnection();  PreparedStatement ptm = conn.prepareCall(sql)) {
            ptm.setInt(1, id);
            ptm.executeUpdate();
        }
    }
}
