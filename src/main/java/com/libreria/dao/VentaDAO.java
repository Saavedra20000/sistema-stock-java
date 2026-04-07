/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.dao;

import com.libreria.model.DetalleVenta;
import com.libreria.model.ItemVenta;
import com.libreria.model.VentaResumen;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VentaDAO {

    public boolean registrarVenta(List<ItemVenta> items, String metodoPago, int cuotas, double porcentajeDescuento, double porcentajeRecargo) {
        Connection con = null;
        PreparedStatement psVenta = null;
        PreparedStatement psDetalle = null;
        PreparedStatement psStock = null;
        PreparedStatement psBuscar = null;
        ResultSet rs = null;

        try {
            if (items == null || items.isEmpty()) {
                System.out.println("La venta no tiene productos.");
                return false;
            }

            double subtotal = 0;

            con = ConexionBD.conectar();
            con.setAutoCommit(false);

            String sqlBuscar = "SELECT id_producto, stock, precio_venta, nombre FROM productos WHERE id_producto = ?";
            psBuscar = con.prepareStatement(sqlBuscar);

            for (ItemVenta item : items) {
                if (item.getProducto() == null || item.getCantidad() <= 0) {
                    System.out.println("Item inválido en la venta.");
                    con.rollback();
                    return false;
                }

                psBuscar.setInt(1, item.getProducto().getIdProducto());
                rs = psBuscar.executeQuery();

                if (!rs.next()) {
                    System.out.println("Producto no encontrado en BD.");
                    con.rollback();
                    return false;
                }

                int stockActual = rs.getInt("stock");
                double precioVenta = rs.getDouble("precio_venta");
                String nombre = rs.getString("nombre");

                if (stockActual < item.getCantidad()) {
                    System.out.println("Stock insuficiente para: " + nombre);
                    con.rollback();
                    return false;
                }

                subtotal += precioVenta * item.getCantidad();

                rs.close();
                rs = null;
            }

            double descuento = subtotal * (porcentajeDescuento / 100.0);
            double recargo = subtotal * (porcentajeRecargo / 100.0);
            double iva = 0.0; // si después querés calcular IVA real, lo cambiamos
            double totalFinal = subtotal - descuento + recargo;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String fecha = LocalDateTime.now().format(formatter);

            String sqlVenta = "INSERT INTO ventas (fecha, subtotal_sin_iva, iva, total_con_iva, metodo_pago, cuotas, descuento, recargo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            psVenta = con.prepareStatement(sqlVenta, PreparedStatement.RETURN_GENERATED_KEYS);
            psVenta.setString(1, fecha);
            psVenta.setDouble(2, subtotal);
            psVenta.setDouble(3, iva);
            psVenta.setDouble(4, totalFinal);
            psVenta.setString(5, metodoPago);
            psVenta.setInt(6, cuotas);
            psVenta.setDouble(7, descuento);
            psVenta.setDouble(8, recargo);
            psVenta.executeUpdate();

            rs = psVenta.getGeneratedKeys();
            int idVentaGenerada = 0;

            if (rs.next()) {
                idVentaGenerada = rs.getInt(1);
            }

            if (idVentaGenerada == 0) {
                con.rollback();
                return false;
            }

            String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario_sin_iva, subtotal_linea) VALUES (?, ?, ?, ?, ?)";
            psDetalle = con.prepareStatement(sqlDetalle);

            String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";
            psStock = con.prepareStatement(sqlStock);

            for (ItemVenta item : items) {
                psBuscar.setInt(1, item.getProducto().getIdProducto());
                rs = psBuscar.executeQuery();

                if (!rs.next()) {
                    con.rollback();
                    return false;
                }

                double precioVenta = rs.getDouble("precio_venta");
                double subtotalLinea = precioVenta * item.getCantidad();

                rs.close();
                rs = null;

                psDetalle.setInt(1, idVentaGenerada);
                psDetalle.setInt(2, item.getProducto().getIdProducto());
                psDetalle.setInt(3, item.getCantidad());
                psDetalle.setDouble(4, precioVenta);
                psDetalle.setDouble(5, subtotalLinea);
                psDetalle.executeUpdate();

                psStock.setInt(1, item.getCantidad());
                psStock.setInt(2, item.getProducto().getIdProducto());
                psStock.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            System.out.println("Error al registrar la venta: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (psBuscar != null) {
                    psBuscar.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (psVenta != null) {
                    psVenta.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (psDetalle != null) {
                    psDetalle.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (psStock != null) {
                    psStock.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<VentaResumen> listarVentas() {
        return listarVentasPorRango(null, null);
    }

    // CAMBIO: filtro por rango de fechas
    public List<VentaResumen> listarVentasPorRango(LocalDate desde, LocalDate hasta) {
        List<VentaResumen> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM ventas WHERE 1=1");

        if (desde != null) {
            sql.append(" AND date(fecha) >= date(?)");
        }

        if (hasta != null) {
            sql.append(" AND date(fecha) <= date(?)");
        }

        sql.append(" ORDER BY fecha DESC");

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int index = 1;

            if (desde != null) {
                ps.setString(index++, desde.toString()); // yyyy-MM-dd
            }

            if (hasta != null) {
                ps.setString(index++, hasta.toString()); // yyyy-MM-dd
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VentaResumen v = new VentaResumen();
                    v.setIdVenta(rs.getInt("id_venta"));

                    Timestamp ts = rs.getTimestamp("fecha");
                    if (ts != null) {
                        v.setFecha(ts.toLocalDateTime());
                    }

                    v.setSubtotalSinIva(rs.getDouble("subtotal_sin_iva"));
                    v.setIva(rs.getDouble("iva"));
                    v.setTotalConIva(rs.getDouble("total_con_iva"));
                    v.setMetodoPago(rs.getString("metodo_pago"));
                    v.setCuotas(rs.getInt("cuotas"));
                    v.setDescuento(rs.getDouble("descuento"));
                    v.setRecargo(rs.getDouble("recargo"));

                    lista.add(v);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar ventas: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    public List<DetalleVenta> obtenerDetalleVenta(int idVenta) {
        List<DetalleVenta> lista = new ArrayList<>();

        String sql = """
        SELECT 
            dv.id_detalle,
            dv.id_venta,
            dv.id_producto,
            p.nombre AS nombre_producto,
            dv.cantidad,
            dv.precio_unitario_sin_iva,
            dv.subtotal_linea
        FROM detalle_venta dv
        INNER JOIN productos p ON dv.id_producto = p.id_producto
        WHERE dv.id_venta = ?
        ORDER BY dv.id_detalle
    """;

        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVenta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta detalle = new DetalleVenta();

                    detalle.setIdDetalle(rs.getInt("id_detalle"));
                    detalle.setIdVenta(rs.getInt("id_venta"));
                    detalle.setNombreProducto(rs.getString("nombre_producto"));
// NO tenemos nombreProducto en el modelo → lo ignoramos por ahora
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setPrecioUnitarioSinIva(rs.getDouble("precio_unitario_sin_iva"));
                    detalle.setSubtotalLinea(rs.getDouble("subtotal_linea"));

                    lista.add(detalle);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public int contarVentas() {
        String sql = "SELECT COUNT(*) FROM ventas";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error al contar ventas.");
            e.printStackTrace();
        }

        return 0;
    }
}
