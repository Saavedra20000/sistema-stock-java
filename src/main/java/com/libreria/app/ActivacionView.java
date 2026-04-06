/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.app;

import com.libreria.util.LicenciaUtil;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ActivacionView extends Application {

    @Override
    public void start(Stage stage) {
        Label lblTitulo = new Label("Sistema no activado");
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblTexto = new Label("Envíe este código al proveedor para solicitar la activación:");

        TextArea txtCodigo = new TextArea(LicenciaUtil.obtenerIdEquipo());
        txtCodigo.setEditable(false);
        txtCodigo.setWrapText(true);
        txtCodigo.setMaxWidth(300);
        txtCodigo.setMaxHeight(80);

        Label lblRuta = new Label("La licencia debe guardarse en:\n" + LicenciaUtil.rutaLicencia());
        lblRuta.setWrapText(true);

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> stage.close());

        VBox root = new VBox(15, lblTitulo, lblTexto, txtCodigo, lblRuta, btnCerrar);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 450, 280);

        stage.setTitle("Activación del Sistema");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
    launch(args);
}
}
