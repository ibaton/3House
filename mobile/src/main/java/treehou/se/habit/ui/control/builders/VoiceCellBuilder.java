package treehou.se.habit.ui.control.builders;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.Controller;
import treehou.se.habit.core.controller.VoiceCell;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerUtil;
import treehou.se.habit.ui.homescreen.VoiceService;

public class VoiceCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "VoiceCellBuilder";

    public View build(final Context context, Controller controller, final Cell cell){
        final VoiceCell voiceCell = cell.voiceCell();

        int[] pallete = ControllerUtil.generateColor(controller, cell);

        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_button, null);
        cellView.setBackgroundColor(pallete[ControllerUtil.INDEX_BUTTON]);

        ImageButton imgIcon = (ImageButton) cellView.findViewById(R.id.img_icon_button);
        imgIcon.setImageDrawable(Util.getIconDrawable(context, voiceCell.getIcon()));

        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Server server = Loader.loadServer(context);

                Intent callbackIntent = VoiceService.createVoiceCommand(context, voiceCell.getItem().getServer());
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
            }
        });

        return cellView;
    }

    @Override
    public RemoteViews buildRemote(final Context context, Controller controller, Cell cell) {
        final VoiceCell voiceCell = cell.voiceCell();

        RemoteViews cellView = new RemoteViews(context.getPackageName(), R.layout.cell_button);
        cellView.setInt(R.id.cell_button, "setBackgroundColor", cell.getColor());

        cellView.setImageViewBitmap(R.id.img_icon_button, Util.getIconBitmap(context, voiceCell.getIcon()));

        Intent callbackIntent = VoiceService.createVoiceCommand(context, voiceCell.getItem().getServer());
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
