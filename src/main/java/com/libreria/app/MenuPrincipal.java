/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.app;

import com.libreria.view.ConfiguracionNegocioView;
import com.libreria.dao.CategoriaDAO;
import com.libreria.dao.ProductoDAO;
import com.libreria.dao.VentaDAO;
import com.libreria.util.BackupUtil;
import com.libreria.view.RestaurarBackupView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.libreria.dao.ConfiguracionNegocioDAO;
import com.libreria.model.ConfiguracionNegocio;

public class MenuPrincipal extends Application {

    private final Label lblTotalProductos = new Label("0");
    private final Label lblTotalCategorias = new Label("0");
    private final Label lblTotalVentas = new Label("0");
    private final Label lblTotalStockBajo = new Label("0");
    private final Label lblTitulo = new Label("Saavedra's");
    private final Label lblSubtitulo = new Label("Sistema de Gestión de Stock y Ventas");

    @Override
    public void start(Stage primaryStage) {
        ConfiguracionNegocioDAO configDAO = new ConfiguracionNegocioDAO();
        ConfiguracionNegocio config = configDAO.obtenerConfiguracion();

        String nombreSistema = "Saavedra's";

        if (config != null && config.getNombreEmpresa() != null && !config.getNombreEmpresa().trim().isEmpty()) {
            nombreSistema = config.getNombreEmpresa().trim();
        }

        lblTitulo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1f3a5f;");
        lblSubtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #5a5a5a;");

        Label lblVersion = new Label("Versión 1.1.0");
        lblVersion.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");

        VBox cardProductos = crearCardDashboard("Productos", lblTotalProductos);
        VBox cardCategorias = crearCardDashboard("Categorías", lblTotalCategorias);
        VBox cardVentas = crearCardDashboard("Ventas", lblTotalVentas);
        VBox cardStockBajo = crearCardDashboard("Stock bajo", lblTotalStockBajo);

        cardStockBajo.setOnMouseClicked(e -> abrirProductosStockBajo());
        cardStockBajo.setStyle(cardStockBajo.getStyle() + "-fx-cursor: hand;");

        HBox panelDashboard = new HBox(15, cardProductos, cardCategorias, cardVentas, cardStockBajo);
        panelDashboard.setAlignment(Pos.CENTER);

        Button btnProductos = new Button("Gestión de Productos");
        Button btnCategorias = new Button("Gestión de Categorías");
        Button btnVentas = new Button("Gestión de Ventas");
        Button btnHistorialVentas = new Button("Historial de Ventas");
        Button btnConfiguracion = new Button("Configuración negocio");
        Button btnRestaurarBackup = new Button("Restaurar Backup");
        Button btnSalir = new Button("Salir");

        btnProductos.setPrefWidth(260);
        btnCategorias.setPrefWidth(260);
        btnVentas.setPrefWidth(260);
        btnHistorialVentas.setPrefWidth(260);
        btnConfiguracion.setPrefWidth(260);
        btnRestaurarBackup.setPrefWidth(260);
        btnSalir.setPrefWidth(260);

        btnProductos.setPrefHeight(38);
        btnCategorias.setPrefHeight(38);
        btnVentas.setPrefHeight(38);
        btnHistorialVentas.setPrefHeight(38);
        btnConfiguracion.setPrefHeight(38);
        btnRestaurarBackup.setPrefHeight(38);
        btnSalir.setPrefHeight(38);

        String estiloBoton = "-fx-background-color: #e9eef5;"
                + "-fx-text-fill: #1f3a5f;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-border-radius: 8;"
                + "-fx-border-color: #c7d3e3;";

        btnProductos.setStyle(estiloBoton);
        btnCategorias.setStyle(estiloBoton);
        btnVentas.setStyle(estiloBoton);
        btnHistorialVentas.setStyle(estiloBoton);
        btnConfiguracion.setStyle(estiloBoton);
        btnRestaurarBackup.setStyle(estiloBoton);
        btnSalir.setStyle(estiloBoton);

        btnProductos.setOnAction(e -> abrirProductos());
        btnCategorias.setOnAction(e -> abrirCategorias());
        btnVentas.setOnAction(e -> abrirVentas());
        btnHistorialVentas.setOnAction(e -> abrirHistorialVentas());
        btnConfiguracion.setOnAction(e -> new ConfiguracionNegocioView().mostrar());
        btnRestaurarBackup.setOnAction(e -> RestaurarBackupView.mostrar());
        btnSalir.setOnAction(e -> primaryStage.close());

        VBox root = new VBox(18,
                lblTitulo,
                lblSubtitulo,
                lblVersion,
                panelDashboard,
                btnProductos,
                btnCategorias,
                btnVentas,
                btnHistorialVentas,
                btnConfiguracion,
                btnRestaurarBackup,
                btnSalir
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f8fafc, #eef3f9);");

        Scene scene = new Scene(root, 700, 520);
        actualizarNombreNegocio(primaryStage);
        actualizarDashboard();

        primaryStage.setTitle(nombreSistema + " - Sistema de gestión de stock y ventas");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> BackupUtil.crearBackupAutomatico());

        primaryStage.focusedProperty().addListener((obs, estabaEnFoco, ahoraEnFoco) -> {
            if (ahoraEnFoco) {
                actualizarDashboard();
                actualizarNombreNegocio(primaryStage);
            }
        });
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private VBox crearCardDashboard(String titulo, Label lblValor) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #5a5a5a; -fx-font-weight: bold;");

        lblValor.setStyle("-fx-font-size: 24px; -fx-text-fill: #1f3a5f; -fx-font-weight: bold;");

        VBox card = new VBox(8, lblTitulo, lblValor);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefWidth(140);
        card.setStyle("-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-radius: 12;"
                + "-fx-border-color: #d9e2ec;");

        return card;
    }

    private void actualizarDashboard() {
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        ProductoDAO productoDAO = new ProductoDAO();
        VentaDAO ventaDAO = new VentaDAO();

        lblTotalCategorias.setText(String.valueOf(categoriaDAO.contarCategoriasActivas()));
        lblTotalProductos.setText(String.valueOf(productoDAO.contarProductos()));
        lblTotalVentas.setText(String.valueOf(ventaDAO.contarVentas()));
        lblTotalStockBajo.setText(String.valueOf(productoDAO.contarStockBajo()));
    }

    private void abrirProductos() {
        Stage ventana = new Stage();
        MainApp app = new MainApp();

        try {
            app.start(ventana);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirProductosStockBajo() {
        Stage ventana = new Stage();
        MainApp app = new MainApp(true);

        try {
            app.start(ventana);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirCategorias() {
        Stage ventana = new Stage();
        CategoriaView app = new CategoriaView();

        try {
            app.start(ventana);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirVentas() {
        Stage ventana = new Stage();
        VentaView app = new VentaView();

        try {
            app.start(ventana);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirHistorialVentas() {
        Stage ventana = new Stage();
        HistorialVentasView app = new HistorialVentasView();

        try {
            app.start(ventana);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarNombreNegocio(Stage primaryStage) {
        ConfiguracionNegocioDAO configDAO = new ConfiguracionNegocioDAO();
        ConfiguracionNegocio config = configDAO.obtenerConfiguracion();

        String nombreSistema = "Saavedra's";

        if (config != null
                && config.getNombreEmpresa() != null
                && !config.getNombreEmpresa().trim().isEmpty()) {
            nombreSistema = config.getNombreEmpresa().trim();
        }

        lblTitulo.setText(nombreSistema);
        primaryStage.setTitle(nombreSistema + " - Sistema de gestión de stock y ventas");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
