/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.util.Date;

/**
 *
 * @author emmanuel
 */
public class PagoCuota {
    
    private boolean check;
    private Prestamo idPrestamo;
    private Integer cuotaNum;
    private double capital;
    private double interes;
    private double cargoMora;
    private double cuota;
    private String notaPago;
    private Date fecha;
    
    public PagoCuota(){
        check = false;
        idPrestamo = null;
        cuotaNum = 0;
        capital = 0;
        interes = 0;
        cargoMora = 0;
        cuota = 0;
        notaPago = "";
        fecha = null;
    }
    
    //GETTERS & SETTERS

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public Integer getCuotaNum() {
        return cuotaNum;
    }

    public void setCuotaNum(Integer cuotaNum) {
        this.cuotaNum = cuotaNum;
    }

    public double getCapital() {
        return capital;
    }

    public void setCapital(double capital) {
        this.capital = capital;
    }

    public double getInteres() {
        return interes;
    }

    public void setInteres(double interes) {
        this.interes = interes;
    }

    public double getCuota() {
        return cuota;
    }

    public void setCuota(double cuota) {
        this.cuota = cuota;
    }

    public String getNotaPago() {
        return notaPago;
    }

    public void setNotaPago(String notaPago) {
        this.notaPago = notaPago;
    }

    public Prestamo getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(Prestamo idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getCargoMora() {
        return cargoMora;
    }

    public void setCargoMora(double cargoMora) {
        this.cargoMora = cargoMora;
    }
    
    
}
