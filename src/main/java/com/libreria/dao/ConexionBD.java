/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String CARPETA_APP = System.getProperty("user.home")
            + File.separator + "AppData"
            + File.separator + "Local"
            + File.separator + "Saavedras";

    private static final String URL = "jdbc:sqlite:" + CARPETA_APP + File.separator + "libreria.db";

    public static Connection conectar() throws SQLException {
        File carpeta = new File(CARPETA_APP);

        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver SQLite.", e);
        }

        return DriverManager.getConnection(URL);
    }
}
