package models;

import java.sql.Timestamp;

public class Sale {
    private int saleId;
    private Timestamp saleDate;
    private double totalAmount;
    private int userId;

    public Sale(int saleId, Timestamp saleDate, double totalAmount, int userId) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
        this.userId = userId;
    }

    public int getSaleId() { return saleId; }
    public Timestamp getSaleDate() { return saleDate; }
    public double getTotalAmount() { return totalAmount; }
    public int getUserId() { return userId; }
}