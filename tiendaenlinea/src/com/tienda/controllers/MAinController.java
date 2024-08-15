package com.tienda.controllers;


import com.tienda.utils.Database;
import com.tienda.models.producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MAinController {
    @FXML
    private TableView<producto> productTable;
    @FXML
    private TableColumn<producto, String> nameColumn;
    @FXML
    private TableColumn<producto, Double> priceColumn;
    @FXML
    private TableColumn<producto, Integer> quantityColumn;
    @FXML
    private TableColumn<producto, Integer> stockColumn;
    @FXML
    private TableColumn<producto, ImageView> imageColumn;
    @FXML
    private TableColumn<producto,String> descripcionColumn;
   

    private ObservableList<producto> productList = FXCollections.observableArrayList();
    private ObservableList<producto> cartList = FXCollections.observableArrayList();

    @FXML
public void initialize() {
    // Configuración de las otras columnas
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
    descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

    // Configuración de la columna de imagen
    imageColumn.setCellFactory(column -> new TableCell<producto, ImageView>() {
        private final ImageView imageView = new ImageView();
       
        {
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
        }

        @Override
        protected void updateItem(ImageView item, boolean empty) {
            super.updateItem(item, empty);
        
            if (empty) {
                // Si la celda está vacía, no se muestra ninguna imagen
                setGraphic(null);
            } else {
                // Si la celda no está vacía, actualiza la imagen
                producto currentProduct = getTableView().getItems().get(getIndex());
                try {
                    imageView.setImage(new Image(currentProduct.getimagenURL(), true));
                } catch (Exception e) {
                    e.printStackTrace(); // Imprime cualquier error al cargar la imagen
                }
                setGraphic(imageView);
            }
        }
        
    });
        loadProducts();
    }

    private void loadProducts() {
        productTable.getItems().clear(); // Limpia los productos existentes
        productList.clear(); // Limpia la lista observable de productos

        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM productos";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                producto product = new producto();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setStock(rs.getInt("stock"));
                product.setimagenURL(rs.getString("imagenURL"));
                product.setDescripcion(rs.getString("descripcion"));
                productList.add(product);
            }
            productTable.setItems(productList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

@FXML
private void handleAddToCart(ActionEvent event) {
    producto selectedProduct = productTable.getSelectionModel().getSelectedItem();
    if (selectedProduct != null && selectedProduct.getStock() > 0) {
        boolean alreadyInCart = false;

        // Revisa si el producto ya está en el carrito
        for (producto p : cartList) {
            if (p.getName().equals(selectedProduct.getName())) {
                // Incrementa la cantidad en el carrito
                p.setQuantity(p.getQuantity() + 1);
                alreadyInCart = true;
                break;
            }
        }

        if (!alreadyInCart) {
            // Añade el producto al carrito con cantidad 1
            producto newProduct = new producto(
                selectedProduct.getName(),
                selectedProduct.getPrice(),
                selectedProduct.getStock(),
                1,
                selectedProduct.getimagenURL(),
                selectedProduct.getDescripcion()
            );
            cartList.add(newProduct);
        } else {
            // Actualiza la cantidad en el carrito
            selectedProduct.setStock(selectedProduct.getStock() - 1);
        }

        productTable.refresh();
    } else {
        // Mostrar mensaje si el producto no tiene stock
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText("El producto seleccionado no tiene stock disponible.");
        alert.showAndWait();
    }
}

    @FXML
    private void handleShowCart(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/fxml/cart.fxml"));
            Parent root = loader.load();
            CartController cartController = loader.getController();
            cartController.setCartList(cartList);

            Stage stage = new Stage();
            stage.setTitle("Carrito de Compras");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleDeleteProduct() {
        // Obtener el producto seleccionado
        producto selectedProduct = productTable.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            // Eliminar el producto de la base de datos
            try (Connection connection = Database.getConnection()) {
                String sql = "DELETE FROM productos WHERE name = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, selectedProduct.getName()); // Suponiendo que Producto tiene un método getId()
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Error", "No se pudo eliminar el producto.");
                return;
            }
            

            // Eliminar el producto de la lista y actualizar la tabla
            productList.remove(selectedProduct);
            showAlert(AlertType.INFORMATION, "Éxito", "Producto eliminado exitosamente.");
        } else {
            // Mostrar advertencia si no hay producto seleccionado
            showAlert(AlertType.WARNING, "Seleccionar Producto", "Por favor, selecciona un producto para eliminar.");
        }
    }
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleEditProduct() {
        // Lógica para editar el producto seleccionado
        producto selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Editar Producto");
            alert.setHeaderText(null);
            alert.setContentText("Editar producto aún no implementado.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Seleccionar Producto");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona un producto para editar.");
            alert.showAndWait();
        }
    }
    @FXML
    private void handleAddProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/fxml/add_product.fxml"));
            Parent root = loader.load();

            AddProductController controller = loader.getController();
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Añadir Producto");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TableView<producto> getProductTable() {
        return productTable;
    }
}

