package fr.grk.ecp.models;

import com.mongodb.BasicDBObject;

import javax.json.JsonObject;

/**
 * Created by grk on 16/01/15.
 */
public class Session extends MongoObject{
    private String token;
    private String handle;
    private String creationDate;

    @Override
    public BasicDBObject toDBObject() {
        return null;
    }

    @Override
    public JsonObject toJson() {
        return null;
    }
    //TODO : Design Model
    //TODO : Create session's ejb
    //TODO : Authentication JAX-RS Services Class
}
