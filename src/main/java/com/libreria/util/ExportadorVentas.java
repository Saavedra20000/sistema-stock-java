/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.util;

import com.libreria.model.VentaResumen;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportadorVentas {

    public static void exportarExcel(List<VentaResumen> ventas, String rutaArchivo) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Historial Ventas");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID Venta");
        header.createCell(1).setCellValue("Fecha");
        header.createCell(2).setCellValue("Método Pago");
        header.createCell(3).setCellValue("Cuotas");
        header.createCell(4).setCellValue("Subtotal");
        header.createCell(5).setCellValue("Descuento");
        header.createCell(6).setCellValue("Recargo");
        header.createCell(7).setCellValue("Total Final");

        int fila = 1;

        for (VentaResumen v : ventas) {
            Row row = sheet.createRow(fila++);
            row.createCell(0).setCellValue(v.getIdVenta());
            row.createCell(1).setCellValue(v.getFecha() != null ? v.getFecha().toString() : "");
            row.createCell(2).setCellValue(v.getMetodoPago() != null ? v.getMetodoPago() : "");
            row.createCell(3).setCellValue(v.getCuotas());
            row.createCell(4).setCellValue(v.getSubtotalSinIva());
            row.createCell(5).setCellValue(v.getDescuento());
            row.createCell(6).setCellValue(v.getRecargo());
            row.createCell(7).setCellValue(v.getTotalConIva());
        }

        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            workbook.write(fos);
        }

        workbook.close();
    }

    public static void exportarPDF(List<VentaResumen> ventas, String rutaArchivo) throws IOException, DocumentException {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
            document.open();

            document.add(new Paragraph("Historial de Ventas"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);

            table.addCell("ID Venta");
            table.addCell("Fecha");
            table.addCell("Método Pago");
            table.addCell("Cuotas");
            table.addCell("Subtotal");
            table.addCell("Descuento");
            table.addCell("Recargo");
            table.addCell("Total Final");

            for (VentaResumen v : ventas) {
                table.addCell(String.valueOf(v.getIdVenta()));
                table.addCell(v.getFecha() != null ? v.getFecha().toString() : "");
                table.addCell(v.getMetodoPago() != null ? v.getMetodoPago() : "");
                table.addCell(String.valueOf(v.getCuotas()));
                table.addCell(String.format("%.2f", v.getSubtotalSinIva()));
                table.addCell(String.format("%.2f", v.getDescuento()));
                table.addCell(String.format("%.2f", v.getRecargo()));
                table.addCell(String.format("%.2f", v.getTotalConIva()));
            }

            document.add(table);

        } finally {
            document.close();
        }
    }
}
