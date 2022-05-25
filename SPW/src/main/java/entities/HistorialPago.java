/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
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
@Table(name = "historial_pago")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HistorialPago.findAll", query = "SELECT h FROM HistorialPago h"),
    @NamedQuery(name = "HistorialPago.findByIdHistorialP", query = "SELECT h FROM HistorialPago h WHERE h.idHistorialP = :idHistorialP"),
    @NamedQuery(name = "HistorialPago.findByCuotaNum", query = "SELECT h FROM HistorialPago h WHERE h.cuotaNum = :cuotaNum"),
    @NamedQuery(name = "HistorialPago.findByCargoMora", query = "SELECT h FROM HistorialPago h WHERE h.cargoMora = :cargoMora"),
    @NamedQuery(name = "HistorialPago.findByInteres", query = "SELECT h FROM HistorialPago h WHERE h.interes = :interes"),
    @NamedQuery(name = "HistorialPago.findByCapital", query = "SELECT h FROM HistorialPago h WHERE h.capital = :capital"),
    @NamedQuery(name = "HistorialPago.findByFecha", query = "SELECT h FROM HistorialPago h WHERE h.fecha = :fecha"),
    @NamedQuery(name = "HistorialPago.findBynotaPago", query = "SELECT h FROM HistorialPago h WHERE h.notaPago = :notaPago")})
public class HistorialPago implements Serializable {

    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_historial_p")
    private Integer idHistorialP;
    @Column(name = "cuota_num")
    private Integer cuotaNum;
    @Column(name = "cargo_mora")
    private double cargoMora;
    @Column(name = "interes")
    private double interes;
    @Column(name = "capital")
    private double capital;  
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "nota_pago")
    private String notaPago;
    @JoinColumn(name = "id_prestamo", referencedColumnName = "id_prestamo")
    @ManyToOne
    private Prestamo idPrestamo;

    public HistorialPago() {
    }

    public HistorialPago(Integer idHistorialP) {
        this.idHistorialP = idHistorialP;
    }

    public Integer getIdHistorialP() {
        return idHistorialP;
    }

    public void setIdHistorialP(Integer idHistorialP) {
        this.idHistorialP = idHistorialP;
    }

    public Integer getCuotaNum() {
        return cuotaNum;
    }

    public void setCuotaNum(Integer cuotaNum) {
        this.cuotaNum = cuotaNum;
    }

    public double getCargoMora() {
        return cargoMora;
    }

    public void setCargoMora(double cargoMora) {
        this.cargoMora = cargoMora;
    }

    public double getInteres() {
        return interes;
    }

    public void setInteres(double interes) {
        this.interes = interes;
    }

    public double getCapital() {
        return capital;
    }

    public void setCapital(double capital) {
        this.capital = capital;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idHistorialP != null ? idHistorialP.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HistorialPago)) {
            return false;
        }
        HistorialPago other = (HistorialPago) object;
        if ((this.idHistorialP == null && other.idHistorialP != null) || (this.idHistorialP != null && !this.idHistorialP.equals(other.idHistorialP))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "test.HistorialPago[ idHistorialP=" + idHistorialP + " ]";
    }
    
}
