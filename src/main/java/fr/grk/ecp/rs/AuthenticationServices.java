package fr.grk.ecp.rs;

import com.wordnik.swagger.annotations.*;
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
@Path("/authenticate")
@Api(value = "/authenticate", description = "API login", position = 1)
public class AuthenticationServices extends MicrobloggingService{

    @Inject
    AuthenticationSessionBean authenticationSessionBean;

    @Path("/{handle}")
    @POST
    @Produces("application/json")
    @ApiOperation(value = "get API token to access methods requiring authorization")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "User handle/host id not valid"),
            @ApiResponse(code = 403, message = "Invalid Password"),
            @ApiResponse(code = 404, message = "User handle doesn't exists"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public JsonObject authenticate(@ApiParam(name = "handle", value = "alphanumeric user handle starting by ':'", required = true) @PathParam("handle") String handle,
                                    @HeaderParam("password") String password, @HeaderParam("hostID") String hostID) {

        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);
        if (password == null) throw new WebApplicationException("password not valid", Response.Status.BAD_REQUEST);
        if (hostID == null) throw new WebApplicationException("host not valid", Response.Status.BAD_REQUEST);

        Session s = authenticationSessionBean.authenticate(matcher.group(2), password, hostID);
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("server", Preferences.SERVER_NAME);
        builder.add("session", s.toJson());


        return builder.build();
    }
}
