package ui;

import config.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    // --- Medicines Components ---
    private JTextField txtName = new JTextField(10);
    private JTextField txtType = new JTextField(8);
    private JTextField txtPrice = new JTextField(6);
    private JTextField txtStock = new JTextField(6);
    private JButton btnAddMedicine = new JButton("Add Medicine");
    private JTable medTable;
    private DefaultTableModel medModel;

    // --- Suppliers Components ---
    private JTextField txtSupName = new JTextField(10);
    private JTextField txtSupContact = new JTextField(10);
    private JTextField txtSupPhone = new JTextField(8);
    private JTextField txtSupEmail = new JTextField(10);
    private JButton btnAddSupplier = new JButton("Add Supplier");
    private JTable supTable;
    private DefaultTableModel supModel;

    // --- Users Components ---
    private JTextField txtUsername = new JTextField(8);
    private JPasswordField txtPassword = new JPasswordField(8);
    private JTextField txtFullName = new JTextField(10);
    private JComboBox<String> cmbRole = new JComboBox<>(new String[]{"Admin", "Cashier"});
    private JButton btnAddUser = new JButton("Add User");
    private JTable userTable;
    private DefaultTableModel userModel;

    public AdminDashboard() {
        setTitle("PIMS - Administrator Dashboard");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Manage Medicines", createManageMedicinesPanel());
        tabbedPane.addTab("Manage Suppliers", createManageSuppliersPanel());
        tabbedPane.addTab("Manage Users", createManageUsersPanel());
        tabbedPane.addTab("Reports", new JPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Load initial data from database tables
        loadMedicines();
        loadSuppliers();
        loadUsers();
    }

    // ==========================================
    // 1. MANAGE MEDICINES
    // ==========================================
    private JPanel createManageMedicinesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        medModel = new DefaultTableModel(new String[]{
                "ID", "Name", "Type", "Price", "Stock", "Expiry"
        }, 0);
        medTable = new JTable(medModel);
        panel.add(new JScrollPane(medTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        bottomPanel.add(new JLabel("Name:"));
        bottomPanel.add(txtName);
        bottomPanel.add(new JLabel("Type:"));
        bottomPanel.add(txtType);
        bottomPanel.add(new JLabel("Price:"));
        bottomPanel.add(txtPrice);
        bottomPanel.add(new JLabel("Stock:"));
        bottomPanel.add(txtStock);
        bottomPanel.add(btnAddMedicine);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        btnAddMedicine.addActionListener(e -> addMedicine());

        return panel;
    }

    private void addMedicine() {
        String name = txtName.getText().trim();
        String type = txtType.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String stockStr = txtStock.getText().trim();

        if (name.isEmpty() || type.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all medicine fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            String sql = "INSERT INTO medicines (name, medicine_type, price, quantity_in_stock, expiry_date) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, name);
                pstmt.setString(2, type);
                pstmt.setDouble(3, price);
                pstmt.setInt(4, stock);
                pstmt.setDate(5, java.sql.Date.valueOf("2027-01-01"));

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Medicine Added Successfully!");
                clearMedicineFields();
                loadMedicines();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and Stock must be valid numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMedicines() {
        medModel.setRowCount(0);
        String sql = "SELECT medicine_id, name, medicine_type, price, quantity_in_stock, expiry_date FROM medicines";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading medicines: " + ex.getMessage());
        }
    }

    private void clearMedicineFields() {
        txtName.setText("");
        txtType.setText("");
        txtPrice.setText("");
        txtStock.setText("");
    }

    // ==========================================
    // 2. MANAGE SUPPLIERS
    // ==========================================
    private JPanel createManageSuppliersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        supModel = new DefaultTableModel(new String[]{
                "Supplier ID", "Name", "Contact Person", "Phone", "Email"
        }, 0);
        supTable = new JTable(supModel);
        panel.add(new JScrollPane(supTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        bottomPanel.add(new JLabel("Name:"));
        bottomPanel.add(txtSupName);
        bottomPanel.add(new JLabel("Contact:"));
        bottomPanel.add(txtSupContact);
        bottomPanel.add(new JLabel("Phone:"));
        bottomPanel.add(txtSupPhone);
        bottomPanel.add(new JLabel("Email:"));
        bottomPanel.add(txtSupEmail);
        bottomPanel.add(btnAddSupplier);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        btnAddSupplier.addActionListener(e -> addSupplier());

        return panel;
    }

    private void addSupplier() {
        String name = txtSupName.getText().trim();
        String contact = txtSupContact.getText().trim();
        String phone = txtSupPhone.getText().trim();
        String email = txtSupEmail.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Supplier Name and Phone are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO suppliers (name, contact_person, phone, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Supplier Added Successfully!");
            clearSupplierFields();
            loadSuppliers();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSuppliers() {
        supModel.setRowCount(0);
        String sql = "SELECT supplier_id, name, contact_person, phone, email FROM suppliers";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                supModel.addRow(new Object[]{
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact_person"),
                        rs.getString("phone"),
                        rs.getString("email")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading suppliers: " + ex.getMessage());
        }
    }

    private void clearSupplierFields() {
        txtSupName.setText("");
        txtSupContact.setText("");
        txtSupPhone.setText("");
        txtSupEmail.setText("");
    }

    // ==========================================
    // 3. MANAGE USERS
    // ==========================================
    private JPanel createManageUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        userModel = new DefaultTableModel(new String[]{
                "User ID", "Username", "Full Name", "Role"
        }, 0);
        userTable = new JTable(userModel);
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        bottomPanel.add(new JLabel("Username:"));
        bottomPanel.add(txtUsername);
        bottomPanel.add(new JLabel("Password:"));
        bottomPanel.add(txtPassword);
        bottomPanel.add(new JLabel("Full Name:"));
        bottomPanel.add(txtFullName);
        bottomPanel.add(new JLabel("Role:"));
        bottomPanel.add(cmbRole);
        bottomPanel.add(btnAddUser);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        btnAddUser.addActionListener(e -> addUser());

        return panel;
    }

    private void addUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String fullName = txtFullName.getText().trim();
        String role = cmbRole.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all user fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, role);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User Added Successfully!");
            clearUserFields();
            loadUsers();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUsers() {
        userModel.setRowCount(0);
        String sql = "SELECT user_id, username, full_name, role FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                userModel.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("role")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }
    }

    private void clearUserFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtFullName.setText("");
        cmbRole.setSelectedIndex(0);
    }
}