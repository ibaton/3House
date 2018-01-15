package treehou.se.habit.service.wear.connector.messages

class VoiceCommandMessage @JvmOverloads constructor(var message: String?, var server: Long = -1) {

    fun haveServer(): Boolean {
        return server >= 0
    }
}
