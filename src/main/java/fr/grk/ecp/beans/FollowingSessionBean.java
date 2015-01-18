package fr.grk.ecp.beans;

import com.mongodb.*;
import fr.grk.ecp.models.Following;
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
public class FollowingSessionBean {


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
            DB db = mongoClient.getDB(Preferences.DB_COLLECTION_NAME);
            dbCollection = db.getCollection("following");
            if (null == dbCollection) {
                db.createCollection("following", null);
            }
        }
    }


    /**
     * Which user(s) "handle" follows ?
     * @param handle
     * @return
     */
    public List<User> getFollowings(String handle){
        BasicDBObject query = new BasicDBObject("follower", handle);
        DBCursor cur = dbCollection.find(query);

        List<User> res = new ArrayList<User>();

        if (null != cur && cur.hasNext()) {
            Following f = Following.fromDBObject(cur.next());
            res.add(userSessionBean.getUser(f.getFolloweeHandle()));
        }
        return res;
    }

    /**
     * Who the fuck is following "handle" ?
     * @param handle
     * @return
     */
    public List<User> getFollowers(String handle){
        BasicDBObject query = new BasicDBObject("followee", handle);
        DBCursor cur = dbCollection.find(query);

        List<User> res = new ArrayList<User>();

        if (null != cur && cur.hasNext()) {
            Following f = Following.fromDBObject(cur.next());
            res.add(userSessionBean.getUser(f.getFollowerHandle()));
        }
        return res;
    }


    private boolean isFollowing(String handle, String followeeHandle){
        BasicDBObject query = new BasicDBObject("followee", followeeHandle)
                .append("follower", handle);
        DBCursor cur = dbCollection.find(query);

        if (null != cur && cur.hasNext()) {
            return true;
        }
        return false;
    }


    public boolean follow(String handle, String followeeHandle) throws WebApplicationException{
        if (isFollowing(handle, followeeHandle)) throw new WebApplicationException("already following the guy", Response.Status.FORBIDDEN);

            Following f = new Following();
            f.setFollowerHandle(handle);
            f.setFolloweeHandle(followeeHandle);
            f.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            dbCollection.insert(f.toDBObject());
            return true;
    }

    public boolean unfollow(String handle, String followingHandle){
        //TODO : Keep history by handling status
        BasicDBObject query = new BasicDBObject("followee", followingHandle);
        query.append("follower", handle);
        DBCursor cur = dbCollection.find(query);
        if (null != cur && cur.hasNext()) {
            dbCollection.remove(cur.next());
            return true;
        }else{
            throw new WebApplicationException("handle does not exists", Response.Status.NOT_FOUND);
        }
    }










}
