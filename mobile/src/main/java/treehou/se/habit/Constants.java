package treehou.se.habit;

import java.util.HashSet;
import java.util.Set;

import treehou.se.habit.core.Item;

/**
 * Created by ibaton on 2014-09-10.
 */
public class Constants {

    // TODO remove when support for multiple servers.
    public static final String PREFERENCE_SERVER    = "server";

    // URL for remote openHAB server.
    public static final String PREF_URL_REMOTE  = "url_remote";

    // URL for local openHAB server.
    public static final String PREF_URL_LOCAL   = "url_local";

    // Username for openHAB server.
    public static final String PREF_USERNAME    = "user";

    // Password for openHAB server.
    public static final String PREF_PASSWORD    = "password";

    // Notification to speech.
    public static final String PREF_REGISTRATION_SERVER = "notification_to_speech";

    public static final String PREFIX_NOTIFICATION = "prefnot_";

    public static final boolean DEFAULT_NOTIFICATION_TO_SPEACH = false;

    public static final String PREF_INIT_SETUP = "init_setup";

    public static final int MIN_TEXT_ADDON = 50;
    public static final int MAX_TEXT_ADDON = 200;
    public static final int DEFAULT_TEXT_ADDON = 100;

    public static final Set<String> SUPPORT_SWITCH = new HashSet<>();
    static {
        SUPPORT_SWITCH.add(Item.TYPE_SWITCH);
        SUPPORT_SWITCH.add(Item.TYPE_GROUP);
        SUPPORT_SWITCH.add(Item.TYPE_STRING);
        SUPPORT_SWITCH.add(Item.TYPE_NUMBER);
        SUPPORT_SWITCH.add(Item.TYPE_CONTACT);
        SUPPORT_SWITCH.add(Item.TYPE_COLOR);
    }

    public static final Set<String> SUPPORT_INC_DEC = new HashSet<>();
    static {
        SUPPORT_INC_DEC.add(Item.TYPE_NUMBER);
        SUPPORT_INC_DEC.add(Item.TYPE_DIMMER);
    }
}
