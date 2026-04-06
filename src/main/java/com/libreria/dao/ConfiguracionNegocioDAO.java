/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.dao;

import com.libreria.model.ConfiguracionNegocio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfiguracionNegocioDAO {

    public ConfiguracionNegocio obtenerConfiguracion() {
        String sql = "SELECT * FROM configuracion_negocio LIMIT 1";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                ConfiguracionNegocio config = new ConfiguracionNegocio();
                config.setId(rs.getInt("id"));
                config.setNombreEmpresa(rs.getString("nombre_empresa"));
                config.setDireccion(rs.getString("direccion"));
                config.setTelefono(rs.getString("telefono"));
                config.setCuit(rs.getString("cuit"));
                config.setMensajeTicket(rs.getString("mensaje_ticket"));
                return config;
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener configuración del negocio.");
            e.printStackTrace();
        }

        return null;
    }

    public boolean guardarOActualizar(ConfiguracionNegocio config) {
        ConfiguracionNegocio existente = obtenerConfiguracion();

        if (existente == null) {
            return insertar(config);
        } else {
            config.setId(existente.getId());
            return actualizar(config);
        }
    }

    private boolean insertar(ConfiguracionNegocio config) {
        String sql = """
                INSERT INTO configuracion_negocio
                (nombre_empresa, direccion, telefono, cuit, mensaje_ticket)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, config.getNombreEmpresa());
            ps.setString(2, config.getDireccion());
            ps.setString(3, config.getTelefono());
            ps.setString(4, config.getCuit());
            ps.setString(5, config.getMensajeTicket());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar configuración del negocio.");
            e.printStackTrace();
            return false;
        }
    }

    private boolean actualizar(ConfiguracionNegocio config) {
        String sql = """
                UPDATE configuracion_negocio
                SET nombre_empresa = ?, direccion = ?, telefono = ?, cuit = ?, mensaje_ticket = ?
                WHERE id = ?
                """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, config.getNombreEmpresa());
            ps.setString(2, config.getDireccion());
            ps.setString(3, config.getTelefono());
            ps.setString(4, config.getCuit());
            ps.setString(5, config.getMensajeTicket());
            ps.setInt(6, config.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar configuración del negocio.");
            e.printStackTrace();
            return false;
        }
    }
}
