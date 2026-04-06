/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.app;

import com.libreria.dao.VentaDAO;
import com.libreria.model.DetalleVenta;
import com.libreria.model.VentaResumen;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class DetalleVentaView {

    public void mostrar(VentaResumen ventaResumen) {
        VentaDAO ventaDAO = new VentaDAO();
        List<DetalleVenta> detalles = ventaDAO.obtenerDetalleVenta(ventaResumen.getIdVenta());

        Stage stage = new Stage();

        Label lblTitulo = new Label("Detalle de venta - ID: " + ventaResumen.getIdVenta());

        TableView<DetalleVenta> tablaDetalle = new TableView<>();

        TableColumn<DetalleVenta, String> colProducto = new TableColumn<>("Producto");
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colProducto.setPrefWidth(220);

        TableColumn<DetalleVenta, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidad.setPrefWidth(100);

        TableColumn<DetalleVenta, Double> colPrecio = new TableColumn<>("Precio Unitario");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitarioSinIva"));
        colPrecio.setPrefWidth(150);

        TableColumn<DetalleVenta, Double> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotalLinea"));
        colSubtotal.setPrefWidth(150);

        tablaDetalle.getColumns().addAll(colProducto, colCantidad, colPrecio, colSubtotal);
        tablaDetalle.setItems(FXCollections.observableArrayList(detalles));
        tablaDetalle.setPrefHeight(300);

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> stage.close());

        VBox root = new VBox(10, lblTitulo, tablaDetalle, btnCerrar);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 580, 420);
        stage.setScene(scene);
        stage.setTitle("Detalle de Venta");
        stage.show();
    }
}
