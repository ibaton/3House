package treehou.se.habit.tasker.reciever

import android.content.Context
import android.os.Bundle
import android.util.Log

import javax.inject.Inject

import io.realm.Realm
import treehou.se.habit.connector.Communicator
import treehou.se.habit.core.db.model.ItemDB
import treehou.se.habit.tasker.boundle.IncDecBoundleManager
import treehou.se.habit.util.Util

class IncDecReciever : IFireReciever {

    @Inject lateinit var communicator: Communicator

    fun isBundleValid(bundle: Bundle?): Boolean {
        if (null == bundle) {
            Log.e(TAG, "Bundle cant be null")
            return false
        }

        if (!bundle.containsKey(BUNDLE_EXTRA_VALUE)) {
            Log.e(TAG, String.format("bundle must contain extra %s", BUNDLE_EXTRA_VALUE))
            return false
        }

        if (5 != bundle.keySet().size) {
            Log.e(TAG, String.format("bundle must contain 5 keys, but currently contains %d keys: %s", bundle.keySet().size, bundle.keySet()))
            return false
        }

        return true
    }


    override fun fire(context: Context, bundle: Bundle): Boolean {
        Util.getApplicationComponent(context).inject(this)

        if (isBundleValid(bundle)) {
            val itemId = bundle.getInt(BUNDLE_EXTRA_ITEM)

            val min = bundle.getInt(BUNDLE_EXTRA_MIN)
            val max = bundle.getInt(BUNDLE_EXTRA_MAX)
            val range = Math.abs(max) + Math.abs(min)

            val value = Math.max(Math.min(bundle.getInt(BUNDLE_EXTRA_VALUE), range), -range)

            val realm = Realm.getDefaultInstance()
            val item = ItemDB.load(realm, itemId.toLong())
            if (item != null) {
                communicator!!.incDec(item.server?.toGeneric(), item.name, value, min, max)
                Log.d(TAG, "Sent sendCommand " + value + " to item " + item.name)
            } else {
                Log.d(TAG, "Item no longer exists")
            }
            realm.close()
        } else {
            Log.d(TAG, "Boundle not valid.")
        }

        return false
    }

    companion object {

        val TAG = "IncDecReciever"

        val TYPE = IncDecBoundleManager.TYPE_COMMAND

        val BUNDLE_EXTRA_VALUE = "treehou.se.habit.extra.VALUE"
        val BUNDLE_EXTRA_MIN = "treehou.se.habit.extra.MIN"
        val BUNDLE_EXTRA_MAX = "treehou.se.habit.extra.MAX"
        val BUNDLE_EXTRA_ITEM = "treehou.se.habit.extra.ITEM"
    }
}
