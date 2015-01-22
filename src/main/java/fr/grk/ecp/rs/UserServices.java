package fr.grk.ecp.rs;

import fr.grk.ecp.beans.UserSessionBean;
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
@Path("/users")
public class UserServices {


    private static final Pattern handlePattern = Pattern.compile("^(:)(\\w+)$");
    @Inject
    UserSessionBean userSessionBean;
    @Context
    private UriInfo context;

    @Path("/")
    @GET
    @Produces("application/json")
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
    public JsonObject getSomeUser(@PathParam("handle") String handle) {
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

    @Path("/")
    @POST
    @Consumes("application/json")
    public Response createUser(User u) {
        if (u.getHandle() == null) throw new WebApplicationException("handle is missing", Response.Status.BAD_REQUEST);
        if (u.getPassword() == null)
            throw new WebApplicationException("password is missing", Response.Status.BAD_REQUEST);
        userSessionBean.createUser(u);
        String uri = context.getPath() + "/:" + u.getHandle();
        return Response.created(URI.create(uri)).build();

    }


}
