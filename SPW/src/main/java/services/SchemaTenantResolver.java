/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

/**
 *
 * @author emmanuel
 */
import entities.Usuario;
import java.util.HashMap;
import java.util.Map;


public class SchemaTenantResolver extends MuiltitenancyResolver {

    public static Map<String, String> userDatasourceMap;
    
    public SchemaTenantResolver(){
        userDatasourceMap = new HashMap();
        //Usuario y esquema por default
        userDatasourceMap.put("jodilone", "public");
    }
    

    @Override
    public String resolveCurrentTenantIdentifier() {

        //System.out.println("Identificador schema: "+this.tenantIdentifier);

        if(this.tenantIdentifier != null
                && userDatasourceMap.containsKey(this.tenantIdentifier)){
            return userDatasourceMap.get(this.tenantIdentifier);
        }else {
            return userDatasourceMap.get("jodilone");
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

}
