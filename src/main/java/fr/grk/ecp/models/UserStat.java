package fr.grk.ecp.models;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by grk on 24/01/15.
 */
public class UserStat {
    private String handle;
    private String picture;
    private int followers;
    private int follows;
    private boolean followed;

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("handle", getHandle())
                .add("picture", getPicture())
                .add("followers", getFollowers())
                .add("follows", getFollows())
                .add("followed", isFollowed())
                .build();
    }


}
