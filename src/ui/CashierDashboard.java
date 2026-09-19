package ui;

import config.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CashierDashboard extends JFrame {
    private int cashierId;
    private DefaultTableModel cartModel = new DefaultTableModel(new String[]{"Med ID", "Name", "Qty", "Price", "Total"}, 0);
    private JTable cartTable = new JTable(cartModel);
    private JTextField txtMedId = new JTextField(5), txtQty = new JTextField(5);
    private JLabel lblTotal = new JLabel("Total: R0.00");
    private double grandTotal = 0.0;

    public CashierDashboard(int cashierId) {
        this.cashierId = cashierId;
        setTitle("PIMS - Point of Sale (Cashier)");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Medicine ID:"));
        topPanel.add(txtMedId);
        topPanel.add(new JLabel("Qty:"));
        topPanel.add(txtQty);
        JButton btnAddToCart = new JButton("Add to Cart");
        topPanel.add(btnAddToCart);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(lblTotal);
        JButton btnCheckout = new JButton("Checkout & Print Bill");
        bottomPanel.add(btnCheckout);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        btnAddToCart.addActionListener(e -> addToCart());
        btnCheckout.addActionListener(e -> checkout());
    }

    private void addToCart() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            int medId = Integer.parseInt(txtMedId.getText().trim());
            int qty = Integer.parseInt(txtQty.getText().trim());

            String sql = "SELECT name, price, quantity_in_stock FROM medicines WHERE medicine_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, medId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("quantity_in_stock");

                if (qty > stock) {
                    JOptionPane.showMessageDialog(this, "Not enough stock!");
                    return;
                }

                double lineTotal = price * qty;
                grandTotal += lineTotal;
                cartModel.addRow(new Object[]{medId, name, qty, price, lineTotal});
                lblTotal.setText("Total: R" + String.format("%.2f", grandTotal));
            } else {
                JOptionPane.showMessageDialog(this, "Item not found.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void checkout() {
        if (cartModel.getRowCount() == 0) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            String saleSql = "INSERT INTO sales (total_amount, user_id) VALUES (?, ?)";
            PreparedStatement pstmtSale = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS);
            pstmtSale.setDouble(1, grandTotal);
            pstmtSale.setInt(2, cashierId);
            pstmtSale.executeUpdate();

            ResultSet keys = pstmtSale.getGeneratedKeys();
            keys.next();
            int saleId = keys.getInt(1);

            String itemSql = "INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES (?, ?, ?, ?)";
            String updateStockSql = "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? WHERE medicine_id = ?";

            PreparedStatement pstmtItem = conn.prepareStatement(itemSql);
            PreparedStatement pstmtStock = conn.prepareStatement(updateStockSql);

            for (int i = 0; i < cartModel.getRowCount(); i++) {
                int medId = (int) cartModel.getValueAt(i, 0);
                int qty = (int) cartModel.getValueAt(i, 2);
                double price = (double) cartModel.getValueAt(i, 3);

                pstmtItem.setInt(1, saleId);
                pstmtItem.setInt(2, medId);
                pstmtItem.setInt(3, qty);
                pstmtItem.setDouble(4, price);
                pstmtItem.addBatch();

                pstmtStock.setInt(1, qty);
                pstmtStock.setInt(2, medId);
                pstmtStock.addBatch();
            }

            pstmtItem.executeBatch();
            pstmtStock.executeBatch();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Sale Processed! Total Bill: R" + grandTotal);
            
            cartModel.setRowCount(0);
            grandTotal = 0;
            lblTotal.setText("Total: R0.00");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Transaction failed: " + ex.getMessage());
        }
    }
}