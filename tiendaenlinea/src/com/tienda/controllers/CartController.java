package com.tienda.controllers;



import com.tienda.models.UserSession;
import com.tienda.models.producto;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import com.tienda.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CartController {

    @FXML
    private TableView<producto> cartTable;
    @FXML
    private TableColumn<producto, String> nameColumn;
    @FXML
    private TableColumn<producto, Double> priceColumn;
    @FXML
    private TableColumn<producto, Integer> quantityColumn;
    @FXML
    private TableColumn<producto, String> descriptionColumn;
    @FXML
    private Button purchaseButton;

    private ObservableList<producto> cartList;

    @FXML
    public void initialize() {
        // Asigna las propiedades de las columnas
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Configura el botón de compra
        purchaseButton.setOnAction(event -> handlePurchase());
    }

    public void setCartList(ObservableList<producto> cartList) {
        this.cartList = cartList;
        cartTable.setItems(cartList);
    }

    private void handlePurchase() {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // Inicia una transacción
            int userId;
            try {
                UserSession.getInstance();
                userId = UserSession.getCurrentUserId();
            } catch (IllegalStateException e) {
                // Manejo de error si UserSession no está inicializado
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de Sesión");
                alert.setHeaderText(null);
                alert.setContentText("La sesión del usuario no está inicializada.");
                alert.showAndWait();
                return;
            }

            // Prepara la declaración para insertar en la tabla de ventas
            String insertSaleQuery = "INSERT INTO ventas (user_id, total, date) VALUES (?, ?, ?)";
            PreparedStatement insertSaleStmt = conn.prepareStatement(insertSaleQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            insertSaleStmt.setInt(1, (userId+1));
            double total = cartList.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
            insertSaleStmt.setDouble(2, total);
            insertSaleStmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis())); // Fecha actual
            insertSaleStmt.executeUpdate();

            // Obtiene el ID generado de la venta
            int saleId;
            try (ResultSet generatedKeys = insertSaleStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    saleId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la venta.");
                }
            }

            // Inserta los detalles de la venta
            String insertSaleDetailQuery = "INSERT INTO detalles_venta (sale_id, product_id, quantity) VALUES (?, ?, ?)";
            PreparedStatement insertSaleDetailStmt = conn.prepareStatement(insertSaleDetailQuery);

            for (producto p : cartList) {
                insertSaleDetailStmt.setInt(1, saleId);
                insertSaleDetailStmt.setInt(2, p.getId());
                insertSaleDetailStmt.setInt(3, p.getQuantity());
                insertSaleDetailStmt.addBatch();
            }

            insertSaleDetailStmt.executeBatch();

            // Actualiza el stock de los productos
            String updateProductStockQuery = "UPDATE productos SET stock = stock - ? WHERE id = ?";
            PreparedStatement updateProductStockStmt = conn.prepareStatement(updateProductStockQuery);

            for (producto p : cartList) {
                updateProductStockStmt.setInt(1, p.getQuantity());
                updateProductStockStmt.setInt(2, p.getId());
                updateProductStockStmt.addBatch();
            }

            updateProductStockStmt.executeBatch();

            conn.commit(); // Finaliza la transacción

            // Muestra el mensaje de compra exitosa
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Compra Exitosa");
            alert.setHeaderText(null);
            alert.setContentText("¡La compra se realizó exitosamente!");
            alert.showAndWait();

            // Limpia el carrito después de la compra
            cartList.clear();
            cartTable.setItems(cartList);

        } catch (SQLException e) {
            e.printStackTrace();
            // Muestra un mensaje de error en caso de excepción
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al procesar la compra.");
            alert.showAndWait();
        }
    }
}