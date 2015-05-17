package treehou.se.habit.ui.widgets.factories;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.Item;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.widgets.WidgetFactory;

/**
 * Created by ibaton on 2014-10-19.
 */
public class TextBuilder implements IWidgetBuilder {

    @Override
    public WidgetFactory.IWidgetHolder build(final WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {

        TextBuilderHolder holder = new TextBuilderHolder(widget, parent, widgetFactory);
        return holder;
    }

    public static class TextBuilderHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "TextBuilderHolder";

        private BaseBuilder.BaseBuilderHolder baseHolder;
        private WidgetFactory factory;

        public TextBuilderHolder(Widget widget, Widget parent, WidgetFactory factory) {

            this.factory = factory;

            baseHolder = new BaseBuilder.BaseBuilderHolder.Builder(factory)
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
        public void update(final Widget widget) {
            if (widget == null) {
                return;
            }

            final Item item = widget.getItem();
            final Context context = factory.getContext();
            if(item != null && item.getType().equals(Item.TYPE_STRING) && item.getType().equals(Item.TYPE_STRING)){

                baseHolder.getView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.send_text_command));

                        View inputView = factory.getInflater().inflate(R.layout.dialog_input_text, null);

                        final EditText input = (EditText) inputView.findViewById(R.id.txt_command);
                        input.setText(item.getState());

                        if(item.getType().equals(Item.TYPE_STRING)) {
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                        else if (item.getType().equals(Item.TYPE_NUMBER)) {
                            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        }
                        builder.setView(inputView);

                        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String text = input.getText().toString();
                                Communicator.instance(context).command(factory.getServer(), widget.getItem(), text);
                            }
                        });
                        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();

                        return false;
                    }
                });
            }

            baseHolder.update(widget);
        }
    }
}
