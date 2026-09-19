package dao;

import config.DatabaseConnection;
import java.sql.*;

public class SupplierDAO {
    public ResultSet getAllSuppliers() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM suppliers");
    }
}