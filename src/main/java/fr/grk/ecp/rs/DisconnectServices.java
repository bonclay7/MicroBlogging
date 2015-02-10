package fr.grk.ecp.rs;

import com.wordnik.swagger.annotations.*;
import fr.grk.ecp.beans.AuthenticationSessionBean;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by grk on 10/02/15.
 */

@Path("/disconnect")
@Api(value = "/disconnect", description = "API logout")
public class DisconnectServices extends MicrobloggingService implements ApiSecurity {

    @Inject
    AuthenticationSessionBean authenticationSessionBean;

    @Path("/{handle}")
    @POST
    @ApiOperation(value = "API token suppression", notes = "Releases apiToken. [Authenticated method]")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "OK"),
            @ApiResponse(code = 400, message = "User handle is not valid"),
            @ApiResponse(code = 403, message = "User not connected"),
            @ApiResponse(code = 404, message = "User handle doesn't exists"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public Response disconnect(@ApiParam(name = "handle", value = "alphanumeric user handle starting by ':'", required = true) @PathParam("handle") String handle,
                               @ApiParam(name = "api_token", value = "Api token [api_token.host_id]", defaultValue = "api_token.host_id", required = true) @HeaderParam("Authorization") String apiToken) {
        this.parseAPIToken(apiToken);

        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);

        authenticationSessionBean.disconnect(matcher.group(2), this.getToken(), this.getHostID());
        return Response.noContent().build();
    }

    @Override
    public String authorize(String handle, String token, String hostID) throws WebApplicationException {
        //user authentication
        if (!authenticationSessionBean.isAuthenticated(handle, token, hostID))
            throw new WebApplicationException("authentication failed", Response.Status.FORBIDDEN);
        return handle;
    }
}
