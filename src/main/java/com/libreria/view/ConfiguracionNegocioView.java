/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.view;

import com.libreria.dao.ConfiguracionNegocioDAO;
import com.libreria.model.ConfiguracionNegocio;
import com.libreria.util.Mensajes;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ConfiguracionNegocioView {

    private TextField txtNombreEmpresa = new TextField();
    private TextField txtDireccion = new TextField();

    public void mostrar() {

        ConfiguracionNegocioDAO dao = new ConfiguracionNegocioDAO();
        ConfiguracionNegocio config = dao.obtenerConfiguracion();

        if (config != null) {
            txtNombreEmpresa.setText(config.getNombreEmpresa());
            txtDireccion.setText(config.getDireccion());
        }

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Nombre empresa:"), 0, 0);
        grid.add(txtNombreEmpresa, 1, 0);

        grid.add(new Label("Dirección:"), 0, 1);
        grid.add(txtDireccion, 1, 1);

        Button btnGuardar = new Button("Guardar");

        Stage stage = new Stage();

        btnGuardar.setOnAction(e -> {

            ConfiguracionNegocio nueva = new ConfiguracionNegocio();

            nueva.setNombreEmpresa(txtNombreEmpresa.getText());
            nueva.setDireccion(txtDireccion.getText());
    
            boolean ok = dao.guardarOActualizar(nueva);

            if (ok) {
                Mensajes.mostrarInfo("Configuración", "Datos guardados correctamente.");
                stage.close();
            } else {
                Mensajes.mostrarError("Error", "No se pudieron guardar los datos.");
            }

        });

        grid.add(btnGuardar, 1, 5);

        Scene scene = new Scene(grid, 450, 300);

        stage.setTitle("Configuración del negocio");
        stage.setScene(scene);
        stage.show();
    }
}
