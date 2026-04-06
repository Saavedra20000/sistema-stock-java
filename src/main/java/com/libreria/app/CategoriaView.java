/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.app;

import com.libreria.dao.CategoriaDAO;
import com.libreria.model.Categoria;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import com.libreria.util.Mensajes;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;


public class CategoriaView extends Application {

    private final TableView<Categoria> tabla = new TableView<>();
    private final ObservableList<Categoria> lista = FXCollections.observableArrayList();

    private final TextField txtNombre = new TextField();
    private final TextField txtDescripcion = new TextField();

    private Categoria categoriaSeleccionada = null;

    @Override
    public void start(Stage stage) {

        Label titulo = new Label("Gestión de Categorías");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1f3a5f;");

        TableColumn<Categoria, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getNombre()));
        colNombre.setPrefWidth(200);

        TableColumn<Categoria, String> colDescripcion = new TableColumn<>("Descripción");
        colDescripcion.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getDescripcion()));
        colDescripcion.setPrefWidth(300);

        tabla.getColumns().add(colNombre);
        tabla.getColumns().add(colDescripcion);
        tabla.setItems(lista);

        tabla.setOnMouseClicked(e -> seleccionar());

        Button btnCargar = new Button("Cargar");
        Button btnGuardar = new Button("Guardar");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");
        Button btnNuevo = new Button("Nuevo");

        String estiloBoton
                = "-fx-background-color: #e9eef5;"
                + "-fx-text-fill: #1f3a5f;"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 6;"
                + "-fx-border-radius: 6;"
                + "-fx-border-color: #c7d3e3;";

        btnCargar.setStyle(estiloBoton);
        btnGuardar.setStyle(estiloBoton);
        btnModificar.setStyle(estiloBoton);
        btnEliminar.setStyle(estiloBoton);
        btnNuevo.setStyle(estiloBoton);

        btnCargar.setPrefWidth(100);
        btnGuardar.setPrefWidth(100);
        btnModificar.setPrefWidth(100);
        btnEliminar.setPrefWidth(100);
        btnNuevo.setPrefWidth(100);

        btnCargar.setOnAction(e -> cargar());
        btnGuardar.setOnAction(e -> guardar());
        btnModificar.setOnAction(e -> modificar());
        btnEliminar.setOnAction(e -> eliminar());
        btnNuevo.setOnAction(e -> nuevo());

        HBox barraBotones = new HBox(10,
                btnCargar,
                btnGuardar,
                btnModificar,
                btnEliminar,
                btnNuevo
        );

        barraBotones.setAlignment(Pos.CENTER_LEFT);

        txtNombre.setPrefWidth(220);
        txtDescripcion.setPrefWidth(220);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Nombre:"), 0, 0);
        form.add(txtNombre, 1, 0);

        form.add(new Label("Descripción:"), 0, 1);
        form.add(txtDescripcion, 1, 1);

        VBox panelDerecho = new VBox(15,
                new Label("Datos de la categoría"),
                form
        );

        panelDerecho.setPadding(new Insets(10));

        BorderPane root = new BorderPane();

        VBox top = new VBox(10,
                titulo,
                barraBotones
        );

        top.setPadding(new Insets(10));

        root.setTop(top);
        root.setCenter(tabla);
        root.setRight(panelDerecho);

        root.setStyle("-fx-background-color: #f8fafc;");

        Scene scene = new Scene(root, 900, 450);

        stage.setTitle("Categorías - Saavedra's");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void cargar() {
        CategoriaDAO dao = new CategoriaDAO();
        List<Categoria> categorias = dao.listarCategorias();

        lista.clear();
        lista.addAll(categorias);
    }

    private void guardar() {
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();

        if (nombre == null || nombre.trim().isEmpty()) {
            Mensajes.mostrarError("Error", "El nombre de la categoría es obligatorio.");
            return;
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre.trim());
        categoria.setDescripcion(descripcion != null ? descripcion.trim() : "");
        categoria.setActivo(true);

        CategoriaDAO dao = new CategoriaDAO();

        if (dao.existeCategoriaPorNombre(nombre.trim())) {
            Mensajes.mostrarError("Error", "Ya existe una categoría con ese nombre.");
            return;
        }

// si no existe activa, vemos si existe inactiva
        if (dao.reactivarCategoria(nombre.trim())) {
            Mensajes.mostrarInfo("Info", "Categoría reactivada correctamente.");
            limpiar();
            cargar();
            return;
        }

        boolean ok = dao.agregarCategoria(categoria);

        if (ok) {
            Mensajes.mostrarInfo("Éxito", "Categoría guardada correctamente.");
            limpiar();
            cargar();
        } else {
            Mensajes.mostrarError("Error", "No se pudo guardar la categoría.");
        }
    }

    private void modificar() {
        if (categoriaSeleccionada == null) {
            Mensajes.mostrarError("Error", "Debe seleccionar una categoría.");
            return;
        }

        String nombre = txtNombre.getText();

        // 🔴 VALIDACIÓN
        if (nombre == null || nombre.trim().isEmpty()) {
            Mensajes.mostrarError("Error", "El nombre de la categoría es obligatorio.");
            return;
        }

        categoriaSeleccionada.setNombre(nombre.trim());
        categoriaSeleccionada.setDescripcion(txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "");

        CategoriaDAO dao = new CategoriaDAO();
        boolean ok = dao.modificarCategoria(categoriaSeleccionada);

        if (ok) {
            Mensajes.mostrarInfo("Éxito", "Categoría modificada correctamente.");
            txtNombre.clear();
            categoriaSeleccionada = null;
            tabla.getSelectionModel().clearSelection();
            cargar();
        } else {
            Mensajes.mostrarError("Error", "No se pudo modificar la categoría.");
        }
    }

    private void eliminar() {
        if (categoriaSeleccionada == null) {
            Mensajes.mostrarError("Error", "Debe seleccionar una categoría.");
            return;
        }
        boolean confirmar = Mensajes.confirmar("Confirmar", "¿Está seguro de eliminar la categoría seleccionada?");
        if (!confirmar) {
            return;
        }
        CategoriaDAO dao = new CategoriaDAO();
        boolean ok = dao.eliminarCategoria(categoriaSeleccionada.getIdCategoria());
        Mensajes.mostrarInfo("Éxito", "Categoria eliminada correctamente.");

        cargar();
        limpiar();
        categoriaSeleccionada = null;
    }

    private void nuevo() {
        categoriaSeleccionada = null;
        limpiar();
        tabla.getSelectionModel().clearSelection();
    }

    private void seleccionar() {
        categoriaSeleccionada = tabla.getSelectionModel().getSelectedItem();

        if (categoriaSeleccionada != null) {
            txtNombre.setText(categoriaSeleccionada.getNombre());
            txtDescripcion.setText(categoriaSeleccionada.getDescripcion());
        }
    }

    private void limpiar() {
        txtNombre.clear();
        txtDescripcion.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
