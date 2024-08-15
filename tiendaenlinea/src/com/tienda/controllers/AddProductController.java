package com.tienda.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tienda.models.producto;
import com.tienda.utils.Database;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddProductController {

    @FXML
    private TextField productNameField;

    @FXML
    private TextField productPriceField;

    @FXML
    private TextField productStockField;

    @FXML
    private TextField productImageUrlField;

    @FXML
    private TextField productDescripcion;

    private MAinController mainController;

    // Este método será llamado cuando se haga clic en el botón "Añadir Producto"
    @FXML
    private void handleAddProduct() {
        String nombre = productNameField.getText().trim();
        String precioText = productPriceField.getText().trim();
        String stockText = productStockField.getText().trim();
        String imagen = productImageUrlField.getText().trim();
        String descripcion = productDescripcion.getText().trim();

        if (nombre.isEmpty() || precioText.isEmpty() || stockText.isEmpty() || imagen.isEmpty() || descripcion.isEmpty()) {
            showAlert(AlertType.ERROR, "Formulario incompleto", "Por favor, complete todos los campos.");
            return;
        }

        double precio;
        int stock;
        int cantidad = 0;

        try {
            precio = Double.parseDouble(precioText);
            stock = Integer.parseInt(stockText);
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Entrada inválida", "Por favor, ingrese valores válidos para el precio y el stock.");
            return;
        }

        producto producto = new producto(nombre,precio,stock,cantidad,imagen,descripcion);
        mainController.getProductTable().getItems().add(producto);

        try (Connection connection = Database.getConnection()) {
            String sql = "INSERT INTO productos (name, price, stock, imagenURL, descripcion) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nombre);
                statement.setDouble(2, precio);
                statement.setInt(3, stock);
                statement.setString(4, imagen);
                statement.setString(5, descripcion);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Cerrar la ventana de añadir producto
        Stage stage = (Stage) productNameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Método para establecer el controlador principal
    public void setMainController(MAinController mainController) {
        this.mainController = mainController;
    }

    @FXML
   public void handleBackToLogin(ActionEvent event) {
    // Volver al inicio de sesión
    Node source = (Node) event.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    stage.close();
    }
}
