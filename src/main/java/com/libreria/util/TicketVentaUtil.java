/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.util;

import com.libreria.model.ItemVenta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.libreria.dao.ConfiguracionNegocioDAO;
import com.libreria.model.ConfiguracionNegocio;

public class TicketVentaUtil {

    public static void mostrarTicket(
            List<ItemVenta> items,
            String metodoPago,
            Integer cuotas,
            double subtotal,
            double descuento,
            double recargo,
            double totalFinal
    ) {
        String ticket = generarTextoTicket(
                items,
                metodoPago,
                cuotas,
                subtotal,
                descuento,
                totalFinal
        );

        TextArea areaTicket = new TextArea(ticket);
        areaTicket.setEditable(false);
        areaTicket.setWrapText(false);
        areaTicket.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 11px;");

        Button btnImprimir = new Button("Imprimir");
        btnImprimir.setOnAction(e -> imprimirTexto(ticket));

        VBox root = new VBox(10, areaTicket, btnImprimir);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        Stage stage = new Stage();
        stage.setTitle("Ticket de venta");
        stage.setScene(new Scene(root, 380, 650));
        stage.show();
    }

    private static String generarTextoTicket(
            List<ItemVenta> items,
            String metodoPago,
            Integer cuotas,
            double subtotal,
            double descuento,
            double totalFinal
    ) {
        StringBuilder sb = new StringBuilder();

        ConfiguracionNegocioDAO configDAO = new ConfiguracionNegocioDAO();
        ConfiguracionNegocio config = configDAO.obtenerConfiguracion();

        String nombreEmpresa = "Saavedra's";
        String direccion = "";
        String mensajeTicket = "";

        if (config != null) {
            if (config.getNombreEmpresa() != null && !config.getNombreEmpresa().isBlank()) {
                nombreEmpresa = config.getNombreEmpresa().trim();
            }
            if (config.getDireccion() != null) {
                direccion = config.getDireccion().trim();
            }
        }

        LocalDateTime ahora = LocalDateTime.now();
        String fecha = ahora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String hora = ahora.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        sb.append(nombreEmpresa.toUpperCase()).append("\n");

        if (!direccion.isBlank()) {
            sb.append("Dir: ").append(direccion).append("\n");
        }

        sb.append("====================================\n");
        sb.append("Fecha: ").append(fecha).append("\n");
        sb.append("Hora : ").append(hora).append("\n");
        sb.append("====================================\n");
        sb.append("Medio de pago: ").append(metodoPago != null ? metodoPago : "").append("\n");
        sb.append("Cuotas       : ").append(cuotas != null ? cuotas : 1).append("\n");
        sb.append("====================================\n");
        sb.append("====================================\n");
        sb.append("CANT PRODUCTO           P.UNIT SUBT\n");
        sb.append("------------------------------------\n");

        for (ItemVenta item : items) {
            String nombre = item.getProducto().getNombre();
            if (item.getProducto().getDescripcion() != null && !item.getProducto().getDescripcion().isBlank()) {
                nombre += " - " + item.getProducto().getDescripcion();
            }

            String nombreCorto = recortar(nombre, 18);

            double precio = item.getProducto().getPrecioVenta();
            double sub = item.getSubtotalSinIva();

            sb.append(String.format("%-4s %-18s %6.2f %6.2f%n",
                    item.getCantidad(),
                    nombreCorto,
                    precio,
                    sub
            ));
        }

        sb.append("------------------------------------\n");
        sb.append(String.format("Total s/impuestos: %12.2f%n", subtotal));

        if (descuento > 0) {
            sb.append(String.format("Descuento       : %12.2f%n", descuento));
        }

        sb.append(String.format("TOTAL           : %12.2f%n", totalFinal));
        sb.append("====================================\n");
        sb.append("      COMPROBANTE NO FISCAL\n");

        if (!mensajeTicket.isBlank()) {
            sb.append("====================================\n");
            sb.append(mensajeTicket).append("\n");
        }

        sb.append("====================================\n");

        return sb.toString();
    }

    private static String recortar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= max) {
            return texto;
        }
        return texto.substring(0, max - 3) + "...";
    }

    private static void imprimirTexto(String texto) {
        TextArea area = new TextArea(texto);
        area.setEditable(false);
        area.setWrapText(false);
        area.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 11px;");

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            boolean ok = job.printPage(area);
            if (ok) {
                job.endJob();
            }
        }
    }
}
