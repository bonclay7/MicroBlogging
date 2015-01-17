package fr.grk.ecp.models;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import javax.json.Json;
import javax.json.JsonObject;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created by grk on 07/12/14.
 */
@XmlRootElement
public class Tweet extends MongoObject{

    private String tweetid;
    private String message;
    private String userHandle;
    //private User user;
    private String time;


    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    public static Tweet fromDBObject(DBObject doc) {
        Tweet t = new Tweet();
        t.setTweetid(doc.get("_id").toString());
        t.setMessage(doc.get("message").toString());
        t.setTime(doc.get("time").toString());
        t.setUserHandle(doc.get("handle").toString());


        /*
        Map<String, String> userMap = (Map<String, String>)doc.get("user");
        User u = new User();
        u.setEmail(userMap.get("email"));
        u.setHandle(userMap.get("handle"));
        u.setPassword(userMap.get("password"));
        u.setUserid(userMap.get("_id"));
        t.setUser(u);
        //*/
        return t;
    }


    @Override
    public BasicDBObject toDBObject() {
        BasicDBObject doc = new BasicDBObject();
        doc.put("message", message);
        //doc.put("user", user.toDBObject());
        doc.put("handle", userHandle);
        doc.put("time", time);
        return doc;
    }

    @Override
    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("content", getMessage())
                .add("by", userHandle)
                .add("at", getTime()).build();
    }

    public String getTweetid() {
        return tweetid;
    }

    public void setTweetid(String tweetid) {
        this.tweetid = tweetid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
