/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;

public class LicenciaUtil {

    private static final String CARPETA_APP = System.getProperty("user.home")
            + File.separator + "AppData"
            + File.separator + "Local"
            + File.separator + "Saavedras";

    private static final String ARCHIVO_LICENCIA = CARPETA_APP + File.separator + "licencia.key";

    public static String obtenerIdEquipo() {
        String nombrePc = System.getenv("COMPUTERNAME");
        String usuario = System.getProperty("user.name");
        String base = (nombrePc != null ? nombrePc : "PC") + "|" + (usuario != null ? usuario : "USER");

        return sha256(base).substring(0, 16).toUpperCase();
    }

    public static boolean licenciaValida() {
        try {
            File archivo = new File(ARCHIVO_LICENCIA);

            if (!archivo.exists()) {
                return false;
            }

            String contenido = Files.readString(archivo.toPath()).trim();
            String idEquipo = obtenerIdEquipo();

            return contenido.equals(idEquipo);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String rutaLicencia() {
        return ARCHIVO_LICENCIA;
    }

    private static String sha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) {
                    hex.append('0');
                }
                hex.append(h);
            }
            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error generando hash", e);
        }
    }
}