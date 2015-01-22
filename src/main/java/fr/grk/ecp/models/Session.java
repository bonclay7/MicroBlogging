package fr.grk.ecp.models;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by grk on 16/01/15.
 */
public class Session extends MongoObject{

    private String id;
    private String token;
    private String handle;
    private String hostID;
    private String creationDate;
    private int status;


    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public BasicDBObject toDBObject() {
        BasicDBObject doc = new BasicDBObject();
        doc.put("handle", handle);
        doc.put("token", token);
        doc.put("date", creationDate);
        doc.put("status", status);
        doc.put("host_id", hostID);
        return doc;
    }

    @Override
    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("handle", getHandle())
                .add("token", getToken())
                .add("creationDate", getCreationDate())
                .add("host", getHostID())
                .add("status", getStatus()).build();
    }


    public static Session fromDBObject(DBObject doc) {
        Session s = new Session();
        s.setId(doc.get("_id") == null ? null : doc.get("_id").toString());
        s.setToken(doc.get("token") == null ? null : doc.get("token").toString());
        try{
            s.setStatus(Integer.parseInt(doc.get("status") == null ? null : doc.get("status").toString()));
        }catch (Exception e){
            s.setStatus(0);
        }
        s.setHandle(doc.get("handle") == null ? null : doc.get("handle").toString());
        s.setCreationDate(doc.get("date") == null ? null : doc.get("date").toString());
        s.setHostID(doc.get("host_id") == null ? null : doc.get("host_id").toString());
        return s;
    }

}
