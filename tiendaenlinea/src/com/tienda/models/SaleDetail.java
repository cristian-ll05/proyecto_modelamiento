package com.tienda.models;


public class SaleDetail {
    private int saleId;
    private int productId;
    private int quantity;

    public SaleDetail(int saleId, int productId, int quantity){
        this.saleId = saleId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getSaleId() {
        return saleId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    
}
