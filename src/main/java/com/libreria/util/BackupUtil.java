/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupUtil {

    private static final String CARPETA_APP = System.getProperty("user.home")
            + File.separator + "AppData"
            + File.separator + "Local"
            + File.separator + "Saavedras";

    public static void crearBackupAutomatico() {
        try {
            File origen = new File(CARPETA_APP, "libreria.db");

            if (!origen.exists()) {
                System.out.println("No existe libreria.db. No se creó backup.");
                return;
            }

            File carpetaBackups = new File(CARPETA_APP, "backups");
            if (!carpetaBackups.exists()) {
                carpetaBackups.mkdirs();
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String fecha = LocalDateTime.now().format(formatter);

            File destino = new File(carpetaBackups, "backup_" + fecha + ".db");

            Files.copy(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Backup automático creado: " + destino.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Error al crear backup automático.");
            e.printStackTrace();
        }
    }

    public static void restaurarBackup(File archivoBackup) {

        try {

            File baseActual = new File(CARPETA_APP, "libreria.db");

            if (!archivoBackup.exists()) {
                System.out.println("El backup seleccionado no existe.");
                return;
            }

            // Crear backup de seguridad antes de restaurar
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String fecha = LocalDateTime.now().format(formatter);

            File copiaSeguridad = new File(CARPETA_APP, "backup_pre_restore_" + fecha + ".db");

            Files.copy(baseActual.toPath(), copiaSeguridad.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Restaurar
            Files.copy(archivoBackup.toPath(), baseActual.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Backup restaurado correctamente.");

        } catch (Exception e) {

            System.out.println("Error al restaurar backup.");
            e.printStackTrace();

        }

    }
}
