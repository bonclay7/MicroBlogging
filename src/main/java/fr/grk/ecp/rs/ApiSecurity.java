package fr.grk.ecp.rs;

import javax.ws.rs.WebApplicationException;

/**
 * Created by grk on 10/02/15.
 */
public interface ApiSecurity {

    public String authorize(String handle, String token, String hostID) throws WebApplicationException;
}
