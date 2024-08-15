package com.tienda.controllers;


import com.tienda.utils.Database;
import com.tienda.models.Usuario;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tienda.models.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;


public class LoginController {
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasena;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsuario.getText();
        String password = txtContrasena.getText();

        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario user = new Usuario();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                UserSession.getInstance(user);

                Stage stage = (Stage) txtUsuario.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/com/tienda/fxml/main.fxml"));
                stage.setScene(new Scene(root, 800, 600));
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Usuario o contraseña incorrectos");
                alert.show();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/tienda/fxml/registro.fxml"));
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}