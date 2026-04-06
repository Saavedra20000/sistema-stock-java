/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.view;

import com.libreria.util.BackupUtil;
import com.libreria.util.Mensajes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class RestaurarBackupView {

    private static final String CARPETA_BACKUPS =
            System.getProperty("user.home")
            + "\\AppData\\Local\\Saavedras\\backups";

    public static void mostrar() {

        Stage stage = new Stage();
        stage.setTitle("Restaurar Backup");

        ListView<File> listaBackups = new ListView<>();

        ObservableList<File> backups = FXCollections.observableArrayList();

        File carpeta = new File(CARPETA_BACKUPS);

        if (carpeta.exists()) {

            File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(".db"));

            if (archivos != null) {
                for (File f : archivos) {
                    backups.add(f);
                }
            }
        }

        listaBackups.setItems(backups);

        listaBackups.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        Button btnRestaurar = new Button("Restaurar backup seleccionado");

        btnRestaurar.setOnAction(e -> {

            File seleccionado = listaBackups.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {

                Mensajes.mostrarAdvertencia("Atención", "Debe seleccionar un BackUp.");
                return;
            }

            BackupUtil.restaurarBackup(seleccionado);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Backup restaurado");
            alert.setHeaderText(null);
            alert.setContentText("El backup fue restaurado correctamente.\nReinicie el sistema.");

            alert.showAndWait();
        });

        VBox root = new VBox(15,
                new Label("Seleccione un backup para restaurar:"),
                listaBackups,
                btnRestaurar
        );

        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 400, 350);

        stage.setScene(scene);
        stage.show();
    }
}
