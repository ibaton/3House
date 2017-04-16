package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class TextWidgetFactory implements IWidgetFactory {

    @Override
    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
        return new TextWidgetHolder(context, factory, server, page, widget, parent);
    }

    private static class TextWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "TextWidgetHolder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private Context context;
        private OHServer server;

        TextWidgetHolder(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {

            this.server = server;
            this.context = context;

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
                    .setWidget(widget)
                    .setParent(parent)
                    .setFlat(false)
                    .build();

            update(widget);
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }

        @Override
        public void update(final OHWidget widget) {
            if (widget == null) {
                return;
            }

            final OHItem item = widget.getItem();
            if(item != null && item.getType().equals(OHItem.TYPE_STRING) && item.getType().equals(OHItem.TYPE_STRING)){

                baseHolder.getView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.send_text_command));

                        View inputView = LayoutInflater.from(context).inflate(R.layout.dialog_input_text, null);

                        final EditText input = (EditText) inputView.findViewById(R.id.txt_command);
                        input.setText(item.getState());

                        if(item.getType().equals(OHItem.TYPE_STRING)) {
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                        else if (item.getType().equals(OHItem.TYPE_NUMBER)) {
                            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        }
                        builder.setView(inputView);

                        /*IServerHandler serverHandler = new Connector.ServerHandler(server, context);*/
                        builder.setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
                            String text = input.getText().toString();
                            //serverHandler.sendCommand(widget.getItem().getName(), text);
                        });
                        builder.setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> dialog.cancel());
                        builder.show();

                        return false;
                    }
                });
            }

            baseHolder.update(widget);
        }
    }
}
