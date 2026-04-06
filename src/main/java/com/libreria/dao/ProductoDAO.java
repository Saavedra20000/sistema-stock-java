/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.dao;

import com.libreria.model.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT * FROM productos ORDER BY nombre";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto p = mapearProducto(rs);
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar productos.");
            e.printStackTrace();
        }

        return lista;
    }

    public boolean agregarProducto(Producto p) {
        String sql = "INSERT INTO productos (codigo, codigo_barras, nombre, descripcion, precio_compra, precio_venta, stock, stock_minimo, id_categoria, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getCodigoBarras());
            ps.setString(3, p.getNombre());
            ps.setString(4, p.getDescripcion());
            ps.setDouble(5, p.getPrecioCompra());
            ps.setDouble(6, p.getPrecioVenta());
            ps.setInt(7, p.getStock());
            ps.setInt(8, p.getStockMinimo());
            ps.setInt(9, p.getIdCategoria());
            ps.setBoolean(10, p.isActivo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar producto.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean modificarProducto(Producto p) {
        String sql = "UPDATE productos SET codigo = ?, codigo_barras = ?, nombre = ?, descripcion = ?, precio_compra = ?, precio_venta = ?, stock = ?, stock_minimo = ?, id_categoria = ?, activo = ? WHERE id_producto = ?";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getCodigoBarras());
            ps.setString(3, p.getNombre());
            ps.setString(4, p.getDescripcion());
            ps.setDouble(5, p.getPrecioCompra());
            ps.setDouble(6, p.getPrecioVenta());
            ps.setInt(7, p.getStock());
            ps.setInt(8, p.getStockMinimo());
            ps.setInt(9, p.getIdCategoria());
            ps.setBoolean(10, p.isActivo());
            ps.setInt(11, p.getIdProducto());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al modificar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarProducto(int idProducto) {
        String sql = "DELETE FROM productos WHERE id_producto = ?";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar producto.");
            e.printStackTrace();
            return false;
        }
    }

    public List<Producto> buscarProductos(String texto, Integer idCategoria) {
        List<Producto> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM productos WHERE 1=1");

        if (texto != null && !texto.trim().isEmpty()) {
            sql.append(" AND (codigo LIKE ? OR nombre LIKE ? OR codigo_barras LIKE ?)");
        }

        if (idCategoria != null) {
            sql.append(" AND id_categoria = ?");
        }

        sql.append(" ORDER BY nombre");

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int index = 1;

            if (texto != null && !texto.trim().isEmpty()) {
                String patron = "%" + texto.trim() + "%";
                ps.setString(index++, patron);
                ps.setString(index++, patron);
                ps.setString(index++, patron);
            }

            if (idCategoria != null) {
                ps.setInt(index++, idCategoria);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = mapearProducto(rs);
                    lista.add(p);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar productos.");
            e.printStackTrace();
        }

        return lista;
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("id_producto"));
        p.setCodigo(rs.getString("codigo"));
        p.setCodigoBarras(rs.getString("codigo_barras"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecioCompra(rs.getDouble("precio_compra"));
        p.setPrecioVenta(rs.getDouble("precio_venta"));
        p.setStock(rs.getInt("stock"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setIdCategoria(rs.getInt("id_categoria"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }

    public Producto buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM productos WHERE codigo = ?";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearProducto(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar producto por código: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<Producto> listarProductosConStockBajo() {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT * FROM productos WHERE stock <= stock_minimo ORDER BY nombre";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto p = mapearProducto(rs);
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar productos con stock bajo: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    public Producto buscarPorCodigoBarras(String codigoBarras) {
        String sql = "SELECT * FROM productos WHERE codigo_barras = ?";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigoBarras);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar producto por código de barras: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public int contarProductos() {
        String sql = "SELECT COUNT(*) FROM productos";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error al contar productos.");
            e.printStackTrace();
        }

        return 0;
    }

    public int contarStockBajo() {
        String sql = "SELECT COUNT(*) FROM productos WHERE stock <= stock_minimo";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error al contar stock bajo.");
            e.printStackTrace();
        }

        return 0;
    }

    public List<Producto> listarProductosStockBajo() {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT * FROM productos WHERE stock <= stock_minimo ORDER BY nombre";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto p = mapearProducto(rs);
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar productos con stock bajo.");
            e.printStackTrace();
        }

        return lista;
    }
}
