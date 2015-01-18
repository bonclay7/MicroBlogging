package fr.grk.ecp.rs;

import fr.grk.ecp.beans.AuthenticationSessionBean;
import fr.grk.ecp.beans.TweetSessionBean;
import fr.grk.ecp.models.Tweet;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by grk on 07/12/14.
 */
@Path("/")
public class TweetsServices {


    private static final Pattern handlePattern = Pattern.compile("^(:)(\\w+)$");
    @Inject
    TweetSessionBean tweetSessionBean;
    @Inject
    AuthenticationSessionBean authenticationSessionBean;
    @Context
    private UriInfo context;

    private JsonObject prepareJson(List<Tweet> list) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("server", Preferences.SERVER_NAME);
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Tweet tweet : list) {
            arrayBuilder.add(tweet.toJson());
        }
        builder.add("tweets", arrayBuilder.build());
        return builder.build();
    }

    @Path("/tweets")
    @GET
    @Produces("application/json")
    public JsonObject getAllTweets() {
        return prepareJson(tweetSessionBean.getTweets());
    }


    @Path("/{handle}/tweets")
    @GET
    @Produces("application/json")
    public JsonObject getUserTweets(@PathParam("handle") String handle) {
        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);
        return prepareJson(tweetSessionBean.getUserTweets(matcher.group(2)));
    }

    /**
     * @param handle
     * @param token
     * @param message
     * @return
     */
    @Path("/{handle}/tweet")
    @POST
    @Consumes("text/plain")
    public Response createTweet(@PathParam("handle") String handle, @HeaderParam("token") String token, String message) {
        if (message == null)
            throw new WebApplicationException("handle and/or message missing", Response.Status.BAD_REQUEST);
        handle = authorize(handle, token);

        //proceed
        tweetSessionBean.createTweet(handle, message);
        String uri = context.getPath() + "s";
        return Response.created(URI.create(uri.replace(":", ""))).build();
    }


    @Path("/{handle}/reading_list")
    @GET
    @Produces("application/json")
    public JsonObject getReadingList(@PathParam("handle") String handle, @HeaderParam("token") String token) {
        handle = authorize(handle, token);
        return prepareJson(tweetSessionBean.getReadingList(handle));
    }


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
