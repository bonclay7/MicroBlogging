package fr.grk.ecp.beans;

import com.mongodb.*;
import fr.grk.ecp.models.User;
import fr.grk.ecp.utils.Preferences;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by grk on 07/12/14.
 */
@Stateless
public class UserSessionBean {

    private DBCollection dbCollection;
    //User user;

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
            dbCollection = db.getCollection("users");
            if (null == dbCollection) {
                db.createCollection("users", null);
            }
        }
    }


    /**
     * Create an user
     *
     * @param u User to be created
     * @return true if creation success, otherwise, false
     * @throws WebApplicationException
     */
    public void createUser(User u) throws WebApplicationException {
        if (getUser(u.getHandle()) != null)
            throw new WebApplicationException("User already exists", Response.Status.NOT_ACCEPTABLE);
        if (u.getPassword() == null) throw new WebApplicationException("Password missing", Response.Status.BAD_REQUEST);

        u.setPassword(hash(u.getPassword()));
        dbCollection.insert(u.toDBObject());

    }

    private String hash(String string){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(string.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            return string;
        }

    }


    /**
     * Search on the db for a user using his handle
     *
     * @param handle
     * @return The User
     */
    public User getUser(String handle) {
        BasicDBObject query = new BasicDBObject("handle", handle);
        DBCursor cur = dbCollection.find(query);
        if (null != cur && cur.hasNext()) {
            return User.fromDBObject(cur.next());
        }
        return null;
    }


    /**
     * Return the user list in a wrapper object
     *
     * @return
     */
    public List<User> getUsers() {
        List<User> users = new ArrayList<User>();
        DBCursor cur = dbCollection.find();
        for (DBObject dbo : cur.toArray()) {
            users.add(User.fromDBObject(dbo));
        }
        return users;
    }

}
