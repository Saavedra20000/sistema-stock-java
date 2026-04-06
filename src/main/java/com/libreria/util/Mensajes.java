/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.util.Optional;

public class Mensajes {

    private static void aplicarEstilo(DialogPane dialogPane) {
        dialogPane.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-background-color: #f8fafc;"
        );
    }

    public static void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Saavedra's");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);

        aplicarEstilo(alert.getDialogPane());

        alert.showAndWait();
    }

    public static void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Saavedra's");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);

        aplicarEstilo(alert.getDialogPane());

        alert.showAndWait();
    }

    public static void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Saavedra's");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);

        aplicarEstilo(alert.getDialogPane());

        alert.showAndWait();
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Saavedra's");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);

        aplicarEstilo(alert.getDialogPane());

        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}
