package fr.grk.ecp.rs;

import fr.grk.ecp.models.Tweet;
import fr.grk.ecp.utils.Preferences;

import javax.json.JsonObject;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by grk on 10/02/15.
 */
public abstract class MicrobloggingService {
    protected static final Pattern handlePattern = Pattern.compile("^(:)(\\w+)$");
    private String token;
    private String hostID;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public void parseAPIToken(String apiToken){
        if (apiToken == null) return;

        apiToken = apiToken.trim();
        apiToken = apiToken.replace(Preferences.AUTH_METHOD, "");
        token = apiToken.split("\\.")[0];
        hostID = apiToken.split("\\.")[1];

    }

}
