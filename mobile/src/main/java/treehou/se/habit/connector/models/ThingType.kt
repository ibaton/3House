package treehou.se.habit.connector.models

import com.google.gson.annotations.SerializedName

class ThingType {

    val channels: List<Channel>? = null
    val description: String? = null
    val label: String? = null

    @SerializedName("UID")
    private val uID: String? = null

    val isBridge: Boolean = false

    fun getuID(): String? {
        return uID
    }
}
