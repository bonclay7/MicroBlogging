package fr.grk.ecp.utils;

/**
 * Created by grk on 12/12/14.
 */
public class Preferences {

    //private static Preferences instance = null;

    //public static String MONGO_DB_ADDR = "172.31.9.100";
    public static String MONGO_DB_ADDR = "192.168.56.102";
    public static String SERVER_NAME = "Hayabuza";
    public static String COLLECTION_NAME = "microblogging";
    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final int SESSION_ACTIVE = 1;
    public static final int SESSION_INACTIVE = 0;
    public static final int SESSION_TIMEOUT = 0; // (seconds) 0 : unlimited


}
