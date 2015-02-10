package fr.grk.ecp.rs;

import com.wordnik.swagger.annotations.*;
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

/**
 * Created by grk on 07/12/14.
 */
@Path("/tweets")
@Api(value = "/tweets", description = "All about tweets")
public class TweetsServices extends MicrobloggingService implements ApiSecurity {


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

    @Path("/")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Get all tweets", notes = "Get all users tweets !")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public JsonObject getAllTweets() {
        return prepareJson(tweetSessionBean.getTweets());
    }


    @Path("/{handle}")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Get user tweets", notes = "Get all tweets of some user passed in argument")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "User handle is not valid"),
            @ApiResponse(code = 404, message = "User handle doesn't exists"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public JsonObject getUserTweets(@PathParam("handle") String handle) {
        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);
        return prepareJson(tweetSessionBean.getUserTweets(matcher.group(2)));
    }

    /**
     * @param handle
     * @param apiToken
     * @param message
     * @return
     */
    @Path("/{handle}")
    @POST
    @Consumes("text/plain")
    @ApiOperation(value = "Create tweet", notes = "Create a tweet")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "User handle is not valid"),
            @ApiResponse(code = 404, message = "User handle doesn't exists"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public Response createTweet(
            @ApiParam(name = "handle", value = "alphanumeric user handle starting by ':'", required = true) @PathParam("handle") String handle,
            @ApiParam(name = "api_token", value = "Api token [Bearer api_token.host_id]", required = true) @HeaderParam("Authorization") String apiToken,
            String message) {

        super.parseAPIToken(apiToken);

        if (message == null)
            throw new WebApplicationException("handle and/or message missing", Response.Status.BAD_REQUEST);

        handle = authorize(handle, this.getToken(), this.getHostID());

        //proceed
        tweetSessionBean.createTweet(handle, message);
        String uri = context.getPath() + "s";
        return Response.created(URI.create(uri.replace(":", ""))).build();
    }


    @Path("/{handle}/reading_list")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Get Reading list", notes = "Get all users and followings tweets (twitter timeline) ordered by date")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "User handle is not valid"),
            @ApiResponse(code = 404, message = "User handle doesn't exists"),
            @ApiResponse(code = 500, message = "Something wrong in Server")}
    )
    public JsonObject getReadingList(@ApiParam(name = "handle", value = "alphanumeric user handle starting by ':'", required = true) @PathParam("handle") String handle,
                                     @ApiParam(name = "api_token", value = "Api token [Bearer api_token.host_id]", required = true) @HeaderParam("Authorization") String apiToken) {
        super.parseAPIToken(apiToken);

        handle = authorize(handle, getToken(), getHostID());
        return prepareJson(tweetSessionBean.getReadingList(handle));
    }


    @Override
    public String authorize(String handle, String token, String hostID) throws WebApplicationException {
        Matcher matcher = handlePattern.matcher(handle);
        if (!matcher.matches()) throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);
        if (token == null) throw new WebApplicationException("token missing", Response.Status.FORBIDDEN);

        handle = matcher.group(2);
        //user authentication
        if (!authenticationSessionBean.isAuthenticated(handle, token, hostID))
            throw new WebApplicationException("authentication failed", Response.Status.FORBIDDEN);
        return handle;
    }


}
