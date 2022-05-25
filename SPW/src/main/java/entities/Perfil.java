/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author emmanuel
 */
@Entity
@Table(name = "perfil")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Perfil.findAll", query = "SELECT p FROM Perfil p"),
    @NamedQuery(name = "Perfil.findByIdPerfil", query = "SELECT p FROM Perfil p WHERE p.idPerfil = :idPerfil")})
public class Perfil implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_perfil")
    private Integer idPerfil;
    @JoinColumn(name = "id_permiso", referencedColumnName = "id_permiso")
    @ManyToOne(optional = false)
    private Permiso idPermiso;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @ManyToOne(optional = false)
    private Usuario idUsuario;

    public Perfil() {
    }

    public Perfil(Integer idPerfil) {
        this.idPerfil = idPerfil;
    }

    public Integer getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(Integer idPerfil) {
        this.idPerfil = idPerfil;
    }

    public Permiso getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(Permiso idPermiso) {
        this.idPermiso = idPermiso;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPerfil != null ? idPerfil.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Perfil)) {
            return false;
        }
        Perfil other = (Perfil) object;
        if ((this.idPerfil == null && other.idPerfil != null) || (this.idPerfil != null && !this.idPerfil.equals(other.idPerfil))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Perfil[ idPerfil=" + idPerfil + " ]";
    }
    
}
