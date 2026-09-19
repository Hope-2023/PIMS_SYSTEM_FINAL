package dao;

import config.DatabaseConnection;
import java.sql.*;

public class SaleDAO {
    public ResultSet getAllSales() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM sales");
    }
}