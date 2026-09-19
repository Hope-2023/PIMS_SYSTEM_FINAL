package ui.panels;

import config.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageMedicinesPanel extends JPanel {
    private JTable medTable = new JTable();
    private DefaultTableModel medModel = new DefaultTableModel(new String[]{"ID", "Name", "Type", "Price", "Stock", "Expiry"}, 0);
    private JTextField txtMedName = new JTextField(10), txtPrice = new JTextField(5), txtStock = new JTextField(5);

    public ManageMedicinesPanel() {
        setLayout(new BorderLayout());
        medTable.setModel(medModel);
        add(new JScrollPane(medTable), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(txtMedName);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(txtPrice);
        inputPanel.add(new JLabel("Stock:"));
        inputPanel.add(txtStock);

        JButton btnAdd = new JButton("Add Medicine");
        btnAdd.addActionListener(e -> addMedicine());
        inputPanel.add(btnAdd);

        add(inputPanel, BorderLayout.SOUTH);
        loadMedicineData();
    }

    private void loadMedicineData() {
        medModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM medicines");
            while (rs.next()) {
                medModel.addRow(new Object[]{
                    rs.getInt("medicine_id"),
                    rs.getString("name"),
                    rs.getString("medicine_type"),
                    rs.getDouble("price"),
                    rs.getInt("quantity_in_stock"),
                    rs.getDate("expiry_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMedicine() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO medicines (name, price, quantity_in_stock, reorder_level, expiry_date) VALUES (?, ?, ?, 10, '2027-01-01')";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtMedName.getText());
            pstmt.setDouble(2, Double.parseDouble(txtPrice.getText()));
            pstmt.setInt(3, Integer.parseInt(txtStock.getText()));
            pstmt.executeUpdate();
            loadMedicineData();
            JOptionPane.showMessageDialog(this, "Medicine Added!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding item: " + ex.getMessage());
        }
    }
}