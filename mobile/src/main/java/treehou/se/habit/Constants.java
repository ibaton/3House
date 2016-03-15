package treehou.se.habit;

import java.util.HashSet;
import java.util.Set;

import se.treehou.ng.ohcommunicator.core.OHItemWrapper;

public class Constants {

    // TODO remove when support for multiple servers.
    public static final String PREFERENCE_SERVER    = "server";

    public static final String GCM_SENDER_ID = "737820980945";

    // NotificationDB to speech.
    public static final String PREF_REGISTRATION_SERVER = "notification_to_speech";

    public static final boolean DEFAULT_NOTIFICATION_TO_SPEACH = false;

    public static final String PREF_INIT_SETUP = "init_setup";

    public static final int MIN_TEXT_ADDON = 50;
    public static final int MAX_TEXT_ADDON = 200;
    public static final int DEFAULT_TEXT_ADDON = 100;

    public static final Set<String> SUPPORT_SWITCH = new HashSet<>();
    static {
        SUPPORT_SWITCH.add(OHItemWrapper.TYPE_GROUP);
        SUPPORT_SWITCH.add(OHItemWrapper.TYPE_SWITCH);
        SUPPORT_SWITCH.add(OHItemWrapper.TYPE_STRING);
        SUPPORT_SWITCH.add(OHItemWrapper.TYPE_NUMBER);
        SUPPORT_SWITCH.add(OHItemWrapper.TYPE_CONTACT);
        SUPPORT_SWITCH.add(OHItemWrapper.TYPE_COLOR);
    }

    public static final Set<String> SUPPORT_INC_DEC = new HashSet<>();
    static {
        SUPPORT_SWITCH.add(OHItemWrapper.TYPE_GROUP);
        SUPPORT_INC_DEC.add(OHItemWrapper.TYPE_NUMBER);
        SUPPORT_INC_DEC.add(OHItemWrapper.TYPE_DIMMER);
    }
}
