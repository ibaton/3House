package treehou.se.habit.ui.control.builders;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.core.db.model.controller.VoiceCellDB;
import treehou.se.habit.ui.homescreen.VoiceService;
import treehou.se.habit.ui.util.ViewHelper;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerUtil;

public class VoiceCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "VoiceCellBuilder";

    @BindView(R.id.img_icon_button) ImageButton imgIcon;

    public View build(final Context context, ControllerDB controller, final CellDB cell){
        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_button, null);
        ButterKnife.bind(this, cellView);

        Realm realm = Realm.getDefaultInstance();
        final VoiceCellDB voiceCell = cell.getCellVoice();

        int[] pallete = ControllerUtil.generateColor(controller, cell);

        imgIcon.setImageDrawable(Util.getIconDrawable(context, voiceCell.getIcon()));
        imgIcon.getBackground().setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY);
        imgIcon.setOnClickListener(v -> {

            if(voiceCell.getItem() == null || voiceCell.getItem().getServer()  == null){
                return;
            }

            ServerDB server = voiceCell.getItem().getServer();

            Intent callbackIntent = VoiceService.createVoiceCommand(context, server);
            PendingIntent openhabPendingIntent = PendingIntent.getService(context, 9, callbackIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            // Specify the calling package to identify your application
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, VoiceService.class.getPackage().getName());
            // Display an hint to the user about what he should say.
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    context.getString(R.string.voice_command_title));

            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            //intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, openhabPendingIntent);

            // Instruct the widget manager to update the widget
            context.startActivity( intent);
        });

        return cellView;
    }

    @Override
    public RemoteViews buildRemote(final Context context, ControllerDB controller, CellDB cell) {
        Realm realm = Realm.getDefaultInstance();
        final VoiceCellDB voiceCell = cell.getCellVoice();

        RemoteViews cellView = new RemoteViews(context.getPackageName(), R.layout.cell_button);

        int[] pallete = ControllerUtil.generateColor(controller, cell);
        ViewHelper.colorRemoteDrawable(cellView, R.id.img_icon_button, pallete[ControllerUtil.INDEX_BUTTON]);

        cellView.setImageViewBitmap(R.id.img_icon_button, Util.getIconBitmap(context, voiceCell.getIcon()));

        if(voiceCell.getItem() == null || voiceCell.getItem().getServer()  == null){
            return cellView;
        }

        ServerDB server = voiceCell.getItem().getServer();

        Intent callbackIntent = VoiceService.createVoiceCommand(context, server);
        PendingIntent openhabPendingIntent = PendingIntent.getService(context.getApplicationContext(), (int)(Math.random()*Integer.MAX_VALUE), callbackIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, VoiceService.class.getPackage().getName());
        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.voice_command_title));

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, openhabPendingIntent);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 9, intent, 0);

        cellView.setOnClickPendingIntent(R.id.img_icon_button, pendingIntent);

        return cellView;
    }
}
