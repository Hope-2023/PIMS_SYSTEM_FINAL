package dao;

import config.DatabaseConnection;
import java.sql.*;

public class MedicineDAO {
    public ResultSet getAllMedicines() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM medicines");
    }
}