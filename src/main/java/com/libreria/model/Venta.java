/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.model;

import java.time.LocalDateTime;

public class Venta {

    private int idVenta;
    private LocalDateTime fecha;
    private double subtotalSinIva;
    private double iva;
    private double totalConIva;

    public Venta() {
    }

    public Venta(int idVenta, LocalDateTime fecha, double subtotalSinIva, double iva, double totalConIva) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.subtotalSinIva = subtotalSinIva;
        this.iva = iva;
        this.totalConIva = totalConIva;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getSubtotalSinIva() {
        return subtotalSinIva;
    }

    public void setSubtotalSinIva(double subtotalSinIva) {
        this.subtotalSinIva = subtotalSinIva;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getTotalConIva() {
        return totalConIva;
    }

    public void setTotalConIva(double totalConIva) {
        this.totalConIva = totalConIva;
    }
}
