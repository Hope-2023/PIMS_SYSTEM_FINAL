package ui.panels;

import config.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReportsPanel extends JPanel {
    public ReportsPanel() {
        setLayout(new FlowLayout());
        JButton btnExpiry = new JButton("Generate Expiry Report (30 Days)");
        
        btnExpiry.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "SELECT * FROM medicines WHERE expiry_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY)";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                StringBuilder sb = new StringBuilder("Medicines Expiring Soon:\n");
                while(rs.next()) {
                    sb.append(rs.getString("name")).append(" - Exp: ").append(rs.getDate("expiry_date")).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        
        add(btnExpiry);
    }
}