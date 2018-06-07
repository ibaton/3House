package treehou.se.habit.ui.control

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RemoteViews

import java.util.HashMap

import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.core.db.model.controller.ControllerDB

class CellFactory {

    private val cellBuilders = HashMap<Int, CellBuilder>()
    private var defaultBuilder: CellBuilder = DefaultBuilder()

    fun addBuilder(type: Int, builder: CellBuilder) {
        cellBuilders.put(type, builder)
    }

    fun setDefaultBuilder(builder: CellBuilder) {
        defaultBuilder = builder
    }

    fun create(context: Context, container: ViewGroup, controller: ControllerDB, cell: CellDB): View {

        Log.d(TAG, "cellBuilder cell type " + cell.type)
        var cellView: View
        try {
            val cellType = cell.type

            var cellBuilder: CellBuilder? = cellBuilders[cellType]
            if (cellBuilder == null) {
                Log.d(TAG, "cellBuilder using default")
                cellBuilder = defaultBuilder
            } else {
                Log.d(TAG, "cellBuilder using custom")
            }
            cellView = cellBuilder.build(context, container, controller, cell)
        } catch (e: Exception) {
            Log.d(TAG, "Failed render $cell error $e")
            e.printStackTrace()
            cellView = defaultBuilder.build(context, container, controller, cell)
        }

        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        lp.weight = 1f
        cellView.layoutParams = lp

        return cellView
    }

    fun createRemote(context: Context, controller: ControllerDB, cell: CellDB): RemoteViews? {

        Log.d(TAG, "cellBuilder cell type " + cell.type)

        var cellBuilder: CellBuilder? = cellBuilders[cell.type]

        if (cellBuilder == null) {
            Log.d(TAG, "cellBuilder using default")
            cellBuilder = defaultBuilder
        } else {
            Log.d(TAG, "cellBuilder using custom")
        }

        var remoteViews: RemoteViews?
        try {
            remoteViews = cellBuilder.buildRemote(context, controller, cell)
        } catch (e: Exception) {
            remoteViews = defaultBuilder.buildRemote(context, controller, cell)
        }

        return remoteViews
    }

    interface CellBuilder {

        fun build(context: Context, container: ViewGroup, controller: ControllerDB, cell: CellDB): View
        fun buildRemote(context: Context, controller: ControllerDB, cell: CellDB): RemoteViews?
    }

    class DefaultBuilder : CellBuilder {

        override fun build(context: Context, container: ViewGroup, controller: ControllerDB, cell: CellDB): View {

            val inflater = LayoutInflater.from(context)
            return inflater.inflate(R.layout.cell_empty, container, false)
        }

        override fun buildRemote(context: Context, controller: ControllerDB, cell: CellDB): RemoteViews {
            return RemoteViews(context.packageName, R.layout.cell_empty)
        }
    }

    companion object {

        private val TAG = "CellFactory"
    }
}
