/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.app;

import com.libreria.util.DBInitializer;
import com.libreria.util.LicenciaUtil;

public class Launcher {

    public static void main(String[] args) {
        DBInitializer.inicializar();

        if (LicenciaUtil.licenciaValida()) {
            MenuPrincipal.main(args);
        } else {
            ActivacionView.main(args);
        }
    }
}
