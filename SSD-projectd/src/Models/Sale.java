package Models;

public class Sale {
    private int saleId;
    private int productId;
    private int quantitySold;
    private double totalPrice;

    public Sale(int saleId, int productId, int quantitySold, double totalPrice) {
        this.saleId = saleId;
        this.productId = productId;
        this.quantitySold = quantitySold;
        this.totalPrice = totalPrice;
    }

    public int getSaleId() { return saleId; }
    public int getProductId() { return productId; }
    public int getQuantitySold() { return quantitySold; }
    public double getTotalPrice() { return totalPrice; }
}
