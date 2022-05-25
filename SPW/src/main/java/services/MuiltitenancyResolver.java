/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

/**
 * MultiTenantResolver: es una clase abstracta que implementa la 
 * CurrentTenantIdentifierResolverinterfaz. Esta clase tiene como 
 * objetivo proporcionar un método setTenantIdentifier para todas 
 * las CurrentTenantIdentifierResolverimplementaciones.
 * 
 * @author emmanuel
 */
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public abstract class MuiltitenancyResolver implements CurrentTenantIdentifierResolver {

    protected String tenantIdentifier;


    public void setTenantIdentifier(String tenantIdentifier) {
        this.tenantIdentifier = tenantIdentifier;
    }
}