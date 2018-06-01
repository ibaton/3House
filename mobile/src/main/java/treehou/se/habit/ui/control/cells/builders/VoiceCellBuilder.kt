package treehou.se.habit.ui.control.cells.builders

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.RemoteViews
import butterknife.BindView
import butterknife.ButterKnife
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.core.db.model.controller.ControllerDB
import treehou.se.habit.service.VoiceService
import treehou.se.habit.ui.control.CellFactory
import treehou.se.habit.ui.control.ControllerUtil
import treehou.se.habit.ui.util.ViewHelper
import treehou.se.habit.util.Util

class VoiceCellBuilder : CellFactory.CellBuilder {

    @BindView(R.id.img_icon_button) lateinit var imgIcon: ImageButton

    override fun build(context: Context, controller: ControllerDB, cell: CellDB): View {
        val inflater = LayoutInflater.from(context)
        val cellView = inflater.inflate(R.layout.cell_button, null)
        ButterKnife.bind(this, cellView)

        val voiceCell = cell.getCellVoice()

        val pallete = ControllerUtil.generateColor(controller, cell)

        imgIcon.setImageDrawable(Util.getIconDrawable(context, voiceCell!!.icon))
        imgIcon.background.setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY)
        imgIcon.setOnClickListener { v ->

            if (voiceCell!!.item == null || voiceCell.item!!.server == null) {
                return@setOnClickListener
            }

            val server = voiceCell.item!!.server

            val callbackIntent = VoiceService.createVoiceCommand(context, server!!)
            val openhabPendingIntent = PendingIntent.getService(context, 9, callbackIntent, PendingIntent.FLAG_CANCEL_CURRENT)

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            // Specify the calling package to identify your application
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, VoiceService::class.java.`package`.name)
            // Display an hint to the user about what he should say.
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    context.getString(R.string.voice_command_title))

            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            //intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, openhabPendingIntent)

            // Instruct the widget manager to update the widget
            context.startActivity(intent)
        }

        return cellView
    }

    override fun buildRemote(context: Context, controller: ControllerDB, cell: CellDB): RemoteViews? {
        val voiceCell = cell.getCellVoice()

        val cellView = RemoteViews(context.packageName, R.layout.cell_button)

        val pallete = ControllerUtil.generateColor(controller, cell)
        ViewHelper.colorRemoteDrawable(cellView, R.id.img_icon_button, pallete[ControllerUtil.INDEX_BUTTON])

        cellView.setImageViewBitmap(R.id.img_icon_button, Util.getIconBitmap(context, voiceCell!!.icon))

        if (voiceCell.item == null || voiceCell.item!!.server == null) {
            return cellView
        }

        val server = voiceCell.item!!.server

        val callbackIntent = VoiceService.createVoiceCommand(context, server!!)
        val openhabPendingIntent = PendingIntent.getService(context.applicationContext, (Math.random() * Integer.MAX_VALUE).toInt(), callbackIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, VoiceService::class.java.`package`.name)
        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.voice_command_title))

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        //intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, openhabPendingIntent)

        val pendingIntent = PendingIntent.getActivity(context, 9, intent, 0)

        cellView.setOnClickPendingIntent(R.id.img_icon_button, pendingIntent)

        return cellView
    }

    companion object {

        private val TAG = "VoiceCellBuilder"
    }
}
