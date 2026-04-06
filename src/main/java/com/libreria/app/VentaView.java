/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.app;

import com.libreria.dao.ProductoDAO;
import com.libreria.dao.VentaDAO;
import com.libreria.model.ItemVenta;
import com.libreria.model.Producto;
import com.libreria.util.Mensajes;
import com.libreria.util.TicketVentaUtil;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import javafx.scene.input.KeyEvent;
import java.util.ArrayList;

public class VentaView extends Application {

    private final TableView<Producto> tablaProductos = new TableView<>();
    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    private final TableView<ItemVenta> tablaCarrito = new TableView<>();
    private final ObservableList<ItemVenta> listaCarrito = FXCollections.observableArrayList();

    // Búsqueda manual
    private final TextField txtBuscar = new TextField();

    // Escaneo separado
    private final TextField txtEscanearCodigo = new TextField();

    private final TextField txtCantidad = new TextField();
    private final TextField txtDescuento = new TextField();
    private final TextField txtRecargo = new TextField();

    // Método de pago y cuotas
    private final ComboBox<String> comboMetodoPago = new ComboBox<>();
    private final ComboBox<Integer> comboCuotas = new ComboBox<>();

    private final Label lblSubtotal = new Label("0.00");
    private final Label lblDescuento = new Label("0.00");
    private final Label lblRecargo = new Label("0.00");
    private final Label lblTotal = new Label("0.00");

    private Producto productoSeleccionado = null;

    private String bufferCantidadRapida = "";

