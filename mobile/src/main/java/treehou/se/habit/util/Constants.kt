package treehou.se.habit.util

import java.util.HashSet

import se.treehou.ng.ohcommunicator.connector.models.OHItem

object Constants {

    // TODO remove when support for multiple servers.
    val PREFERENCE_SERVER = "server"

    val MY_OPENHAB_URL = "https://myopenhab.org:443"
    val MY_OPENHAB_URL_COMPARATOR = "myopenhab.org"

    val GCM_SENDER_ID = "737820980945"


    val FIREABASE_DEBUG_KEY_ACTIVITY = "Activity"
    val FIREABASE_DEBUG_KEY_FRAGMENT = "Fragment"

    // NotificationDB to speech.
    val PREF_REGISTRATION_SERVER = "notification_to_speech"

    val DEFAULT_NOTIFICATION_TO_SPEACH = false

    val PREF_INIT_SETUP = "init_setup"

    val MIN_TEXT_ADDON = 50
    val MAX_TEXT_ADDON = 200
    val DEFAULT_TEXT_ADDON = 100

    val SUPPORT_SWITCH: MutableSet<String> = mutableSetOf()

    val SUPPORT_INC_DEC: MutableSet<String> = mutableSetOf()

    init {
        SUPPORT_SWITCH.add(OHItem.TYPE_GROUP)
        SUPPORT_SWITCH.add(OHItem.TYPE_SWITCH)
        SUPPORT_SWITCH.add(OHItem.TYPE_STRING)
        SUPPORT_SWITCH.add(OHItem.TYPE_NUMBER)
        SUPPORT_SWITCH.add(OHItem.TYPE_CONTACT)
        SUPPORT_SWITCH.add(OHItem.TYPE_COLOR)
    }

    init {
        SUPPORT_SWITCH.add(OHItem.TYPE_GROUP)
        SUPPORT_INC_DEC.add(OHItem.TYPE_NUMBER)
        SUPPORT_INC_DEC.add(OHItem.TYPE_DIMMER)
    }
}
