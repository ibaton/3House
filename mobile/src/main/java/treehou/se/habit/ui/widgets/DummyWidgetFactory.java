package treehou.se.habit.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import treehou.se.habit.R;
import treehou.se.habit.core.Widget;
import treehou.se.habit.core.settings.WidgetSettings;
import treehou.se.habit.ui.Util;

public class DummyWidgetFactory {

    private static final String TAG = "WidgetFactory";

    private Context context;

    public DummyWidgetFactory(Context context){
        this.context = context;
    }

    public View createWidget(final Widget widget){
        LayoutInflater inflater = LayoutInflater.from(context);

        WidgetSettings settings = WidgetSettings.loadGlobal(context);

        View itemView = inflater.inflate(R.layout.item_widget_base, null);
        View baseDataHolder = itemView.findViewById(R.id.lou_base_data_holder);
        TextView lblName = (TextView) itemView.findViewById(R.id.lbl_widget_name);

        String label = widget.getLabel();
        float percentage = Util.toPercentage(settings.getTextSize());
        lblName.setTextSize(TypedValue.COMPLEX_UNIT_PX,lblName.getTextSize()*percentage);
        lblName.setText(widget.getLabel());
        if(label == null || label.equals("")) {
            baseDataHolder.setVisibility(View.GONE);
        }

        View iconHolder = itemView.findViewById(R.id.img_widget_icon_holder);
        ImageView imgIcon = (ImageView) itemView.findViewById(R.id.img_widget_icon);

        float imageSizePercentage = Util.toPercentage(settings.getIconSize());
        ViewGroup.LayoutParams layoutParams = imgIcon.getLayoutParams();
        layoutParams.width = (int) (((float)layoutParams.width) * imageSizePercentage);
        imgIcon.setLayoutParams(layoutParams);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_item_settings_widget);

        int backgroundType = settings.getImageBackground();

        setBackgroundColor(imgIcon, bitmap, backgroundType);
        iconHolder.setVisibility(View.VISIBLE);

        return itemView;
    }

    public void setBackgroundColor(ImageView imgIcon, Bitmap bitmap, int type){
        imgIcon.setVisibility(View.VISIBLE);
        imgIcon.setImageBitmap(bitmap);
        int imageBackground = Util.getBackground(context, bitmap,type);
        imgIcon.setBackgroundColor(imageBackground);
    }


}
