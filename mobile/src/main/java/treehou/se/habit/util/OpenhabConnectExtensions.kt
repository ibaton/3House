package treehou.se.habit.util

import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import se.treehou.ng.ohcommunicator.util.OpenhabUtil

fun OHWidget.getName(): String{
    return label.replace("(\\[)(.*)(\\])".toRegex(),"")
}

fun OHWidget.isRollerShutter() : Boolean{
    return OpenhabUtil.isRollerShutter(item?.type ?: "")
}