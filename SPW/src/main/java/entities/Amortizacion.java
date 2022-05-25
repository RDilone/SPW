/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author emmanuel
 */
@Entity
@Table(name = "amortizacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Amortizacion.findAll", query = "SELECT a FROM Amortizacion a order by a.idPago desc"),
    @NamedQuery(name = "Amortizacion.findByIdPago", query = "SELECT a FROM Amortizacion a WHERE a.idPago = :idPago"),
    @NamedQuery(name = "Amortizacion.findByCuotaNum", query = "SELECT a FROM Amortizacion a WHERE a.cuotaNum = :cuotaNum"),
    @NamedQuery(name = "Amortizacion.findByPagoCapital", query = "SELECT a FROM Amortizacion a WHERE a.pagoCapital = :pagoCapital"),
    @NamedQuery(name = "Amortizacion.findByPagoInteres", query = "SELECT a FROM Amortizacion a WHERE a.pagoInteres = :pagoInteres"),
    @NamedQuery(name = "Amortizacion.findByCuota", query = "SELECT a FROM Amortizacion a WHERE a.cuota = :cuota"),
    @NamedQuery(name = "Amortizacion.findBySaldo", query = "SELECT a FROM Amortizacion a WHERE a.saldo = :saldo"),
    @NamedQuery(name = "Amortizacion.findByFecha", query = "SELECT a FROM Amortizacion a WHERE a.fecha = :fecha"),
    @NamedQuery(name = "Amortizacion.findByEstado", query = "SELECT a FROM Amortizacion a WHERE a.estado = :estado")})

public class Amortizacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_pago")
    private Integer idPago;
    @Column(name = "cuota_num")
    private Integer cuotaNum;
    @Column(name = "pago_capital")
    private double pagoCapital;
    @Column(name = "pago_interes")
    private double pagoInteres;
    @Column(name = "cuota")
    private double cuota;
    @Column(name = "saldo")
    private double saldo;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "estado")
    private String estado;
    @JoinColumn(name = "id_prestamo", referencedColumnName = "id_prestamo")
    @ManyToOne
    private Prestamo idPrestamo;

    public Amortizacion() {
    }

    public Amortizacion(Integer idPago) {
        this.idPago = idPago;
    }

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public Integer getCuotaNum() {
        return cuotaNum;
    }

    public void setCuotaNum(Integer cuotaNum) {
        this.cuotaNum = cuotaNum;
    }

    public double getPagoCapital() {
        return pagoCapital;
    }

    public void setPagoCapital(double pagoCapital) {
        this.pagoCapital = pagoCapital;
    }

    public double getPagoInteres() {
        return pagoInteres;
    }

    public void setPagoInteres(double pagoInteres) {
        this.pagoInteres = pagoInteres;
    }

    public double getCuota() {
        return cuota;
    }

    public void setCuota(double cuota) {
        this.cuota = cuota;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Prestamo getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(Prestamo idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPago != null ? idPago.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Amortizacion)) {
            return false;
        }
        Amortizacion other = (Amortizacion) object;
        if ((this.idPago == null && other.idPago != null) || (this.idPago != null && !this.idPago.equals(other.idPago))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "test.Amortizacion[ idPago=" + idPago + " ]";
    }
    
}
