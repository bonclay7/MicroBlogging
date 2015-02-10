package fr.grk.ecp.rs;

import com.wordnik.swagger.annotations.*;
import fr.grk.ecp.beans.AuthenticationSessionBean;
import fr.grk.ecp.beans.FollowingSessionBean;
import fr.grk.ecp.models.User;
import fr.grk.ecp.models.UserStat;
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
@Path("/followings")
@Api(value = "/followings", description = "User's followings", position = 4)
public class FollowingServices extends MicrobloggingService  implements ApiSecurity{

    @Inject
    FollowingSessionBean followingSessionBean;
    @Inject
    AuthenticationSessionBean authenticationSessionBean;
    @Context
    private UriInfo context;


    @Path("/{handle}")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Get user followings", notes = "Get all followings of some user passed in argument")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "User handle is not valid"),
            @ApiResponse(code = 404, message = "User handle doesn't exists"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public JsonObject getUserFollowings(@ApiParam(name = "handle", value = "alphanumeric user handle starting by ':'", required = true) @PathParam("handle") String handle) {

        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);

        //build json repsonse
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (User user : followingSessionBean.getFollowings(matcher.group(2))) {
            arrayBuilder.add(user.toJson());
        }
        builder.add("server", Preferences.SERVER_NAME);
        builder.add("handle", handle);
        builder.add("users", arrayBuilder.build());

        return builder.build();

    }

    @Path("/{handle}/discover")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Get Stats", notes = "Get all users stats (followings/followees) from a user view passed in argument")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "User handle is not valid"),
            @ApiResponse(code = 404, message = "User handle doesn't exists"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public JsonObject getStats( @ApiParam(name = "handle", value = "alphanumeric user handle starting by ':'", required = true) @PathParam("handle") String handle) {

        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);


        //build json repsonse
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (UserStat stat : followingSessionBean.getUserStats(matcher.group(2))) {
            arrayBuilder.add(stat.toJson());
        }
        builder.add("server", Preferences.SERVER_NAME);
        builder.add("stats", arrayBuilder.build());

        return builder.build();

    }


    @Path("/{handle}/follow/{followeeHandle}")
    @PUT
    @ApiOperation(value = "Follow", notes = "Follow a user identified by his handle. [Authenticated method]")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "OK"),
            @ApiResponse(code = 400, message = "User handle or followee handle is not valid"),
            @ApiResponse(code = 403, message = "Authentication error, or followee already followed"),
            @ApiResponse(code = 404, message = "User handle doesn't exists"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public Response follow(@ApiParam(name = "handle", value = "alphanumeric user handle starting by ':'", required = true) @PathParam("handle") String handle,
                           @ApiParam(name = "followeeHandle", value = "alphanumeric user handle starting by ':'", required = true) @PathParam("followeeHandle") String followeeHandle,
                           @ApiParam(name = "Authorization", value = "Api token [Bearer api_token.host_id]", required = true) @HeaderParam("Authorization") String apiToken)
    {
        //very important to have values in getToken() and getHostID() methods
        this.parseAPIToken(apiToken);

        if (followeeHandle == null)
            throw new WebApplicationException("followeeHandle missing", Response.Status.BAD_REQUEST);
        handle = authorize(handle, this.getToken(), this.getHostID());

        followingSessionBean.follow(handle, followeeHandle);
        return Response.accepted().build();
    }


    @Path("/{handle}/follow/{followeeHandle}")
    @DELETE
    @ApiOperation(value = "Unfollow", notes = "Unfollow a user identified by his handle. [Authenticated method]")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "OK"),
            @ApiResponse(code = 400, message = "User handle or followee handle is not valid (':')"),
            @ApiResponse(code = 403, message = "Authentication error, or followee not followed"),
            @ApiResponse(code = 404, message = "User handle or followee handle doesn't exists"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public Response unfollow(@ApiParam(name = "handle", value = "alphanumeric user handle starting by ':'", required = true) @PathParam("handle") String handle,
                             @ApiParam(name = "followeeHandle", value = "alphanumeric user handle starting by ':'", required = true) @PathParam("followeeHandle") String followeeHandle,
                             @ApiParam(name = "Authorization", value = "Api token [Bearer api_token.host_id]", required = true) @HeaderParam("Authorization") String apiToken) {

        //very important to have values in getToken() and getHostID() methods
        super.parseAPIToken(apiToken);

        if (followeeHandle == null)
            throw new WebApplicationException("followeeHandle missing", Response.Status.BAD_REQUEST);
        handle = authorize(handle, getToken(), getHostID());
        followingSessionBean.unfollow(handle, followeeHandle);
        return Response.accepted().build();
    }


    /**
     * Local method for parameters verification and user authorization
     *
     * @param handle
     * @param token
     * @return well parsed handle
     * @throws WebApplicationException
     */
    @Override
    public String authorize(String handle, String token, String hostID) throws WebApplicationException {
        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);
        if (token == null) throw new WebApplicationException("token missing", Response.Status.FORBIDDEN);
        if (hostID == null) throw new WebApplicationException("host missing", Response.Status.FORBIDDEN);

        handle = matcher.group(2);
        //user authentication
        if (!authenticationSessionBean.isAuthenticated(handle, token, hostID))
            throw new WebApplicationException("authentication failed", Response.Status.FORBIDDEN);
        return handle;
    }


}
