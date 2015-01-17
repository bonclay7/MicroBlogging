package fr.grk.ecp.models;

import com.mongodb.BasicDBObject;

import javax.json.JsonObject;

/**
 * Created by grk on 07/12/14.
 */
public abstract class MongoObject {


    public abstract BasicDBObject toDBObject();

    public abstract JsonObject toJson();

}
