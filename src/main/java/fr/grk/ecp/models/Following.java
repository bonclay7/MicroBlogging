package fr.grk.ecp.models;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by grk on 15/01/15.
 */
public class Following extends MongoObject{
    private String id;
    private String followeeHandle;
    private String followerHandle;
    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFollowerHandle(String followerHandle) {
        this.followerHandle = followerHandle;
    }

    public String getFolloweeHandle() {
        return followeeHandle;
    }

    public void setFolloweeHandle(String followeeHandle) {
        this.followeeHandle = followeeHandle;
    }

    public String getFollowerHandle() {
        return followerHandle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public BasicDBObject toDBObject() {
        BasicDBObject doc = new BasicDBObject();
        doc.put("followee", followeeHandle);
        doc.put("follower", followerHandle);
        doc.put("time", time);
        return doc;
    }


    public static Following fromDBObject(DBObject doc) {
        Following f = new Following();
        f.setId(doc.get("_id").toString());
        f.setFolloweeHandle(doc.get("followee").toString());
        f.setFollowerHandle(doc.get("follower").toString());
        f.setTime(doc.get("time").toString());

        return f;
    }


    @Override
    public JsonObject toJson() {
        /*
        return Json.createObjectBuilder()
                .add("followee", getFolloweeHandle())
                .add("follower", getFollowerHandle())
                .add("at", getTime()).build();
                */
        return null;
    }
}
