package fr.grk.ecp.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by grk on 07/12/14.
 */
@XmlRootElement
public class Wrapper {

    @XmlElement
    private String server;
    @XmlElement
    private List<User> users;
    @XmlElement
    private List<Tweet> tweets;

    public Wrapper() {
    }

    public Wrapper(List<User> users) {
        this.users = users;
    }

    public String getServer() {
        this.server = "192.168.56.102";
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

}