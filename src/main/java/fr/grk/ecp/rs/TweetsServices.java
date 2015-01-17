package fr.grk.ecp.rs;

import fr.grk.ecp.beans.TweetSessionBean;
import fr.grk.ecp.models.Tweet;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by grk on 07/12/14.
 */
@Path("/tweets")
public class TweetsServices {


    @Inject
    TweetSessionBean tweetSessionBean;

    @Context
    private UriInfo context;

    private static final Pattern handlePattern = Pattern.compile("^(:)(\\w+)$");


    private JsonObject prepareJson(List<Tweet> list){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("server", Preferences.SERVER_NAME);
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Tweet tweet : list){
            arrayBuilder.add(tweet.toJson());
        }
        builder.add("tweets", arrayBuilder.build());
        return builder.build();
    }

    @Path("/")
    @GET
    @Produces("application/json")
    public JsonObject getAllTweets(){
        return prepareJson(tweetSessionBean.getTweets());
    }


    @Path("/{handle}")
    @GET
    @Produces("application/json")
    public JsonObject getUserTweets(@PathParam("handle") String handle){
        Matcher matcher = handlePattern.matcher(handle);
        if (matcher.matches()) {
            return prepareJson(tweetSessionBean.getUserTweets(matcher.group(2)));
        } else {
            throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);
        }
    }


    @Path("/{handle}")
    @POST
    @Consumes("text/plain")
    public Response createTweet(@PathParam("handle") String handle, String message){
        System.out.println("HANDLE " +handle+ " | MESSAGE " +message);
        if (handle != null && handle != null){

            Matcher matcher = handlePattern.matcher(handle);

            if (matcher.matches()) {
                tweetSessionBean.createTweet(matcher.group(2), message);
                String uri = context.getPath() + "/:" + matcher.group(2);
                return Response.created(URI.create(uri)).build();
            }else {
                throw new WebApplicationException("handle not valid", Response.Status.BAD_REQUEST);
            }
        }else{
            throw new WebApplicationException("handle and/or message missing", Response.Status.BAD_REQUEST);
        }
    }


}
