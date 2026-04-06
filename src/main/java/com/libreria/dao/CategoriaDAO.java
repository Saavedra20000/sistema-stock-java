/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.dao;

import com.libreria.model.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listarCategorias() {
        List<Categoria> lista = new ArrayList<>();

        String sql = "SELECT * FROM categorias WHERE activo = 1 ORDER BY nombre";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categoria c = new Categoria();
                c.setIdCategoria(rs.getInt("id_categoria"));
                c.setNombre(rs.getString("nombre"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setActivo(rs.getBoolean("activo"));

                lista.add(c);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar categorias.");
            e.printStackTrace();
        }

        return lista;
    }

    public boolean agregarCategoria(Categoria c) {
        String sql = "INSERT INTO categorias (nombre, descripcion, activo) VALUES (?, ?, ?)";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt(3, c.isActivo() ? 1 : 0);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar categoria.");
            e.printStackTrace();

            javax.swing.JOptionPane.showMessageDialog(
                    null,
                    "Error SQLite: " + e.getMessage(),
                    "Error real",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );

            return false;
        }
    }

    public boolean modificarCategoria(Categoria c) {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ? WHERE id_categoria = ?";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt(3, c.getIdCategoria());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al modificar categoria.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarCategoria(int idCategoria) {
        String sql = "UPDATE categorias SET activo = 0 WHERE id_categoria = ?";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar categoria.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeCategoriaPorNombre(String nombre) {
        String sql = "SELECT COUNT(*) FROM categorias WHERE nombre = ? AND activo = 1";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al verificar categoría.");
            e.printStackTrace();
        }

        return false;
    }

    public boolean reactivarCategoria(String nombre) {
        String sql = "UPDATE categorias SET activo = 1 WHERE nombre = ?";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int contarCategoriasActivas() {
        String sql = "SELECT COUNT(*) FROM categorias WHERE activo = 1";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error al contar categorías.");
            e.printStackTrace();
        }

        return 0;
    }

    public Categoria buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM categorias WHERE nombre = ?";

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Categoria c = new Categoria();
                    c.setIdCategoria(rs.getInt("id_categoria"));
                    c.setNombre(rs.getString("nombre"));
                    return c;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar categoría por nombre: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