    @Override
    public void start(Stage stage) {

        // TABLA PRODUCTOS
        TableColumn<Producto, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getCodigo()));
        colCodigo.setComparator((a, b) -> {
            try {
                int numA = Integer.parseInt(a);
                int numB = Integer.parseInt(b);
                return Integer.compare(numA, numB);
            } catch (Exception e) {
                return a.compareTo(b);
            }
        });
        TableColumn<Producto, String> colCodigoBarras = new TableColumn<>("Código Barras");
        colCodigoBarras.setCellValueFactory(data
                -> new SimpleStringProperty(
                        data.getValue().getCodigoBarras() != null
                        ? data.getValue().getCodigoBarras()
                        : ""
                ));

        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getNombre()));

        TableColumn<Producto, String> colDescripcion = new TableColumn<>("Descripción");

        colDescripcion.setCellValueFactory(data
                -> new SimpleStringProperty(
                        data.getValue().getDescripcion() != null
                        ? data.getValue().getDescripcion()
                        : ""
                ));
        TableColumn<Producto, String> colPrecio = new TableColumn<>("Precio Venta");
        colPrecio.setCellValueFactory(data
                -> new SimpleStringProperty(String.format("%.2f", data.getValue().getPrecioVenta())));

        TableColumn<Producto, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(data
                -> new SimpleIntegerProperty(data.getValue().getStock()).asObject());

        tablaProductos.getColumns().addAll(colCodigo, colCodigoBarras, colNombre, colDescripcion, colPrecio, colStock);
        tablaProductos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaProductos.setPrefHeight(300);
        tablaProductos.setItems(listaProductos);
        tablaProductos.setOnMouseClicked(e -> {
            productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();

            if (e.getClickCount() == 2 && productoSeleccionado != null) {
                agregarProductoRapido(productoSeleccionado, 1);
            }
        });
        // TABLA CARRITO
        TableColumn<ItemVenta, String> colCarritoCodigo = new TableColumn<>("Código");

        colCarritoCodigo.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getProducto().getCodigo()));

        TableColumn<ItemVenta, String> colCarritoNombre = new TableColumn<>("Producto");

        colCarritoNombre.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getProducto().getNombre()));

        TableColumn<ItemVenta, Integer> colCarritoCantidad = new TableColumn<>("Cantidad");

        colCarritoCantidad.setCellValueFactory(data
                -> new SimpleIntegerProperty(data.getValue().getCantidad()).asObject());

        TableColumn<ItemVenta, String> colCarritoPrecio = new TableColumn<>("Precio Venta");

        colCarritoPrecio.setCellValueFactory(data
                -> new SimpleStringProperty(String.format("%.2f", data.getValue().getProducto().getPrecioVenta())));

        TableColumn<ItemVenta, String> colCarritoSubtotal = new TableColumn<>("Subtotal");

        colCarritoSubtotal.setCellValueFactory(data
                -> new SimpleStringProperty(String.format("%.2f", data.getValue().getSubtotalSinIva())));

        tablaCarrito.getColumns()
                .addAll(
                        colCarritoCodigo,
                        colCarritoNombre,
                        colCarritoCantidad,
                        colCarritoPrecio,
                        colCarritoSubtotal
                );
        tablaCarrito.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaCarrito.setPrefHeight(300);
        tablaCarrito.setItems(listaCarrito);

        // BOTONES
        Button btnBuscar = new Button("Buscar");
        Button btnLimpiarBusqueda = new Button("Limpiar búsqueda");
        Button btnEscanear = new Button("Agregar por escaneo");
        Button btnAgregar = new Button("Agregar manual");
        Button btnQuitar = new Button("Quitar item");
        Button btnConfirmarVenta = new Button("Confirmar venta");

        String estiloBotonSecundario
                = "-fx-background-color: #e9eef5;"
                + "-fx-text-fill: #1f3a5f;"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 6;"
                + "-fx-border-radius: 6;"
                + "-fx-border-color: #c7d3e3;";

        String estiloBotonPrincipal
                = "-fx-background-color: #1f3a5f;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-border-radius: 8;";

        btnBuscar.setStyle(estiloBotonSecundario);
        btnLimpiarBusqueda.setStyle(estiloBotonSecundario);
        btnEscanear.setStyle(estiloBotonSecundario);
        btnAgregar.setStyle(estiloBotonSecundario);
        btnQuitar.setStyle(estiloBotonSecundario);
        btnConfirmarVenta.setStyle(estiloBotonPrincipal);

        // EVENTOS
        btnBuscar.setOnAction(e -> buscarProductos());
        btnLimpiarBusqueda.setOnAction(e -> limpiarBusqueda());

        btnEscanear.setOnAction(e -> agregarPorEscaneo());
        txtEscanearCodigo.setOnAction(e -> agregarPorEscaneo()); // ENTER del lector o teclado

        btnAgregar.setOnAction(e -> agregarAlCarrito());
        btnQuitar.setOnAction(e -> quitarDelCarrito());
        btnConfirmarVenta.setOnAction(e -> confirmarVenta());
        txtCantidad.setOnAction(e -> agregarMultiples());

        // CONFIG CAMPOS
        txtBuscar.setPrefWidth(220);
        txtEscanearCodigo.setPrefWidth(220);
        txtCantidad.setPrefWidth(100);
        txtDescuento.setPrefWidth(100);
        txtRecargo.setPrefWidth(100);

        txtBuscar.setPromptText("Buscar por nombre, código o barra");
        txtEscanearCodigo.setPromptText("Escanear código de barras");

        txtEscanearCodigo.setStyle(
                "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-color: #f4f8fb;"
                + "-fx-border-color: #1f3a5f;"
                + "-fx-border-radius: 6;"
                + "-fx-background-radius: 6;"
        );

        txtDescuento.setText("0");
        txtRecargo.setText("0");

        comboMetodoPago.getItems().addAll("Efectivo", "Transferencia", "Débito", "Crédito");
        comboMetodoPago.setValue("Efectivo");
        comboMetodoPago.setPrefWidth(150);

        comboCuotas.getItems().addAll(1, 3, 6, 12);
        comboCuotas.setValue(1);
        comboCuotas.setPrefWidth(100);
        comboCuotas.setDisable(true);

        comboMetodoPago.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean esCredito = "Crédito".equalsIgnoreCase(newVal);
            comboCuotas.setDisable(!esCredito);
            if (!esCredito) {
                comboCuotas.setValue(1);
            }
        });

        txtDescuento.textProperty().addListener((obs, oldVal, newVal) -> actualizarTotales());
        txtRecargo.textProperty().addListener((obs, oldVal, newVal) -> actualizarTotales());
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarProductos());

        Label lblBuscar = new Label("Búsqueda de productos");
        lblBuscar.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1f3a5f;");

        Label lblEscaneo = new Label("Escaneo rápido");
        lblEscaneo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1f3a5f;");

        // BARRA DE BÚSQUEDA
        HBox barraBusqueda = new HBox(
                10,
                new Label("Buscar:"),
                txtBuscar,
                btnBuscar,
                btnLimpiarBusqueda
        );
        barraBusqueda.setPadding(new Insets(10));

        // BARRA DE ESCANEO
        HBox barraEscaneo = new HBox(
                10,
                new Label("Escanear:"),
                txtEscanearCodigo,
                btnEscanear
        );
        barraEscaneo.setPadding(new Insets(0, 10, 10, 10));

        Label titulo = new Label("Ventas");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1f3a5f;");

        VBox parteSuperior = new VBox(
                8,
                titulo,
                lblBuscar,
                barraBusqueda,
                lblEscaneo,
                barraEscaneo
        );
        parteSuperior.setPadding(new Insets(10));

        // PANEL DERECHO
        GridPane panelDerecho = new GridPane();
        panelDerecho.setStyle(
                "-fx-background-color: #f8fafc;"
                + "-fx-border-color: #d9e2ec;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;"
        );
        panelDerecho.setPadding(new Insets(10));
        panelDerecho.setHgap(10);
        panelDerecho.setVgap(10);

        panelDerecho.add(new Label("Cantidad:"), 0, 0);
        panelDerecho.add(txtCantidad, 1, 0);

        panelDerecho.add(btnAgregar, 1, 1);
        panelDerecho.add(btnQuitar, 1, 2);

        panelDerecho.add(new Label("Método de pago:"), 0, 3);
        panelDerecho.add(comboMetodoPago, 1, 3);

        panelDerecho.add(new Label("Cuotas:"), 0, 4);
        panelDerecho.add(comboCuotas, 1, 4);

        panelDerecho.add(new Label("Subtotal:"), 0, 5);
        panelDerecho.add(lblSubtotal, 1, 5);

        panelDerecho.add(new Label("Descuento %:"), 0, 6);
        panelDerecho.add(txtDescuento, 1, 6);

        panelDerecho.add(new Label("Descuento $:"), 0, 7);
        panelDerecho.add(lblDescuento, 1, 7);

        panelDerecho.add(new Label("Recargo %:"), 0, 8);
        panelDerecho.add(txtRecargo, 1, 8);

        panelDerecho.add(new Label("Recargo $:"), 0, 9);
        panelDerecho.add(lblRecargo, 1, 9);

        panelDerecho.add(new Label("Total final:"), 0, 10);
        lblSubtotal.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        lblDescuento.setStyle("-fx-font-size: 13px;");
        lblRecargo.setStyle("-fx-font-size: 13px;");
        lblTotal.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1f3a5f;");
        lblTotal.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        panelDerecho.add(lblTotal, 1, 10);

        panelDerecho.add(btnConfirmarVenta, 1, 11);

        // CENTRO
        VBox centro = new VBox(10, tablaProductos, new Label("Detalle de venta"), tablaCarrito);
        centro.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8fafc;");
        root.setTop(parteSuperior);
        root.setCenter(centro);
        root.setRight(panelDerecho);

        cargarProductos();
        actualizarTotales();

        Scene scene = new Scene(root, 1400, 730);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            if (productoSeleccionado == null) {
                return;
            }

            String tecla = event.getText();

            if (tecla.equals("*")) {
                bufferCantidadRapida = "*";
                return;
            }

            if (tecla.matches("[0-9]")) {

                if (bufferCantidadRapida.isEmpty()) {
                    bufferCantidadRapida = tecla;
                } else {
                    bufferCantidadRapida += tecla;
                }

                return;
            }

            if (event.getCode() == KeyCode.ENTER) {

                int cantidad = 1;

                try {

                    if (bufferCantidadRapida.startsWith("*")) {
                        cantidad = Integer.parseInt(bufferCantidadRapida.substring(1));
                    } else if (!bufferCantidadRapida.isEmpty()) {
                        cantidad = Integer.parseInt(bufferCantidadRapida);
                    }

                } catch (Exception e) {
                    bufferCantidadRapida = "";
                    return;
                }

                agregarProductoRapido(productoSeleccionado, cantidad);

                bufferCantidadRapida = "";
            }

            if (event.getCode() == KeyCode.ESCAPE) {
                bufferCantidadRapida = "";
            }
        });
        stage.setTitle("Gestión de Ventas");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        txtEscanearCodigo.requestFocus();
    }

    private void cargarProductos() {
        ProductoDAO dao = new ProductoDAO();
        List<Producto> productos = dao.listarProductos();

        listaProductos.clear();
        listaProductos.addAll(productos);
    }

    private void buscarProductos() {
        String texto = txtBuscar.getText().trim();

        ProductoDAO dao = new ProductoDAO();
        List<Producto> productos = dao.buscarProductos(texto, null);

        listaProductos.clear();
        listaProductos.addAll(productos);
    }

    private void limpiarBusqueda() {
        txtBuscar.clear();
        productoSeleccionado = null;
        tablaProductos.getSelectionModel().clearSelection();
        cargarProductos();
    }

    // NUEVO: agregar directo por escaneo
    private void agregarPorEscaneo() {
        String codigoBarras = txtEscanearCodigo.getText();

        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            txtEscanearCodigo.requestFocus();
            return;
        }

        ProductoDAO dao = new ProductoDAO();
        Producto producto = dao.buscarPorCodigoBarras(codigoBarras.trim());

        if (producto == null) {
            mostrarFeedbackEscaneoError();
            Mensajes.mostrarError("Error", "No se encontró un producto con ese código de barras.");
            txtEscanearCodigo.clear();
            txtEscanearCodigo.requestFocus();
            return;
        }

        ItemVenta itemExistente = null;

        for (ItemVenta item : listaCarrito) {
            if (item.getProducto().getIdProducto() == producto.getIdProducto()) {
                itemExistente = item;
                break;
            }
        }

        int cantidadTotal = 1;
        if (itemExistente != null) {
            cantidadTotal = itemExistente.getCantidad() + 1;
        }

        if (cantidadTotal > producto.getStock()) {
            mostrarFeedbackEscaneoError();
            Mensajes.mostrarError("Error", "Stock insuficiente para seguir agregando ese producto.");
            txtEscanearCodigo.clear();
            txtEscanearCodigo.requestFocus();
            return;
        }

        if (itemExistente != null) {
            itemExistente.setCantidad(cantidadTotal);
            tablaCarrito.refresh();
        } else {
            listaCarrito.add(new ItemVenta(producto, 1));
        }

        verificarStockBajo(producto, cantidadTotal);

        mostrarFeedbackEscaneoExitoso();
        txtEscanearCodigo.clear();
        txtEscanearCodigo.requestFocus();
        actualizarTotales();
    }

    private void agregarAlCarrito() {
        if (productoSeleccionado == null) {
            Mensajes.mostrarError("Error", "Debe seleccionar un producto.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            if (cantidad <= 0) {
                Mensajes.mostrarError("Error", "La cantidad debe ser mayor a 0.");
                return;
            }

            ItemVenta itemExistente = null;

            for (ItemVenta item : listaCarrito) {
                if (item.getProducto().getIdProducto() == productoSeleccionado.getIdProducto()) {
                    itemExistente = item;
                    break;
                }
            }

            int cantidadTotal = cantidad;
            if (itemExistente != null) {
                cantidadTotal = itemExistente.getCantidad() + cantidad;
            }

            if (cantidadTotal > productoSeleccionado.getStock()) {
                Mensajes.mostrarError("Error", "Stock insuficiente para esa cantidad total.");
                return;
            }

            if (itemExistente != null) {
                itemExistente.setCantidad(cantidadTotal);
                tablaCarrito.refresh();
            } else {
                ItemVenta itemNuevo = new ItemVenta(productoSeleccionado, cantidad);
                listaCarrito.add(itemNuevo);
            }

            verificarStockBajo(productoSeleccionado, cantidadTotal);

            txtCantidad.clear();
            tablaProductos.getSelectionModel().clearSelection();
            productoSeleccionado = null;
            actualizarTotales();

        } catch (NumberFormatException e) {
            Mensajes.mostrarError("Error", "Ingrese una cantidad válida.");
        }
    }

    private void quitarDelCarrito() {
        ItemVenta item = tablaCarrito.getSelectionModel().getSelectedItem();

        if (item == null) {
            Mensajes.mostrarError("Error", "Debe seleccionar un item del carrito.");
            return;
        }

        listaCarrito.remove(item);
        actualizarTotales();
    }

    private void actualizarTotales() {
        double subtotal = 0;

        for (ItemVenta item : listaCarrito) {
            subtotal += item.getSubtotalSinIva();
        }

        double porcentajeDescuento = 0;
        double porcentajeRecargo = 0;

        try {
            String textoDescuento = txtDescuento.getText();
            if (textoDescuento != null && !textoDescuento.trim().isEmpty()) {
                porcentajeDescuento = Double.parseDouble(textoDescuento.trim());
            }
        } catch (NumberFormatException e) {
            porcentajeDescuento = 0;
        }

        try {
            String textoRecargo = txtRecargo.getText();
            if (textoRecargo != null && !textoRecargo.trim().isEmpty()) {
                porcentajeRecargo = Double.parseDouble(textoRecargo.trim());
            }
        } catch (NumberFormatException e) {
            porcentajeRecargo = 0;
        }

        if (porcentajeDescuento < 0) {
            porcentajeDescuento = 0;
        }
        if (porcentajeRecargo < 0) {
            porcentajeRecargo = 0;
        }

        double descuento = subtotal * (porcentajeDescuento / 100.0);
        double recargo = subtotal * (porcentajeRecargo / 100.0);
        double total = subtotal - descuento + recargo;

        lblSubtotal.setText(String.format("%.2f", subtotal));
        lblDescuento.setText(String.format("%.2f", descuento));
        lblRecargo.setText(String.format("%.2f", recargo));
        lblTotal.setText(String.format("%.2f", total));
    }

    private void mostrarFeedbackEscaneoExitoso() {
        txtEscanearCodigo.setStyle(
                "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-color: #d4edda;"
                + "-fx-border-color: #28a745;"
                + "-fx-border-radius: 6;"
                + "-fx-background-radius: 6;"
        );

        restaurarEstiloEscaneo();
    }

    private void mostrarFeedbackEscaneoError() {
        txtEscanearCodigo.setStyle(
                "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-color: #f8d7da;"
                + "-fx-border-color: #dc3545;"
                + "-fx-border-radius: 6;"
                + "-fx-background-radius: 6;"
        );

        restaurarEstiloEscaneo();
    }

    private void restaurarEstiloEscaneo() {
        PauseTransition pausa = new PauseTransition(Duration.seconds(1));

        pausa.setOnFinished(e -> txtEscanearCodigo.setStyle(
                "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-color: #f4f8fb;"
                + "-fx-border-color: #1f3a5f;"
                + "-fx-border-radius: 6;"
                + "-fx-background-radius: 6;"
        ));

        pausa.play();
    }

    private void confirmarVenta() {
        if (listaCarrito.isEmpty()) {
            Mensajes.mostrarError("Error", "No hay productos en la venta.");
            return;
        }

        boolean confirmar = Mensajes.confirmar("Confirmar venta", "¿Desea confirmar la venta?");
        if (!confirmar) {
            return;
        }

        try {
            double porcentajeDescuento = 0;
            double porcentajeRecargo = 0;

            String textoDescuento = txtDescuento.getText();
            if (textoDescuento != null && !textoDescuento.trim().isEmpty()) {
                porcentajeDescuento = Double.parseDouble(textoDescuento.trim());
            }

            String textoRecargo = txtRecargo.getText();
            if (textoRecargo != null && !textoRecargo.trim().isEmpty()) {
                porcentajeRecargo = Double.parseDouble(textoRecargo.trim());
            }

            if (porcentajeDescuento < 0) {
                Mensajes.mostrarError("Error", "El descuento no puede ser negativo.");
                return;
            }

            if (porcentajeRecargo < 0) {
                Mensajes.mostrarError("Error", "El recargo no puede ser negativo.");
                return;
            }

            String metodoPago = comboMetodoPago.getValue();
            int cuotas = comboCuotas.getValue() != null ? comboCuotas.getValue() : 1;

            List<ItemVenta> itemsVendidos = List.copyOf(listaCarrito);

            VentaDAO dao = new VentaDAO();
            boolean ok = dao.registrarVenta(itemsVendidos, metodoPago, cuotas, porcentajeDescuento, porcentajeRecargo);

            if (ok) {
                double subtotal = calcularSubtotal();
                porcentajeDescuento = parseDoubleSeguro(txtDescuento.getText());
                porcentajeRecargo = parseDoubleSeguro(txtRecargo.getText());

                double descuento = subtotal * (porcentajeDescuento / 100.0);
                double recargo = subtotal * (porcentajeRecargo / 100.0);
                double totalFinal = subtotal - descuento + recargo;

                TicketVentaUtil.mostrarTicket(
                        new ArrayList<>(listaCarrito),
                        comboMetodoPago.getValue(),
                        comboCuotas.getValue(),
                        subtotal,
                        descuento,
                        recargo,
                        totalFinal
                );

                listaCarrito.clear();
                actualizarTotales();
                txtEscanearCodigo.clear();
                txtCantidad.clear();
                limpiarBusqueda();
                cargarProductos();
            } else {
                Mensajes.mostrarError("Error", "No se pudo registrar la venta.");
            }
        } catch (NumberFormatException e) {
            Mensajes.mostrarError("Error", "Descuento y recargo deben ser números válidos.");
        } catch (Exception e) {
            e.printStackTrace();
            Mensajes.mostrarError("Error", "Falló la venta: " + e.getMessage());
        }
    }

    private void verificarStockBajo(Producto producto, int cantidadEnCarrito) {
        int stockRestante = producto.getStock() - cantidadEnCarrito;

        if (stockRestante <= producto.getStockMinimo()) {
            Mensajes.mostrarInfo(
                    "Advertencia de stock",
                    "El producto \"" + producto.getNombre()
                    + "\" quedará con stock bajo.\n"
                    + "Stock restante: " + stockRestante
                    + "\nStock mínimo: " + producto.getStockMinimo()
            );
        }
    }

    private void agregarProductoRapido(Producto producto) {

        ItemVenta itemExistente = null;

        for (ItemVenta item : listaCarrito) {
            if (item.getProducto().getIdProducto() == producto.getIdProducto()) {
                itemExistente = item;
                break;
            }
        }

        int cantidadTotal = 1;

        if (itemExistente != null) {
            cantidadTotal = itemExistente.getCantidad() + 1;
        }

        if (cantidadTotal > producto.getStock()) {
            Mensajes.mostrarError("Stock insuficiente", "No hay más stock de este producto.");
            return;
        }

        if (itemExistente != null) {
            itemExistente.setCantidad(cantidadTotal);
            tablaCarrito.refresh();
        } else {
            listaCarrito.add(new ItemVenta(producto, 1));
        }

        actualizarTotales();
    }

    private void agregarMultiples() {

        if (productoSeleccionado == null) {
            return;
        }

        String texto = txtCantidad.getText().trim();

        int cantidad;

        try {

            if (texto.startsWith("*")) {
                cantidad = Integer.parseInt(texto.substring(1));
            } else {
                cantidad = Integer.parseInt(texto);
            }

        } catch (Exception e) {
            Mensajes.mostrarError("Error", "Cantidad inválida.");
            return;
        }

        if (cantidad <= 0) {
            return;
        }

        ItemVenta itemExistente = null;

        for (ItemVenta item : listaCarrito) {
            if (item.getProducto().getIdProducto() == productoSeleccionado.getIdProducto()) {
                itemExistente = item;
                break;
            }
        }

        int cantidadTotal = cantidad;

        if (itemExistente != null) {
            cantidadTotal = itemExistente.getCantidad() + cantidad;
        }

        if (cantidadTotal > productoSeleccionado.getStock()) {
            Mensajes.mostrarError("Stock insuficiente", "No hay suficiente stock.");
            return;
        }

        if (itemExistente != null) {
            itemExistente.setCantidad(cantidadTotal);
            tablaCarrito.refresh();
        } else {
            listaCarrito.add(new ItemVenta(productoSeleccionado, cantidad));
        }

        txtCantidad.clear();
        actualizarTotales();
    }

    private void agregarProductoRapido(Producto producto, int cantidad) {

        ItemVenta itemExistente = null;

        for (ItemVenta item : listaCarrito) {
            if (item.getProducto().getIdProducto() == producto.getIdProducto()) {
                itemExistente = item;
                break;
            }
        }

        int cantidadTotal = cantidad;

        if (itemExistente != null) {
            cantidadTotal = itemExistente.getCantidad() + cantidad;
        }

        if (cantidadTotal > producto.getStock()) {
            Mensajes.mostrarError("Stock insuficiente", "No hay suficiente stock.");
            return;
        }

        if (itemExistente != null) {
            itemExistente.setCantidad(cantidadTotal);
            tablaCarrito.refresh();
        } else {
            listaCarrito.add(new ItemVenta(producto, cantidad));
        }

        verificarStockBajo(producto, cantidadTotal);
        actualizarTotales();
    }

    private double calcularSubtotal() {
        double subtotal = 0.0;

        for (ItemVenta item : listaCarrito) {
            subtotal += item.getSubtotalSinIva();
        }

        return subtotal;
    }

    private double parseDoubleSeguro(String texto) {
        try {
            if (texto == null || texto.trim().isEmpty()) {
                return 0.0;
            }
            return Double.parseDouble(texto.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
