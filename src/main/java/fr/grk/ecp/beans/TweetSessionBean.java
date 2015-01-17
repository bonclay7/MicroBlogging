package fr.grk.ecp.beans;

import com.mongodb.*;
import fr.grk.ecp.models.Tweet;
import fr.grk.ecp.models.User;
import fr.grk.ecp.utils.Preferences;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by grk on 07/12/14.
 */
@Stateless
public class TweetSessionBean {

    @Inject
    UserSessionBean userSessionBean;

    DBCollection dbCollection;


    /**
     * Create connection to mongoDb and select the users collection on EJB creation
     */
    @PostConstruct
    public void init() {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(Preferences.MONGO_DB_ADDR);
        } catch (UnknownHostException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Connection to database failed", e);
        }

        if (mongoClient != null) {
            DB db = mongoClient.getDB("microblogging");
            dbCollection = db.getCollection("tweets");
            if (null == dbCollection) {
                db.createCollection("tweets", null);
            }
        }
    }

    /**
     * Get all the tweets by all the users
     *
     * @return
     */
    public List<Tweet> getTweets() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        DBCursor cur = dbCollection.find();
        for (DBObject dbo : cur.toArray()) {
            tweets.add(new Tweet().fromDBObject(dbo));
        }
        return tweets;
    }


    /**
     * @param handle
     * @return
     * @throws WebApplicationException
     */
    public List<Tweet> getUserTweets(String handle) throws WebApplicationException {
        List<Tweet> tweets = new ArrayList<Tweet>();

        BasicDBObject query = new BasicDBObject("handle", handle);
        DBCursor cur = dbCollection.find(query);

        for (DBObject dbo : cur.toArray()) {
            tweets.add(new Tweet().fromDBObject(dbo));
        }

        return tweets;
    }

    /**
     * @param handle
     * @param message
     * @return
     */
    public boolean createTweet(String handle, String message) throws WebApplicationException {
        User u = userSessionBean.getUser(handle);
        if (null != u) {
            Tweet t = new Tweet();
            t.setMessage(message);
            t.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            //t.setUserid(u.getUserid());
            t.setUserHandle(handle);
            dbCollection.insert(t.toDBObject());
            return true;
        } else {
            throw new WebApplicationException("handle does not exists", Response.Status.NOT_FOUND);
        }
    }

}
