package fr.grk.ecp.models;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fr.grk.ecp.utils.Preferences;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by grk on 07/12/14.
 */
public class User extends MongoObject {

    private String userid;
    private String handle;
    private String password;
    private String picture;
    private String email;

    /**
     * Converts from MongoDB Object to User Object
     * @param doc
     * @return
     */
    public static User fromDBObject(DBObject doc) {
        User user = new User();
        user.setUserid(doc.get("_id") == null ? null : doc.get("_id").toString());
        user.setHandle(doc.get("handle") == null ? null : doc.get("handle").toString());
        user.setPassword(doc.get("password") == null ? null : doc.get("password").toString());
        user.setEmail(doc.get("email") == null ? null : doc.get("email").toString());
        user.setPicture(doc.get("picture") == null ? null : doc.get("picture").toString());
        return user;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public BasicDBObject toDBObject() {
        BasicDBObject doc = new BasicDBObject();
        doc.put("handle", handle);
        doc.put("password", password);
        doc.put("email", email);
        doc.put("server", Preferences.SERVER_NAME);
        doc.put("picture", picture);
        return doc;
    }

    @Override
    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("handle", getHandle())
                .add("email", getEmail())
                .add("picture", getPicture())
                .build();
    }

}
