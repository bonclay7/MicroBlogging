package fr.grk.ecp.rs;

import com.wordnik.swagger.annotations.*;
import fr.grk.ecp.beans.AuthenticationSessionBean;
import fr.grk.ecp.beans.FollowingSessionBean;
import fr.grk.ecp.models.User;
import fr.grk.ecp.utils.Preferences;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by grk on 07/12/14.
 */
@Path("/followers")
@Api(value = "/followers", description = "User's followers", position = 5)
public class FollowersServices extends MicrobloggingService {

    @Inject
    FollowingSessionBean followingSessionBean;
    @Context
    private UriInfo context;


    
    @Path("/{handle}")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Get user followers", notes = "Get all followers of some user passed in argument")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "User handle is not valid"),
            @ApiResponse(code = 404, message = "User handle doesn't exists"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public JsonObject getUserFollowers(@ApiParam(name = "handle", value = "alphanumeric user handle starting by ':'", required = true) @PathParam("handle") String handle) {

        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);

        //build json repsonse
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (User user : followingSessionBean.getFollowers(matcher.group(2))) {
            arrayBuilder.add(user.toJson());
        }
        builder.add("server", Preferences.SERVER_NAME);
        builder.add("handle", handle);
        builder.add("users", arrayBuilder.build());

        return builder.build();

    }


}
