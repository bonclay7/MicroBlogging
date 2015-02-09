package fr.grk.ecp.rs;

import javax.ws.rs.ApplicationPath;
import java.util.Set;

/**
 * Created by grk on 07/12/14.
 */
@ApplicationPath("/api")
public class Application extends javax.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
        resources.add(com.wordnik.swagger.jaxrs.listing.ApiListingResource.class);
        resources.add(com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider.class);
        resources.add(com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON.class);
        resources.add(com.wordnik.swagger.jaxrs.listing.ResourceListingProvider.class);
        //addRestResourceClasses(resources);
        return resources;
    }


}