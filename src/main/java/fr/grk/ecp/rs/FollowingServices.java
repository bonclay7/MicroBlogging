package fr.grk.ecp.rs;

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
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by grk on 07/12/14.
 */
@Path("/")
public class FollowingServices {

    @Inject
    FollowingSessionBean followingSessionBean;


    @Context
    private UriInfo context;

    private static final Pattern handlePattern = Pattern.compile("^(:)(\\w+)$");

    @Path("/{handle}/followings")
    @GET
    @Produces("application/json")
    public JsonObject getUserFollowings(@PathParam("handle") String handle){
        Matcher matcher = handlePattern.matcher(handle);
        if (matcher.matches()) {

            JsonObjectBuilder builder = Json.createObjectBuilder();
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (User user : followingSessionBean.getFollowings(matcher.group(2))){
                arrayBuilder.add(user.toJson());
            }
            builder.add("server", Preferences.SERVER_NAME);
            builder.add("handle", handle);
            builder.add("follows", arrayBuilder.build());
            return builder.build();
        } else {
            throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);
        }
    }

    @Path("/{handle}/followers")
    @GET
    @Produces("application/json")
    public JsonObject getUserFollowers(@PathParam("handle") String handle){
        Matcher matcher = handlePattern.matcher(handle);
        if (matcher.matches()) {

            JsonObjectBuilder builder = Json.createObjectBuilder();
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (User user : followingSessionBean.getFollowers(matcher.group(2))){
                arrayBuilder.add(user.toJson());
            }
            builder.add("server", Preferences.SERVER_NAME);
            builder.add("handle", handle);
            builder.add("followedBy", arrayBuilder.build());
            return builder.build();

        } else {
            throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);
        }
    }


    @Path("/{handle}/followings")
    @POST
    @Consumes("text/plain")
    public Response follow(@PathParam("handle") String handle, String message){
        //TODO: Extract handle from message as json
        if (handle != null && handle != null){
            Matcher matcher = handlePattern.matcher(handle);
            if (matcher.matches()) {
                followingSessionBean.follow(matcher.group(2), message);
                String uri = context.getPath() + "/:" + matcher.group(2) + "/followings";
                return Response.created(URI.create(uri)).build();
            }else {
                throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);
            }
        }else{
            throw new WebApplicationException("handle and/or message missing", Response.Status.BAD_REQUEST);
        }
    }


    @Path("/{handle}/followings")
    @POST
    @Consumes("text/plain")
    public Response unfollow(@PathParam("handle") String handle, String message){
        //TODO: Extract handle from message as json
        if (handle != null && handle != null){
            Matcher matcher = handlePattern.matcher(handle);
            if (matcher.matches()) {
                followingSessionBean.unfollow(matcher.group(2), message);
                return Response.accepted().build();
            }else {
                throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);
            }
        }else{
            throw new WebApplicationException("handle and/or message missing", Response.Status.BAD_REQUEST);
        }
    }





}
