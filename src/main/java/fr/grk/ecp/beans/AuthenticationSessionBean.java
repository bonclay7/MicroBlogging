package fr.grk.ecp.beans;

import com.mongodb.*;
import fr.grk.ecp.models.Following;
import fr.grk.ecp.models.Session;
import fr.grk.ecp.models.User;
import fr.grk.ecp.utils.Preferences;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by grk on 17/01/15.
 */
@Stateless
public class AuthenticationSessionBean {

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
            DB db = mongoClient.getDB(Preferences.COLLECTION_NAME);
            dbCollection = db.getCollection("sessions");
            if (null == dbCollection) {
                db.createCollection("sessions", null);
            }
        }
    }




    public Session authenticate(String handle, String password) throws WebApplicationException{
        User u = userSessionBean.getUser(handle);

        //Authorizing user
        if (u == null) throw new WebApplicationException("handle does not exists", Response.Status.NOT_FOUND);
        if (password ==null) throw new WebApplicationException("Password is empty", Response.Status.BAD_REQUEST);
        if (!u.getPassword().equalsIgnoreCase(hash(password))) throw new WebApplicationException("Wrong password", Response.Status.FORBIDDEN);

        //Session creation
        Session s = new Session();
        s.setCreationDate(new SimpleDateFormat(Preferences.DATE_FORMAT).format(new Date()));
        s.setHandle(handle);
        s.setStatus(Preferences.SESSION_ACTIVE);
        s.setToken(hash(System.currentTimeMillis()+""));

        //Persist in DB
        dbCollection.insert(s.toDBObject());
        return s;
    }





    public boolean isAuthenticated(String handle, String token) throws WebApplicationException{
        if (handle == null) throw new WebApplicationException("handle missing", Response.Status.NOT_FOUND);
        if (token == null) throw new WebApplicationException("token missing", Response.Status.NOT_FOUND);
        //Session s = getActiveSession(handle);
        if (getActiveSession(handle, token) == null) return false;
        return true;
    }


    public boolean disconnect(String handle, String token) throws WebApplicationException{
        if (handle == null) throw new WebApplicationException("handle does not exists", Response.Status.NOT_FOUND);
        if (getActiveSession(handle, token) == null) throw new WebApplicationException(handle + " not connected", Response.Status.BAD_REQUEST);

        BasicDBObject searchQuery = new BasicDBObject("handle", handle).append("token",token);

        BasicDBObject newDocument = new BasicDBObject();
        newDocument.append("$set", new BasicDBObject().append("status", Preferences.SESSION_INACTIVE).append("stopped", new SimpleDateFormat(Preferences.DATE_FORMAT).format(new Date())));
        dbCollection.update(searchQuery, newDocument);
        return true;
    }

    // ----

    private String hash(String string){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(string.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            return string;
        }

    }


    private Session getActiveSession(String handle, String token) {
        BasicDBObject query = new BasicDBObject("handle", handle);
        query.append("token", token);
        query.append("status", Preferences.SESSION_ACTIVE);

        DBCursor cur = dbCollection.find(query);

        if (null != cur && cur.hasNext()) {
            Session s = Session.fromDBObject(cur.next());
            return s;
        }
        return null;
    }

}
