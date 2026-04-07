/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.app;

import com.libreria.dao.VentaDAO;
import com.libreria.model.VentaResumen;
import com.libreria.util.ExportadorVentas;
import com.libreria.util.Mensajes;
import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HistorialVentasView extends Application {

    private TableView<VentaResumen> tabla = new TableView<>();
    private ObservableList<VentaResumen> lista = FXCollections.observableArrayList();

    private DatePicker dpDesde = new DatePicker();
    private DatePicker dpHasta = new DatePicker();

    private Label lblCantidadVentas = new Label("Ventas encontradas: 0");
    private Label lblTotalVendido = new Label("Total vendido: $0.00");

    @Override
    public void start(Stage stage) {

        Label titulo = new Label("Historial de Ventas");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1f3a5f;");

        lblCantidadVentas.setStyle("-fx-font-size: 12px; -fx-text-fill: #5a5a5a; -fx-font-weight: bold;");
        lblTotalVendido.setStyle("-fx-font-size: 12px; -fx-text-fill: #1f3a5f; -fx-font-weight: bold;");

        TableColumn<VentaResumen, Integer> colId = new TableColumn<>("ID Venta");
        colId.setCellValueFactory(data
                -> new SimpleIntegerProperty(data.getValue().getIdVenta()).asObject());
        colId.setPrefWidth(90);

        TableColumn<VentaResumen, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(data
                -> new SimpleStringProperty(
                        data.getValue().getFecha() != null
                        ? data.getValue().getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                        : ""
                )
        );
        colFecha.setPrefWidth(180);

        TableColumn<VentaResumen, String> colMetodoPago = new TableColumn<>("Método Pago");
        colMetodoPago.setCellValueFactory(data
                -> new SimpleStringProperty(
                        data.getValue().getMetodoPago() != null
                        ? data.getValue().getMetodoPago()
                        : ""
                ));
        colMetodoPago.setPrefWidth(130);

        TableColumn<VentaResumen, Integer> colCuotas = new TableColumn<>("Cuotas");
        colCuotas.setCellValueFactory(data
                -> new SimpleIntegerProperty(data.getValue().getCuotas()).asObject());
        colCuotas.setPrefWidth(80);

        TableColumn<VentaResumen, String> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(data
                -> new SimpleStringProperty(String.format("%.2f", data.getValue().getSubtotalSinIva())));
        colSubtotal.setPrefWidth(110);

        TableColumn<VentaResumen, String> colDescuento = new TableColumn<>("Descuento");
        colDescuento.setCellValueFactory(data
                -> new SimpleStringProperty(String.format("%.2f", data.getValue().getDescuento())));
        colDescuento.setPrefWidth(110);

        TableColumn<VentaResumen, String> colRecargo = new TableColumn<>("Recargo");
        colRecargo.setCellValueFactory(data
                -> new SimpleStringProperty(String.format("%.2f", data.getValue().getRecargo())));
        colRecargo.setPrefWidth(110);

        TableColumn<VentaResumen, String> colTotal = new TableColumn<>("Total Final");
        colTotal.setCellValueFactory(data
                -> new SimpleStringProperty(String.format("%.2f", data.getValue().getTotalConIva())));
        colTotal.setPrefWidth(130);

        tabla.getColumns().clear();
        tabla.getColumns().addAll(
                colId,
                colFecha,
                colMetodoPago,
                colCuotas,
                colSubtotal,
                colDescuento,
                colRecargo,
                colTotal
        );

        tabla.setItems(lista);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setStyle("-fx-font-size: 12px;");

        tabla.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                VentaResumen ventaSeleccionada = tabla.getSelectionModel().getSelectedItem();

                if (ventaSeleccionada != null) {
                    new DetalleVentaView().mostrar(ventaSeleccionada);
                }
            }
        });

        Button btnFiltrar = new Button("Filtrar");
        Button btnHoy = new Button("Hoy");
        Button btnMes = new Button("Este mes");
        Button btnLimpiar = new Button("Limpiar");
        Button btnVerDetalle = new Button("Ver detalle");
        Button btnExcel = new Button("Exportar Excel");
        Button btnPDF = new Button("Exportar PDF");

        String estiloBoton
                = "-fx-background-color: #e9eef5;"
                + "-fx-text-fill: #1f3a5f;"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 6;"
                + "-fx-border-radius: 6;"
                + "-fx-border-color: #c7d3e3;";

        btnFiltrar.setStyle(estiloBoton);
        btnHoy.setStyle(estiloBoton);
        btnMes.setStyle(estiloBoton);
        btnLimpiar.setStyle(estiloBoton);
        btnVerDetalle.setStyle(estiloBoton);
        btnExcel.setStyle(estiloBoton);
        btnPDF.setStyle(estiloBoton);

        btnFiltrar.setOnAction(e -> cargarVentas());
        btnHoy.setOnAction(e -> filtrarHoy());
        btnMes.setOnAction(e -> filtrarMesActual());
        btnLimpiar.setOnAction(e -> limpiarFiltros());
        btnExcel.setOnAction(e -> exportarExcel(stage));
        btnPDF.setOnAction(e -> exportarPDF(stage));

        btnVerDetalle.setOnAction(e -> {
            VentaResumen ventaSeleccionada = tabla.getSelectionModel().getSelectedItem();

            if (ventaSeleccionada == null) {
                Mensajes.mostrarAdvertencia("Atención", "Seleccioná una venta para ver el detalle.");
                return;
            }

            new DetalleVentaView().mostrar(ventaSeleccionada);
        });

        HBox barraFiltros = new HBox(
                10,
                new Label("Desde:"),
                dpDesde,
                new Label("Hasta:"),
                dpHasta,
                btnFiltrar,
                btnHoy,
                btnMes,
                btnLimpiar
        );
        barraFiltros.setAlignment(Pos.CENTER_LEFT);

        HBox barraAcciones = new HBox(
                10,
                btnVerDetalle,
                btnExcel,
                btnPDF
        );
        barraAcciones.setAlignment(Pos.CENTER_LEFT);

        HBox barraResumen = new HBox(20, lblCantidadVentas, lblTotalVendido);
        barraResumen.setAlignment(Pos.CENTER_LEFT);

        VBox parteSuperior = new VBox(10, titulo, barraFiltros, barraAcciones, barraResumen);
        parteSuperior.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(parteSuperior);
        root.setCenter(tabla);
        root.setStyle("-fx-background-color: #f8fafc;");

        cargarVentas();
        actualizarResumenVentas();

        Scene scene = new Scene(root, 1200, 600);
        stage.setTitle("Historial de Ventas - Saavedra's");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void cargarVentas() {
        VentaDAO dao = new VentaDAO();
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();

        List<VentaResumen> ventas = dao.listarVentasPorRango(desde, hasta);

        lista.clear();
        lista.addAll(ventas);
        lblCantidadVentas.setText("Ventas encontradas: " + lista.size());
        actualizarResumenVentas();
    }

    private void filtrarHoy() {
        LocalDate hoy = LocalDate.now();
        dpDesde.setValue(hoy);
        dpHasta.setValue(hoy);
        cargarVentas();
        actualizarResumenVentas();
    }

    private void filtrarMesActual() {
        YearMonth mesActual = YearMonth.now();
        dpDesde.setValue(mesActual.atDay(1));
        dpHasta.setValue(mesActual.atEndOfMonth());
        cargarVentas();
        actualizarResumenVentas();
    }

    private void limpiarFiltros() {
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        cargarVentas();
        actualizarResumenVentas();
    }

    private void exportarExcel(Stage stage) {
        if (lista.isEmpty()) {
            Mensajes.mostrarError("Error", "No hay ventas para exportar.");
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Excel");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
            fileChooser.setInitialFileName("historial_ventas.xlsx");

            File archivo = fileChooser.showSaveDialog(stage);

            if (archivo != null) {
                ExportadorVentas.exportarExcel(lista, archivo.getAbsolutePath());
                Mensajes.mostrarInfo("Éxito", "Excel exportado correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Mensajes.mostrarError("Error", "No se pudo exportar el Excel.");
        }
    }

    private void exportarPDF(Stage stage) {
        if (lista.isEmpty()) {
            Mensajes.mostrarError("Error", "No hay ventas para exportar.");
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
            fileChooser.setInitialFileName("historial_ventas.pdf");

            File archivo = fileChooser.showSaveDialog(stage);

            if (archivo != null) {
                ExportadorVentas.exportarPDF(lista, archivo.getAbsolutePath());
                Mensajes.mostrarInfo("Éxito", "PDF exportado correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Mensajes.mostrarError("Error", "No se pudo exportar el PDF.");
        }
    }

    private void actualizarResumenVentas() {
        int cantidad = lista.size();

        double total = 0.0;
        for (VentaResumen venta : lista) {
            total += venta.getTotalConIva();
        }

        lblCantidadVentas.setText("Ventas encontradas: " + cantidad);
        lblTotalVendido.setText("Total vendido: $" + String.format("%.2f", total));
    }

    public static void main(String[] args) {
        launch(args);
    }
}