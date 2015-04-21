package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import treehou.se.habit.R;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Util;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.ColorpickerActivity;
import treehou.se.habit.ui.widgets.WidgetFactory;

/**
 * Created by ibaton on 2014-10-19.
 */
public class ColorpickerBuilder implements IWidgetBuilder {

    private int color;

    @Override
    public WidgetFactory.IWidgetHolder build(final WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {

        WidgetFactory.IWidgetHolder rootView = new BaseBuilder().build(widgetFactory, page, widget, parent);

        LayoutInflater inflater = widgetFactory.getInflater();
        final Context context = widgetFactory.getContext();

        View itemView = inflater.inflate(R.layout.item_widget_color, null);

        color = Color.BLACK;

        if(widget.getItem() != null && widget.getItem().getState() != null) {
            String[] sHSV = widget.getItem().getState().split(",");
            if (sHSV.length == 3) {
                float[] hSV = {
                        Float.valueOf(sHSV[0]),
                        Float.valueOf(sHSV[1]),
                        Float.valueOf(sHSV[2])};
                color = Color.HSVToColor(hSV);
            }
        }

        rootView.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (widget.getItem() != null) {
                    Intent intent = new Intent(context, ColorpickerActivity.class);
                    Gson gson = Util.createGsonBuilder();
                    intent.putExtra(ColorpickerActivity.EXTRA_SERVER, widgetFactory.getServer().getId());
                    intent.putExtra(ColorpickerActivity.EXTRA_WIDGET, gson.toJson(widget));
                    intent.putExtra(ColorpickerActivity.EXTRA_COLOR, color);

                    context.startActivity(intent);
                }
            }
        });

        LinearLayout subView = (LinearLayout) rootView.getView().findViewById(R.id.lou_widget_holder);
        subView.addView(itemView);

        return rootView;
    }
}
