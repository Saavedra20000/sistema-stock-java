/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.app;

import com.libreria.dao.CategoriaDAO;
import com.libreria.dao.ProductoDAO;
import com.libreria.model.Categoria;
import com.libreria.model.Producto;
import com.libreria.util.ExcelImportador;
import com.libreria.util.Mensajes;
import java.io.File;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final TableView<Producto> tablaProductos = new TableView<>();
    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    private final TextField txtBuscar = new TextField();
    private final ComboBox<Categoria> comboFiltroCategoria = new ComboBox<>();

    private Producto productoSeleccionado = null;

    private boolean abrirSoloStockBajo = false;

    public MainApp() {
    }

    public MainApp(boolean abrirSoloStockBajo) {
        this.abrirSoloStockBajo = abrirSoloStockBajo;
    }

    @Override
    public void start(Stage stage) {

        // COLUMNAS TABLA
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
        colCodigoBarras.setComparator((a, b) -> {
            try {
                Long numA = Long.parseLong(a);
                Long numB = Long.parseLong(b);
                return numA.compareTo(numB);
            } catch (Exception e) {
                return a.compareTo(b);
            }
        });

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

        TableColumn<Producto, String> colPrecioCompra = new TableColumn<>("Costo Neto");
        colPrecioCompra.setCellValueFactory(data
                -> new SimpleStringProperty(String.format("%.2f", data.getValue().getPrecioCompra())));

        TableColumn<Producto, String> colPrecioVenta = new TableColumn<>("Precio Venta");
        colPrecioVenta.setCellValueFactory(data
                -> new SimpleStringProperty(String.format("%.2f", data.getValue().getPrecioVenta())));

        TableColumn<Producto, String> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(data
                -> new SimpleStringProperty(String.valueOf(data.getValue().getStock())));

        TableColumn<Producto, String> colStockMinimo = new TableColumn<>("Stock Mínimo");
        colStockMinimo.setCellValueFactory(data
                -> new SimpleStringProperty(String.valueOf(data.getValue().getStockMinimo())));

        tablaProductos.getColumns().addAll(
                colCodigo,
                colCodigoBarras,
                colNombre,
                colDescripcion,
                colPrecioCompra,
                colPrecioVenta,
                colStock,
                colStockMinimo
        );

        tablaProductos.setItems(listaProductos);
        tablaProductos.setStyle("-fx-selection-bar: #66ccff;");
        tablaProductos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaProductos.setStyle("-fx-font-size: 12px;");

        tablaProductos.setOnMouseClicked(e
                -> productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem());

        tablaProductos.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Producto producto, boolean empty) {
                super.updateItem(producto, empty);

                if (empty || producto == null) {
                    setStyle("");
                } else if (producto.getStock() <= producto.getStockMinimo()) {
                    setStyle("-fx-background-color: #ffcccc;");
                } else {
                    setStyle("");
                }
            }
        });

        // BOTONES
        Button btnNuevo = new Button("Nuevo");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");
        Button btnRecargarCategorias = new Button("Recargar categorías");
        Button btnBuscar = new Button("Buscar");
        Button btnLimpiarFiltros = new Button("Limpiar filtros");
        Button btnStockBajo = new Button("Stock bajo");
        Button btnImportarExcel = new Button("Importar Excel");

        String estiloBoton
                = "-fx-background-color: #e9eef5;"
                + "-fx-text-fill: #1f3a5f;"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 6;"
                + "-fx-border-radius: 6;"
                + "-fx-border-color: #c7d3e3;";

        btnNuevo.setStyle(estiloBoton);
        btnModificar.setStyle(estiloBoton);
        btnEliminar.setStyle(estiloBoton);
        btnRecargarCategorias.setStyle(estiloBoton);
        btnBuscar.setStyle(estiloBoton);
        btnLimpiarFiltros.setStyle(estiloBoton);
        btnStockBajo.setStyle(estiloBoton);
        btnImportarExcel.setStyle(estiloBoton);

        btnNuevo.setPrefWidth(120);
        btnModificar.setPrefWidth(120);
        btnEliminar.setPrefWidth(120);
        btnRecargarCategorias.setPrefWidth(160);
        btnBuscar.setPrefWidth(100);
        btnLimpiarFiltros.setPrefWidth(130);
        btnStockBajo.setPrefWidth(120);
        btnImportarExcel.setPrefWidth(140);

        txtBuscar.setPrefWidth(220);
        comboFiltroCategoria.setPrefWidth(180);

        btnNuevo.setOnAction(e -> abrirFormularioNuevo());
        btnModificar.setOnAction(e -> abrirFormularioModificar());
        btnEliminar.setOnAction(e -> eliminarProducto());
        btnRecargarCategorias.setOnAction(e -> recargarCategorias());
        btnBuscar.setOnAction(e -> buscarProductos());
        btnLimpiarFiltros.setOnAction(e -> limpiarFiltros());
        btnStockBajo.setOnAction(e -> mostrarStockBajo());
        btnImportarExcel.setOnAction(e -> importarExcel(stage));

        txtBuscar.setOnAction(e -> buscarProductos());
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarProductos());

        HBox barraFiltros = new HBox(
                10,
                new Label("Buscar:"),
                txtBuscar,
                new Label("Categoría:"),
                comboFiltroCategoria,
                btnBuscar,
                btnLimpiarFiltros
        );
        barraFiltros.setPadding(new Insets(10));
        barraFiltros.setAlignment(Pos.CENTER_LEFT);

        HBox barraAcciones = new HBox(
                10,
                btnNuevo,
                btnModificar,
                btnEliminar,
                btnRecargarCategorias,
                btnStockBajo,
                btnImportarExcel
        );
        barraAcciones.setPadding(new Insets(10));

        Label titulo = new Label("Gestión de Productos");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1f3a5f;");

        VBox zonaSuperior = new VBox(
                10,
                titulo,
                barraFiltros,
                barraAcciones
        );

        zonaSuperior.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(zonaSuperior);
        root.setCenter(tablaProductos);
        root.setStyle("-fx-background-color: #f8fafc;");

        cargarCategorias();

        if (abrirSoloStockBajo) {
            mostrarStockBajo();
        } else {
            cargarProductos();
        }

        Scene scene = new Scene(root, 1300, 600);

        stage.setTitle("Sistema de Gestión de Stock");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void cargarProductos() {
        ProductoDAO dao = new ProductoDAO();
        List<Producto> productos = dao.listarProductos();

        listaProductos.clear();
        listaProductos.addAll(productos);
    }

    private void cargarCategorias() {
        CategoriaDAO dao = new CategoriaDAO();
        List<Categoria> categorias = dao.listarCategorias();

        comboFiltroCategoria.getItems().clear();
        comboFiltroCategoria.getItems().addAll(categorias);
    }

    private void recargarCategorias() {
        cargarCategorias();
        Mensajes.mostrarInfo("Información", "Categorías recargadas correctamente.");
    }

    private void buscarProductos() {
        String texto = txtBuscar.getText().trim();

        Categoria categoriaSeleccionada = comboFiltroCategoria.getValue();
        Integer idCategoria = null;

        if (categoriaSeleccionada != null) {
            idCategoria = categoriaSeleccionada.getIdCategoria();
        }

        ProductoDAO dao = new ProductoDAO();
        List<Producto> productos = dao.buscarProductos(texto, idCategoria);

        listaProductos.clear();
        listaProductos.addAll(productos);

        if (!texto.isEmpty() && productos.isEmpty()) {
            Mensajes.mostrarInfo("Búsqueda", "No se encontraron productos.");
            productoSeleccionado = null;
            tablaProductos.getSelectionModel().clearSelection();
            txtBuscar.clear();
            txtBuscar.requestFocus();
            return;
        }

        if (productos.size() == 1) {
            tablaProductos.getSelectionModel().select(0);
            tablaProductos.scrollTo(0);
            tablaProductos.requestFocus();
            productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        }
    }

    private void limpiarFiltros() {
        txtBuscar.clear();
        comboFiltroCategoria.setValue(null);
        cargarProductos();
    }

    private void abrirFormularioNuevo() {
        ProductoFormView form = new ProductoFormView(null, this::cargarProductos);
        form.mostrar();
    }

    private void abrirFormularioModificar() {
        if (productoSeleccionado == null) {
            Mensajes.mostrarError("Error", "Debe seleccionar un producto para modificar.");
            return;
        }

        ProductoFormView form = new ProductoFormView(productoSeleccionado, this::cargarProductos);
        form.mostrar();
    }

    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            Mensajes.mostrarError("Error", "Debe seleccionar un producto para eliminar.");
            return;
        }

        boolean confirmar = Mensajes.confirmar("Confirmar", "¿Está seguro de eliminar el producto seleccionado?");
        if (!confirmar) {
            return;
        }

        ProductoDAO dao = new ProductoDAO();
        boolean ok = dao.eliminarProducto(productoSeleccionado.getIdProducto());

        if (ok) {
            Mensajes.mostrarInfo("Éxito", "Producto eliminado correctamente.");
            cargarProductos();
            productoSeleccionado = null;
            tablaProductos.getSelectionModel().clearSelection();
        } else {
            Mensajes.mostrarError("Error", "No se pudo eliminar el producto.");
        }
    }

    private void mostrarStockBajo() {
        ProductoDAO dao = new ProductoDAO();
        List<Producto> productos = dao.listarProductosStockBajo();

        listaProductos.clear();
        listaProductos.addAll(productos);
    }

    private void importarExcel(Stage stage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo Excel");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx")
        );

        File archivo = fileChooser.showOpenDialog(stage);

        if (archivo == null) {
            return;
        }

        try {
            int cantidad = ExcelImportador.importarProductos(archivo);
            System.out.println("Importados: " + cantidad);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Importación");
            alert.setHeaderText(null);
            alert.setContentText("Se importaron " + cantidad + " productos.");
            alert.showAndWait();
            //Mensajes.mostrarInfo(
            //      "Importación completada",
            //    "Los productos fueron importados correctamente."//
            

            cargarProductos();

        } catch (Exception e) {

            Mensajes.mostrarError(
                    "Error al importar",
                    "No se pudo importar el archivo Excel."
            );

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
