/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author emmanuel
 */
@javax.persistence.Entity
@Table(name = "intervalo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Intervalo.findAll", query = "SELECT i FROM Intervalo i"),
    @NamedQuery(name = "Intervalo.findByIdIntervalo", query = "SELECT i FROM Intervalo i WHERE i.idIntervalo = :idIntervalo"),
    @NamedQuery(name = "Intervalo.findByIntervalo", query = "SELECT i FROM Intervalo i WHERE i.intervalo = :intervalo"),
    @NamedQuery(name = "Intervalo.findByCantDias", query = "SELECT i FROM Intervalo i WHERE i.cantDias = :cantDias"),
    @NamedQuery(name = "Intervalo.findByPlural", query = "SELECT i FROM Intervalo i WHERE i.plural = :plural")})
public class Intervalo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_intervalo")
    private Integer idIntervalo;
    @Size(max = 2147483647)
    @Column(name = "intervalo")
    private String intervalo;
    @Column(name = "cant_dias")
    private Integer cantDias;
    @Size(max = 2147483647)
    @Column(name = "plural")
    private String plural;
    @OneToMany(mappedBy = "idIntervaloInteres")
    private Collection<Prestamo> prestamoCollection;
    @OneToMany(mappedBy = "idIntervaloPago")
    private Collection<Prestamo> prestamoCollection1;

    public Intervalo() {
    }

    public Intervalo(Integer idIntervalo) {
        this.idIntervalo = idIntervalo;
    }

    public Integer getIdIntervalo() {
        return idIntervalo;
    }

    public void setIdIntervalo(Integer idIntervalo) {
        this.idIntervalo = idIntervalo;
    }

    public String getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(String intervalo) {
        this.intervalo = intervalo;
    }

    public Integer getCantDias() {
        return cantDias;
    }

    public void setCantDias(Integer cantDias) {
        this.cantDias = cantDias;
    }

    public String getPlural() {
        return plural;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    @XmlTransient
    public Collection<Prestamo> getPrestamoCollection() {
        return prestamoCollection;
    }

    public void setPrestamoCollection(Collection<Prestamo> prestamoCollection) {
        this.prestamoCollection = prestamoCollection;
    }

    @XmlTransient
    public Collection<Prestamo> getPrestamoCollection1() {
        return prestamoCollection1;
    }

    public void setPrestamoCollection1(Collection<Prestamo> prestamoCollection1) {
        this.prestamoCollection1 = prestamoCollection1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idIntervalo != null ? idIntervalo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Intervalo)) {
            return false;
        }
        Intervalo other = (Intervalo) object;
        if ((this.idIntervalo == null && other.idIntervalo != null) || (this.idIntervalo != null && !this.idIntervalo.equals(other.idIntervalo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "test.Intervalo[ idIntervalo=" + idIntervalo + " ]";
    }
    
}
