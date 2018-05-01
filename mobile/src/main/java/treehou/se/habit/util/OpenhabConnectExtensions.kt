package treehou.se.habit.util

import se.treehou.ng.ohcommunicator.connector.models.OHWidget

fun OHWidget.getName(): String{
    return label.replace("(\\[)(.*)(\\])".toRegex(),"")
}