/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.app;

import com.libreria.dao.CategoriaDAO;
import com.libreria.dao.ProductoDAO;
import com.libreria.model.Categoria;
import com.libreria.model.Producto;
import com.libreria.util.Mensajes;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProductoFormView {

    private final Stage stage = new Stage();

    private final TextField txtCodigo = new TextField();
    private final TextField txtCodigoBarras = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtDescripcion = new TextField();
    private final TextField txtPrecioCompra = new TextField();
    private final TextField txtPrecioVenta = new TextField();
    private final TextField txtStock = new TextField();
    private final TextField txtStockMinimo = new TextField();
    private final ComboBox<Categoria> comboCategoria = new ComboBox<>();
    private final Label lblCostoConIva = new Label("0.00");

    private Producto producto;
    private final Runnable alGuardar;

    public ProductoFormView(Producto producto, Runnable alGuardar) {
        this.producto = producto;
        this.alGuardar = alGuardar;
        inicializar();
    }

    private void inicializar() {
        cargarCategorias();

        txtCodigo.setPrefWidth(240);
        txtCodigoBarras.setPrefWidth(240);
        txtNombre.setPrefWidth(240);
        txtDescripcion.setPrefWidth(240);
        txtPrecioCompra.setPrefWidth(240);
        txtPrecioVenta.setPrefWidth(240);
        txtStock.setPrefWidth(240);
        txtStockMinimo.setPrefWidth(240);
        comboCategoria.setPrefWidth(240);

        txtPrecioCompra.textProperty().addListener((obs, oldVal, newVal) -> actualizarCostoConIva());

        Button btnGuardar = new Button("Guardar");
        Button btnCancelar = new Button("Cancelar");

        btnGuardar.setPrefWidth(120);
        btnCancelar.setPrefWidth(120);

        btnGuardar.setOnAction(e -> guardar());
        btnCancelar.setOnAction(e -> stage.close());

        GridPane form = new GridPane();
        form.setPadding(new Insets(15));
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Código:"), 0, 0);
        form.add(txtCodigo, 1, 0);

        form.add(new Label("Código de barras:"), 0, 1);
        form.add(txtCodigoBarras, 1, 1);

        form.add(new Label("Nombre:"), 0, 2);
        form.add(txtNombre, 1, 2);

        form.add(new Label("Descripción:"), 0, 3);
        form.add(txtDescripcion, 1, 3);

        form.add(new Label("Precio Compra:"), 0, 4);
        form.add(txtPrecioCompra, 1, 4);

        form.add(new Label("Costo + IVA 21%:"), 0, 5);
        form.add(lblCostoConIva, 1, 5);

        form.add(new Label("Precio Venta:"), 0, 6);
        form.add(txtPrecioVenta, 1, 6);

        form.add(new Label("Stock:"), 0, 7);
        form.add(txtStock, 1, 7);

        form.add(new Label("Stock Mínimo:"), 0, 8);
        form.add(txtStockMinimo, 1, 8);

        form.add(new Label("Categoría:"), 0, 9);
        form.add(comboCategoria, 1, 9);

        HBox botones = new HBox(10, btnGuardar, btnCancelar);
        form.add(botones, 1, 10);

        if (producto != null) {
            cargarDatos();
            stage.setTitle("Modificar producto");
        } else {
            stage.setTitle("Nuevo producto");
        }

        Scene scene = new Scene(form, 500, 470);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    private void cargarCategorias() {
        CategoriaDAO dao = new CategoriaDAO();
        List<Categoria> categorias = dao.listarCategorias();
        comboCategoria.getItems().clear();
        comboCategoria.getItems().addAll(categorias);
    }

    private void cargarDatos() {
        txtCodigo.setText(producto.getCodigo());
        txtCodigoBarras.setText(producto.getCodigoBarras() != null ? producto.getCodigoBarras() : "");
        txtNombre.setText(producto.getNombre());
        txtDescripcion.setText(producto.getDescripcion());
        txtPrecioCompra.setText(String.valueOf(producto.getPrecioCompra()));
        txtPrecioVenta.setText(String.valueOf(producto.getPrecioVenta()));
        txtStock.setText(String.valueOf(producto.getStock()));
        txtStockMinimo.setText(String.valueOf(producto.getStockMinimo()));

        for (Categoria c : comboCategoria.getItems()) {
            if (c.getIdCategoria() == producto.getIdCategoria()) {
                comboCategoria.setValue(c);
                break;
            }
        }
        actualizarCostoConIva();
    }

    private void guardar() {
        try {
            if (txtCodigo.getText() == null || txtCodigo.getText().trim().isEmpty()) {
                Mensajes.mostrarError("Error", "El código es obligatorio.");
                return;
            }

            if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
                Mensajes.mostrarError("Error", "El nombre es obligatorio.");
                return;
            }

            if (txtPrecioCompra.getText() == null || txtPrecioCompra.getText().trim().isEmpty()) {
                Mensajes.mostrarError("Error", "El precio de compra es obligatorio.");
                return;
            }

            if (txtPrecioVenta.getText() == null || txtPrecioVenta.getText().trim().isEmpty()) {
                Mensajes.mostrarError("Error", "El precio de venta es obligatorio.");
                return;
            }

            if (txtStock.getText() == null || txtStock.getText().trim().isEmpty()) {
                Mensajes.mostrarError("Error", "El stock es obligatorio.");
                return;
            }

            if (txtStockMinimo.getText() == null || txtStockMinimo.getText().trim().isEmpty()) {
                Mensajes.mostrarError("Error", "El stock mínimo es obligatorio.");
                return;
            }

            Categoria categoria = comboCategoria.getValue();
            if (categoria == null) {
                Mensajes.mostrarError("Error", "Debe seleccionar una categoría.");
                return;
            }

            if (producto == null) {
                producto = new Producto();
                producto.setActivo(true);
            }

            producto.setCodigo(txtCodigo.getText().trim());

            String textoCodigoBarras = txtCodigoBarras.getText();
            String codigoBarras = (textoCodigoBarras == null) ? "" : textoCodigoBarras.trim();

            if (codigoBarras.isEmpty()) {
                producto.setCodigoBarras(null);
            } else {
                producto.setCodigoBarras(codigoBarras);
            }

            producto.setNombre(txtNombre.getText().trim());
            producto.setDescripcion(txtDescripcion.getText().trim());
            producto.setPrecioCompra(Double.parseDouble(txtPrecioCompra.getText().trim()));
            producto.setPrecioVenta(Double.parseDouble(txtPrecioVenta.getText().trim()));
            producto.setStock(Integer.parseInt(txtStock.getText().trim()));
            producto.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));
            producto.setIdCategoria(categoria.getIdCategoria());

            if (producto.getPrecioCompra() < 0 || producto.getPrecioVenta() < 0) {
                Mensajes.mostrarError("Error", "Los precios no pueden ser negativos.");
                return;
            }

            if (producto.getStock() < 0 || producto.getStockMinimo() < 0) {
                Mensajes.mostrarError("Error", "El stock no puede ser negativo.");
                return;
            }

            ProductoDAO dao = new ProductoDAO();
            boolean ok;

            if (producto.getIdProducto() == 0) {
                ok = dao.agregarProducto(producto);
            } else {
                ok = dao.modificarProducto(producto);
            }

            if (ok) {

                if (alGuardar != null) {
                    alGuardar.run();
                }

                limpiarFormulario();
                txtCodigo.requestFocus();

            } else {
                Mensajes.mostrarError("Error", "No se pudo guardar el producto.");
            }

        } catch (NumberFormatException ex) {
            Mensajes.mostrarError("Error", "Precio, stock y stock mínimo deben ser números válidos.");
        } catch (Exception ex) {
            ex.printStackTrace();
            Mensajes.mostrarError("Error", "Ocurrió un error inesperado:\n" + ex.getMessage());
        }
    }

    private void actualizarCostoConIva() {
        try {
            String texto = txtPrecioCompra.getText();

            if (texto == null || texto.trim().isEmpty()) {
                lblCostoConIva.setText("0.00");
                return;
            }

            double costo = Double.parseDouble(texto.trim());
            double costoConIva = costo * 1.21;

            lblCostoConIva.setText(String.format("%.2f", costoConIva));

        } catch (NumberFormatException e) {
            lblCostoConIva.setText("0.00");
        }
    }

    private void limpiarFormulario() {

        txtCodigo.clear();
        txtCodigoBarras.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecioCompra.clear();
        txtPrecioVenta.clear();
        txtStock.clear();
        txtStockMinimo.clear();

        comboCategoria.getSelectionModel().clearSelection();

        producto = null;
    }

    public void mostrar() {
        stage.showAndWait();
    }
}
