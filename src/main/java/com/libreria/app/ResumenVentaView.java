/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.app;

import com.libreria.model.ItemVenta;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ResumenVentaView {

    // CAMBIO: ahora muestra metodo de pago, cuotas, descuento y recargo
    public static void mostrar(List<ItemVenta> items, String metodoPago, int cuotas, double porcentajeDescuento, double porcentajeRecargo) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Resumen de Venta");

        TextArea area = new TextArea();
        area.setEditable(false);
        area.setWrapText(true);

        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUMEN DE VENTA ===\n\n");

        double subtotal = 0;

        for (ItemVenta item : items) {
            double subtotalLinea = item.getSubtotalSinIva();
            subtotal += subtotalLinea;

            sb.append("Producto: ").append(item.getProducto().getNombre()).append("\n");
            sb.append("Código: ").append(item.getProducto().getCodigo()).append("\n");
            sb.append("Cantidad: ").append(item.getCantidad()).append("\n");
            sb.append("Precio unitario: $")
              .append(String.format("%.2f", item.getProducto().getPrecioVenta())).append("\n");
            sb.append("Subtotal línea: $")
              .append(String.format("%.2f", subtotalLinea)).append("\n");
            sb.append("-----------------------------------\n");
        }

        double descuento = subtotal * (porcentajeDescuento / 100.0);
        double recargo = subtotal * (porcentajeRecargo / 100.0);
        double total = subtotal - descuento + recargo;

        sb.append("\nMétodo de pago: ").append(metodoPago).append("\n");
        if ("Crédito".equalsIgnoreCase(metodoPago)) {
            sb.append("Cuotas: ").append(cuotas).append("\n");
        }

        sb.append("\nSubtotal: $").append(String.format("%.2f", subtotal)).append("\n");
        sb.append("Descuento %: ").append(String.format("%.2f", porcentajeDescuento)).append("%\n");
        sb.append("Descuento $: ").append(String.format("%.2f", descuento)).append("\n");
        sb.append("Recargo %: ").append(String.format("%.2f", porcentajeRecargo)).append("%\n");
        sb.append("Recargo $: ").append(String.format("%.2f", recargo)).append("\n");
        sb.append("Total final: $").append(String.format("%.2f", total)).append("\n");

        area.setText(sb.toString());

        VBox root = new VBox(10, new Label("Venta registrada correctamente"), area);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 520, 550);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
