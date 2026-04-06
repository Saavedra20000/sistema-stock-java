/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.util;

import java.sql.Connection;
import java.sql.Statement;

import com.libreria.dao.ConexionBD;

public class DBInitializer {

    public static void inicializar() {
        try (Connection con = ConexionBD.conectar(); Statement st = con.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS categorias (
                    id_categoria INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE,
                    descripcion TEXT,
                    activo INTEGER NOT NULL DEFAULT 1
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre_usuario TEXT NOT NULL UNIQUE,
                    clave TEXT NOT NULL,
                    rol TEXT NOT NULL,
                    activo INTEGER NOT NULL DEFAULT 1
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS productos (
                    id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
                    codigo TEXT NOT NULL UNIQUE,
                    nombre TEXT NOT NULL,
                    descripcion TEXT,
                    precio_compra REAL NOT NULL,
                    precio_venta REAL NOT NULL,
                    stock INTEGER NOT NULL,
                    stock_minimo INTEGER NOT NULL,
                    id_categoria INTEGER NOT NULL,
                    activo INTEGER NOT NULL DEFAULT 1,
                    codigo_barras TEXT,
                    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS ventas (
                    id_venta INTEGER PRIMARY KEY AUTOINCREMENT,
                    fecha TEXT NOT NULL,
                    subtotal_sin_iva REAL NOT NULL,
                    iva REAL NOT NULL,
                    total_con_iva REAL NOT NULL,
                    metodo_pago TEXT,
                    cuotas INTEGER,
                    descuento REAL NOT NULL DEFAULT 0,
                    recargo REAL NOT NULL DEFAULT 0
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS detalle_venta (
                    id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_venta INTEGER NOT NULL,
                    id_producto INTEGER NOT NULL,
                    cantidad INTEGER NOT NULL,
                    precio_unitario_sin_iva REAL NOT NULL,
                    subtotal_linea REAL NOT NULL,
                    FOREIGN KEY (id_venta) REFERENCES ventas(id_venta),
                    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
                )
            """);
            st.execute("CREATE INDEX IF NOT EXISTS idx_productos_nombre ON productos(nombre)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_productos_codigo ON productos(codigo)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_productos_codigo_barras ON productos(codigo_barras)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_productos_categoria ON productos(id_categoria)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_ventas_fecha ON ventas(fecha)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_detalle_venta_id_venta ON detalle_venta(id_venta)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_detalle_venta_id_producto ON detalle_venta(id_producto)");

            st.execute("""
CREATE TABLE IF NOT EXISTS configuracion_negocio (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_empresa TEXT,
    direccion TEXT,
    telefono TEXT,
    cuit TEXT,
    mensaje_ticket TEXT
)
     """);

            System.out.println("Base de datos lista.");

        } catch (Exception e) {
            System.out.println("Error al inicializar la base.");
            e.printStackTrace();
        }
    }
}
