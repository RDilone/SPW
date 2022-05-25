/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
 
/**
 *
 * @author emmanuel
 */
@Entity
@Table(name = "prestamo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Prestamo.findAll", query = "SELECT p FROM Prestamo p"),
    @NamedQuery(name = "Prestamo.findByIdPrestamo", query = "SELECT p FROM Prestamo p WHERE p.idPrestamo = :idPrestamo"),
    @NamedQuery(name = "Prestamo.findByCantCuotas", query = "SELECT p FROM Prestamo p WHERE p.cantCuotas = :cantCuotas"),
    @NamedQuery(name = "Prestamo.findByCapitalPrestado", query = "SELECT p FROM Prestamo p WHERE p.capitalPrestado = :capitalPrestado"),
    @NamedQuery(name = "Prestamo.findByBalanceCapital", query = "SELECT p FROM Prestamo p WHERE p.balanceCapital = :balanceCapital"),
    @NamedQuery(name = "Prestamo.findByConcepto", query = "SELECT p FROM Prestamo p WHERE p.concepto = :concepto"),
    @NamedQuery(name = "Prestamo.findByEstado", query = "SELECT p FROM Prestamo p WHERE p.estado = :estado"),
    @NamedQuery(name = "Prestamo.findByFechaFinal", query = "SELECT p FROM Prestamo p WHERE p.fechaFinal = :fechaFinal"),
    @NamedQuery(name = "Prestamo.findByFechaInicio", query = "SELECT p FROM Prestamo p WHERE p.fechaInicio = :fechaInicio"),
    @NamedQuery(name = "Prestamo.findByGanancia", query = "SELECT p FROM Prestamo p WHERE p.ganancia = :ganancia"),
    @NamedQuery(name = "Prestamo.findByPorcentajeMora", query = "SELECT p FROM Prestamo p WHERE p.porcentajeMora = :porcentajeMora"),
    @NamedQuery(name = "Prestamo.findByTasa", query = "SELECT p FROM Prestamo p WHERE p.tasa = :tasa"),
    @NamedQuery(name = "Prestamo.findByTipoInteres", query = "SELECT p FROM Prestamo p WHERE p.tipoInteres = :tipoInteres"),
    @NamedQuery(name = "Prestamo.findByPlazo", query = "SELECT p FROM Prestamo p WHERE p.plazo = :plazo")})
public class Prestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_prestamo")
    private Integer idPrestamo;
    @Column(name = "cant_cuotas")
    private Integer cantCuotas;
    @Column(name = "capital_prestado")
    private double capitalPrestado;
    @Column(name = "balance_capital")
    private double balanceCapital;
    @Size(max = 2147483647)
    @Column(name = "concepto")
    private String concepto;
    @Column(name = "estado")
    private String estado;
    @Column(name = "fecha_final")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFinal;
    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;
    @Column(name = "ganancia")
    private double ganancia;
    @Column(name = "porcentaje_mora")
    private double porcentajeMora;
    @Column(name = "tasa")
    private double tasa;
    @Size(max = 2147483647)
    @Column(name = "tipo_interes")
    private String tipoInteres;
    @Column(name = "plazo")
    private Integer plazo;
    @Size(max = 2147483647)
    @Column(name = "desc_garantia")
    private String descGarantia;
    @OneToMany(mappedBy = "idPrestamo")
    private Collection<HistorialPago> historialPagoCollection;
    @JoinColumn(name = "id_intervalo_interes", referencedColumnName = "id_intervalo")
    @ManyToOne
    private Intervalo idIntervaloInteres;
    @JoinColumn(name = "id_intervalo_pago", referencedColumnName = "id_intervalo")
    @ManyToOne
    private Intervalo idIntervaloPago;
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona")
    @ManyToOne
    private Persona idPersona;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idPrestamo")
    private Collection<HistorialPrestamo> historialPrestamoCollection;
    @OneToMany(mappedBy = "idPrestamo")
    private Collection<Amortizacion> amortizacionCollection;
    
    //cascade = CascadeType.ALL   >   orphanRemoval = true
    
    public Prestamo() {
    }

    public Prestamo(Integer idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public Integer getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(Integer idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public Integer getCantCuotas() {
        return cantCuotas;
    }

    public void setCantCuotas(Integer cantCuotas) {
        this.cantCuotas = cantCuotas;
    }

    public double getCapitalPrestado() {
        return capitalPrestado;
    }

    public void setCapitalPrestado(double capitalPrestado) {
        this.capitalPrestado = capitalPrestado;
    }

    public double getBalanceCapital() {
        return balanceCapital;
    }

    public void setBalanceCapital(double balanceCapital) {
        this.balanceCapital = balanceCapital;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public double getGanancia() {
        return ganancia;
    }

    public void setGanancia(double ganancia) {
        this.ganancia = ganancia;
    }

    public double getPorcentajeMora() {
        return porcentajeMora;
    }

    public void setPorcentajeMora(double porcentajeMora) {
        this.porcentajeMora = porcentajeMora;
    }

    public double getTasa() {
        return tasa;
    }

    public void setTasa(double tasa) {
        this.tasa = tasa;
    }

    public String getTipoInteres() {
        return tipoInteres;
    }

    public void setTipoInteres(String tipoInteres) {
        this.tipoInteres = tipoInteres;
    }

    public Integer getPlazo() {
        return plazo;
    }

    public void setPlazo(Integer plazo) {
        this.plazo = plazo;
    }
    
     public String getDescGarantia() {
        return descGarantia;
    }

    public void setDescGarantia(String descGarantia) {
        this.descGarantia = descGarantia;
    }

    @XmlTransient
    public Collection<HistorialPago> getHistorialPagoCollection() {
        return historialPagoCollection;
    }

    public void setHistorialPagoCollection(Collection<HistorialPago> historialPagoCollection) {
        this.historialPagoCollection = historialPagoCollection;
    }

    public Intervalo getIdIntervaloInteres() {
        return idIntervaloInteres;
    }

    public void setIdIntervaloInteres(Intervalo idIntervaloInteres) {
        this.idIntervaloInteres = idIntervaloInteres;
    }

    public Intervalo getIdIntervaloPago() {
        return idIntervaloPago;
    }

    public void setIdIntervaloPago(Intervalo idIntervaloPago) {
        this.idIntervaloPago = idIntervaloPago;
    }

    public Persona getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Persona idPersona) {
        this.idPersona = idPersona;
    }

    @XmlTransient
    public Collection<HistorialPrestamo> getHistorialPrestamoCollection() {
        return historialPrestamoCollection;
    }

    public void setHistorialPrestamoCollection(Collection<HistorialPrestamo> historialPrestamoCollection) {
        this.historialPrestamoCollection = historialPrestamoCollection;
    }

    @XmlTransient
    public Collection<Amortizacion> getAmortizacionCollection() {
        return amortizacionCollection;
    }

    public void setAmortizacionCollection(Collection<Amortizacion> amortizacionCollection) {
        this.amortizacionCollection = amortizacionCollection;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPrestamo != null ? idPrestamo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Prestamo)) {
            return false;
        }
        Prestamo other = (Prestamo) object;
        if ((this.idPrestamo == null && other.idPrestamo != null) || (this.idPrestamo != null && !this.idPrestamo.equals(other.idPrestamo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "test.Prestamo[ idPrestamo=" + idPrestamo + " ]";
    }
    
}
