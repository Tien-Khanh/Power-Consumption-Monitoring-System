/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Appliance;
import util.DbContext;

/**
 *
 * @author hoanh
 */
public class ApplianceDao {

    public List<Appliance> getALLAppliances() {
        List<Appliance> list = new ArrayList<>();
        String sql = "SELECT appliance_id, appliance_name, household_id, rated_w, room, created_at FROM Appliance";
        try ( Connection conn = DbContext.getConnection();  
              PreparedStatement ptm = conn.prepareStatement(sql); 
              ResultSet rs = ptm.executeQuery()) {

            while (rs.next()) {
                Appliance app = new Appliance();
                app.setAppliance_id(rs.getInt("appliance_id"));
                app.setAppliance_name(rs.getString("appliance_name"));
                app.setHousehold_id(rs.getInt("household_id"));
                app.setRated_w(rs.getFloat("rated_w"));
                app.setRoom(rs.getString("room"));
                app.setCreatedAt(rs.getTimestamp("created_at"));

                list.add(app);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
