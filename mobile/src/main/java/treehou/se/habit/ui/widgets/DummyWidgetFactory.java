package treehou.se.habit.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.util.Util;

public class DummyWidgetFactory {

    private static final String TAG = "DummyWidgetFactory";

    private Context context;

    public DummyWidgetFactory(Context context){
        this.context = context;
    }

    public View createWidget(final OHWidget widget){
        LayoutInflater inflater = LayoutInflater.from(context);

        Realm realm = Realm.getDefaultInstance();
        WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        int backgroundType = settings.getImageBackground();
        float percentage = Util.INSTANCE.toPercentage(settings.getTextSize());
        float imageSizePercentage = Util.INSTANCE.toPercentage(settings.getIconSize());
        realm.close();

        View holderView = inflater.inflate(R.layout.widget_container, null);
        LinearLayout holder = holderView.findViewById(R.id.lou_widget_frame_holder);

        View itemView = inflater.inflate(R.layout.item_widget_base, holder, false);
        View baseDataHolder = itemView.findViewById(R.id.lou_base_data_holder);
        TextView lblName = itemView.findViewById(R.id.widgetName);

        String label = widget.getLabel();
        lblName.setTextSize(TypedValue.COMPLEX_UNIT_PX,lblName.getTextSize()*percentage);
        lblName.setText(widget.getLabel());
        if(label == null || label.equals("")) {
            baseDataHolder.setVisibility(View.GONE);
        }

        View iconHolder = itemView.findViewById(R.id.img_widget_icon_holder);
        ImageView imgIcon = itemView.findViewById(R.id.widgetIcon);

        ViewGroup.LayoutParams layoutParams = imgIcon.getLayoutParams();
        layoutParams.width = (int) (((float)layoutParams.width) * imageSizePercentage);
        imgIcon.setLayoutParams(layoutParams);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_item_settings_widget);

        setBackgroundColor(imgIcon, bitmap, backgroundType);
        iconHolder.setVisibility(View.VISIBLE);

        holder.addView(itemView);

        return holderView;
    }

    public void setBackgroundColor(ImageView imgIcon, Bitmap bitmap, int type){
        imgIcon.setVisibility(View.VISIBLE);
        imgIcon.setImageBitmap(bitmap);
        int imageBackground = Util.INSTANCE.getBackground(context, bitmap,type);
        imgIcon.setBackgroundColor(imageBackground);
    }
}
