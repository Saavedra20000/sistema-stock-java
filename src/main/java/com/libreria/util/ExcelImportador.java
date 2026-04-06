/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.util;

import com.libreria.dao.ConexionBD;
import com.libreria.model.Categoria;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelImportador {

    public static int importarProductos(File archivo) throws Exception {
        int importados = 0;

        try (FileInputStream fis = new FileInputStream(archivo); Workbook workbook = new XSSFWorkbook(fis); Connection con = ConexionBD.conectar()) {

            Sheet sheet = workbook.getSheetAt(0);

            String sql = """
                    INSERT INTO productos
                    (codigo, codigo_barras, nombre, descripcion,
                     precio_compra, precio_venta, stock, stock_minimo,
                     id_categoria, activo)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)
                    """;

            Set<String> codigosExcel = new HashSet<>();

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                    Row row = sheet.getRow(i);

                    if (row == null) {
                        continue;
                    }

                    String codigo = getString(row.getCell(0)).trim();

                    if (codigo.isBlank()) {
                        System.out.println("Fila " + i + ": código vacío, se omite.");
                        continue;
                    }

                    if (codigosExcel.contains(codigo)) {
                        System.out.println("Fila " + i + ": código repetido en Excel -> " + codigo);
                        continue;
                    }

                    if (existeCodigo(con, codigo)) {
                        System.out.println("Fila " + i + ": código ya existe en BD -> " + codigo);
                        continue;
                    }
                    String rubro = getString(row.getCell(2));
                    int idCategoria = obtenerCategoria(con, rubro);
                    codigosExcel.add(codigo);

                    ps.setString(1, codigo);                    // CODIGO
                    ps.setString(2, "");                       // codigo barras
                    ps.setString(3, getString(row.getCell(1))); // ARTICULO
                    ps.setString(4, "");                       // descripcion
                    ps.setDouble(5, getDouble(row.getCell(6))); // FINALUNIT
                    ps.setDouble(6, getDouble(row.getCell(7))); // PUBLICOUNIT
                    ps.setInt(7, (int) getDouble(row.getCell(3))); // STOCK
                    ps.setInt(8, 1);                              // stock minimo
                    ps.setInt(9, idCategoria);                              // categoria

                    ps.addBatch();
                    importados++;
                }
                ps.executeBatch();
            }
        }

        return importados;
    }

    private static boolean existeCodigo(Connection con, String codigo) throws Exception {
        String sql = "SELECT 1 FROM productos WHERE codigo = ? LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static String getString(Cell cell) {
        if (cell == null) {
            return "";
        }

        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    private static double getDouble(Cell cell) {
        if (cell == null) {
            return 0;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            }

            String texto = new DataFormatter().formatCellValue(cell).trim();
            if (texto.isEmpty()) {
                return 0;
            }

            texto = texto.replace(".", "").replace(",", ".");
            return Double.parseDouble(texto);

        } catch (Exception e) {
            return 0;
        }
    }

    private static int obtenerCategoria(Connection con, String nombre) throws Exception {

        if (nombre == null || nombre.isBlank()) {
            return 1;
        }

        String buscar = "SELECT id_categoria FROM categorias WHERE nombre = ?";

        try (PreparedStatement ps = con.prepareStatement(buscar)) {

            ps.setString(1, nombre);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_categoria");
            }
        }

        String insertar = "INSERT INTO categorias (nombre, descripcion, activo) VALUES (?, '', 1)";

        try (PreparedStatement ps = con.prepareStatement(insertar, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 1;
    }
}
