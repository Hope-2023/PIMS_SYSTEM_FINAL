package models;

public class SaleItem {
    private int saleItemId;
    private int saleId;
    private int medicineId;
    private int quantitySold;
    private double priceAtSale;

    public SaleItem(int saleItemId, int saleId, int medicineId, int quantitySold, double priceAtSale) {
        this.saleItemId = saleItemId;
        this.saleId = saleId;
        this.medicineId = medicineId;
        this.quantitySold = quantitySold;
        this.priceAtSale = priceAtSale;
    }

    public int getSaleItemId() { return saleItemId; }
    public int getSaleId() { return saleId; }
    public int getMedicineId() { return medicineId; }
    public int getQuantitySold() { return quantitySold; }
    public double getPriceAtSale() { return priceAtSale; }
}