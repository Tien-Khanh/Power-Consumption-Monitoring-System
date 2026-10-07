/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author TIEN KHANH
 */
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class DbContext {
    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=PRJ301_NILM;encrypt=false";
    private static final String USER = "sa";
    private static final String PASSWORD = "12345";
 
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC driver not found in WEB-INF/lib", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
