/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.model;

public class DetalleVenta {

    private int idDetalle;
    private int idVenta;
    private int idProducto;
    private int cantidad;
    private double precioUnitarioSinIva;
    private double subtotalLinea;
    private String nombreProducto;

    public DetalleVenta() {
    }

    public DetalleVenta(int idDetalle, int idVenta, int idProducto, int cantidad, double precioUnitarioSinIva, double subtotalLinea) {
        this.idDetalle = idDetalle;
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitarioSinIva = precioUnitarioSinIva;
        this.subtotalLinea = subtotalLinea;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitarioSinIva() {
        return precioUnitarioSinIva;
    }

    public void setPrecioUnitarioSinIva(double precioUnitarioSinIva) {
        this.precioUnitarioSinIva = precioUnitarioSinIva;
    }

    public double getSubtotalLinea() {
        return subtotalLinea;
    }

    public void setSubtotalLinea(double subtotalLinea) {
        this.subtotalLinea = subtotalLinea;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
}
