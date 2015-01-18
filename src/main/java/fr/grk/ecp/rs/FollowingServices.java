package fr.grk.ecp.rs;

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
@Path("/")
public class FollowingServices {

    private static final Pattern handlePattern = Pattern.compile("^(:)(\\w+)$");
    @Inject
    FollowingSessionBean followingSessionBean;
    @Inject
    AuthenticationSessionBean authenticationSessionBean;
    @Context
    private UriInfo context;


    @Path("/{handle}/followings")
    @GET
    @Produces("application/json")
    public JsonObject getUserFollowings(@PathParam("handle") String handle) {

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
        builder.add("follows", arrayBuilder.build());

        return builder.build();

    }

    @Path("/{handle}/followers")
    @GET
    @Produces("application/json")
    public JsonObject getUserFollowers(@PathParam("handle") String handle) {

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
        builder.add("followedBy", arrayBuilder.build());

        return builder.build();

    }


    @Path("/{handle}/followings")
    @POST
    @Consumes("text/plain")
    public Response follow(@PathParam("handle") String handle, @HeaderParam("token") String token, String followeeHandle) {

        if (followeeHandle == null)
            throw new WebApplicationException("followeeHandle missing", Response.Status.BAD_REQUEST);
        handle = authorize(handle, token);

        followingSessionBean.follow(handle, followeeHandle);
        return Response.accepted().build();
    }


    @Path("/{handle}/followings")
    @DELETE
    @Consumes("text/plain")
    public Response unfollow(@PathParam("handle") String handle, @HeaderParam("token") String token, String followeeHandle) {

        if (followeeHandle == null)
            throw new WebApplicationException("followeeHandle missing", Response.Status.BAD_REQUEST);
        handle = authorize(handle, token);
        followingSessionBean.unfollow(handle, followeeHandle);
        return Response.noContent().build();
    }


    /**
     * Local method for parameters verification and user authorization
     *
     * @param handle
     * @param token
     * @return well parsed handle
     * @throws WebApplicationException
     */
    private String authorize(String handle, String token) throws WebApplicationException {
        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);
        if (token == null) throw new WebApplicationException("token missing", Response.Status.FORBIDDEN);

        handle = matcher.group(2);
        //user authentication
        if (!authenticationSessionBean.isAuthenticated(handle, token))
            throw new WebApplicationException("authentication failed", Response.Status.FORBIDDEN);
        return handle;
    }


}
