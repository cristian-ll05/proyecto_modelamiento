package com.tienda.models;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class producto {
    private int id;
    private String name;
    private double price;
    private int stock;
    private int quantity; // Nueva propiedad para la cantidad en el carrito
    private String imagenURL;
    private String descripcion;
    private ImageView imageView;

    // Constructor con parámetros
    public producto(String name, double price, int stock, int quantity, String imagenURL, String descripcion) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.quantity = quantity;
        this.imageView = new ImageView(new Image(imagenURL));
        this.imageView.setFitHeight(100);
        this.imageView.setFitWidth(100);
        this.descripcion = descripcion;
        
    }

    // Constructor sin parámetros
    public producto() {}

    // Getters y Setters
    public int getId() { return id+1; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getimagenURL(){ return imagenURL;}
    public void setimagenURL(String imagenUrl) { this.imagenURL = imagenUrl; }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    
}
