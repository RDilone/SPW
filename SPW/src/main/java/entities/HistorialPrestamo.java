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
import javax.persistence.CascadeType;
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
@Table(name = "historial_prestamo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HistorialPrestamo.findAll", query = "SELECT h FROM HistorialPrestamo h"),
    @NamedQuery(name = "HistorialPrestamo.findByIdHistorialP", query = "SELECT h FROM HistorialPrestamo h WHERE h.idHistorialP = :idHistorialP"),    
    @NamedQuery(name = "HistorialPrestamo.findByDescripcionCambio", query = "SELECT h FROM HistorialPrestamo h WHERE h.descripcionCambio = :descripcionCambio"),
    @NamedQuery(name = "HistorialPrestamo.findByTipoCambio", query = "SELECT h FROM HistorialPrestamo h WHERE h.tipoCambio = :tipoCambio"),
    @NamedQuery(name = "HistorialPrestamo.findByFechaCambio", query = "SELECT h FROM HistorialPrestamo h WHERE h.fechaCambio = :fechaCambio")})
public class HistorialPrestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Basic(optional = false)
    @Column(name = "id_historial_p")
    private Integer idHistorialP;  
    @Size(max = 2147483647)
    @Column(name = "descripcion_cambio")
    private String descripcionCambio;
    @Column(name = "fecha_cambio") 
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCambio;
    @Size(max = 2147483647)
    @Column(name = "tipo_cambio")
    private String tipoCambio;   
    @JoinColumn(name = "id_prestamo", referencedColumnName = "id_prestamo")
    @ManyToOne
    private Prestamo idPrestamo;

    public HistorialPrestamo() {
    }

    public HistorialPrestamo(Integer idHistorialP) {
        this.idHistorialP = idHistorialP;
    }

    public Integer getIdHistorialP() {
        return idHistorialP;
    }

    public void setIdHistorialP(Integer idHistorialP) {
        this.idHistorialP = idHistorialP;
    }
 
    public String getDescripcionCambio() {
        return descripcionCambio;
    }

    public void setDescripcionCambio(String descripcionCambio) {
        this.descripcionCambio = descripcionCambio;
    }

    public String getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public Date getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(Date fechaCambio) {
        this.fechaCambio = fechaCambio;
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
        if (!(object instanceof HistorialPrestamo)) {
            return false;
        }
        HistorialPrestamo other = (HistorialPrestamo) object;
        if ((this.idHistorialP == null && other.idHistorialP != null) || (this.idHistorialP != null && !this.idHistorialP.equals(other.idHistorialP))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "test.HistorialPrestamo[ idHistorialP=" + idHistorialP + " ]";
    }
    
}
