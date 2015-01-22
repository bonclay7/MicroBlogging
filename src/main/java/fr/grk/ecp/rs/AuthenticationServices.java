package fr.grk.ecp.rs;

import fr.grk.ecp.beans.AuthenticationSessionBean;
import fr.grk.ecp.models.Session;
import fr.grk.ecp.utils.Preferences;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by grk on 17/01/15.
 */
@Path("/")
public class AuthenticationServices {

    private static final Pattern handlePattern = Pattern.compile("^(:)(\\w+)$");
    @Inject
    AuthenticationSessionBean authenticationSessionBean;

    @Path("/{handle}/authenticate")
    @POST
    @Produces("application/json")
    public JsonObject authenticate(@PathParam("handle") String handle,
                                   @HeaderParam("password") String password, @HeaderParam("hostID") String hostID) {

        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);
        if (password == null) throw new WebApplicationException("password not valid", Response.Status.BAD_REQUEST);
        if (hostID == null) throw new WebApplicationException("host not valid", Response.Status.BAD_REQUEST);

        Session s = authenticationSessionBean.authenticate(matcher.group(2), password, hostID);
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        arrayBuilder.add(s.toJson());


        builder.add("server", Preferences.SERVER_NAME);
        builder.add("session", s.toJson());

        return builder.build();
    }


    @Path("/{handle}/disconnect")
    @POST
    public Response disconnect(@PathParam("handle") String handle, @HeaderParam("token") String token, @HeaderParam("hostID") String hostID) {

        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);

        authenticationSessionBean.disconnect(matcher.group(2), token, hostID);
        return Response.noContent().build();
    }


}
