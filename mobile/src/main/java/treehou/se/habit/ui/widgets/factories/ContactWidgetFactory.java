package treehou.se.habit.ui.widgets.factories;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import treehou.se.habit.R;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.widgets.WidgetFactory;

/**
 * Created by ibaton on 2014-10-19.
 */
public class ContactWidgetFactory implements IWidgetFactory {

    @Override
    public WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {

        WidgetFactory.IWidgetHolder rootView = new BaseWidgetFactory().build(widgetFactory, page, widget, parent);

        View itemView = widgetFactory.getInflater().inflate(R.layout.item_widget_switch, null);

        ToggleButton swtSwitch = (ToggleButton) itemView.findViewById(R.id.swt_switch);
        swtSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        LinearLayout subView = (LinearLayout) rootView.getView().findViewById(R.id.lou_widget_holder);
        subView.addView(itemView);

        return rootView;
    }
}
