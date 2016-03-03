package fr.grk.ecp.rs;

import com.wordnik.swagger.annotations.*;

import fr.grk.ecp.beans.UserSessionBean;
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
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by grk on 07/12/14.
 */
@Path("/users")
@Api(value = "/users", description = "Microblogging users", position = 2)
public class UserServices {


    private static final Pattern handlePattern = Pattern.compile("^(:)(\\w+)$");
    @Inject
    UserSessionBean userSessionBean;
    @Context
    private UriInfo context;

    @Path("/")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Get All users", notes = "Array of microblogging users with server info")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public JsonObject getAllUsers() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (User user : userSessionBean.getUsers()) {
            arrayBuilder.add(user.toJson());
        }
        builder.add("server", Preferences.SERVER_NAME);
        builder.add("users", arrayBuilder.build());
        return builder.build();
    }


    @Path("/{handle}")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Get a user details from a handle")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "User handle doesn't exists"),
            @ApiResponse(code = 400, message = "User handle not valid"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public JsonObject getSomeUser(@ApiParam(name = "handle", value = "alphanumeric user handle starting by ':'", required = true) @PathParam("handle") String handle) {
        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);

        User u = userSessionBean.getUser(matcher.group(2));
        if (u == null)
            throw new WebApplicationException("handle not found", Response.Status.NOT_FOUND);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("server", Preferences.SERVER_NAME);
        builder.add("user", u.toJson());
        return builder.build();

    }

    @Path("/stats/{handle}")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Get a user stats (followers, followings) from a handle")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "User handle doesn't exists"),
            @ApiResponse(code = 400, message = "User handle not valid"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public JsonObject getSomeUserStats(@ApiParam(name = "handle", value = "alphanumeric user handle starting by ':'", required = true) @PathParam("handle") String handle) {
        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);

        UserStat u = userSessionBean.getUserStats(matcher.group(2));
        if (u == null)
            throw new WebApplicationException("handle not found", Response.Status.NOT_FOUND);




        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("server", Preferences.SERVER_NAME);
        builder.add("user", u.toJson());
        return builder.build();

    }

    @Path("/")
    @POST
    @Consumes("application/json")
    @ApiOperation(value = "Create a User", notes = "Create a user from JSON Object")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User created"),
            @ApiResponse(code = 400, message = "User handle or password not valid or missing"),
            @ApiResponse(code = 406, message = "User handle already exists"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public Response createUser(@ApiParam(required = true, name = "user") User u) {
        if (u.getHandle() == null || u.getHandle().length() <= 3) throw new WebApplicationException("handle is missing", Response.Status.BAD_REQUEST);
        if (u.getPassword() == null || u.getPassword().length() <= 5)
            throw new WebApplicationException("password is missing", Response.Status.BAD_REQUEST);
        if (u.getEmail() == null && u.getEmail().length() <= 6) throw new WebApplicationException("email is missing", Response.Status.BAD_REQUEST);
        if (u.getPicture() == null && u.getPicture().length() <= 10) throw new WebApplicationException("picture is missing", Response.Status.BAD_REQUEST);

        userSessionBean.createUser(u);
        String uri = context.getPath() + "/:" + u.getHandle();
        return Response.created(URI.create(uri)).build();

    }


}
