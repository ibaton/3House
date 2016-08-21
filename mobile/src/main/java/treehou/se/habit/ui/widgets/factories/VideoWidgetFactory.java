package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class VideoWidgetFactory implements IWidgetFactory {

    @Override
    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
        return new VideoWidgetHolder(context, factory, server, page, widget, parent);
    }

    public static class VideoWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "VideoWidgetHolder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private Context context;

        private View itemView;
        private VideoView mVideoView;
        private OHWidget widget;

        public VideoWidgetHolder(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
            this.context = context;

            itemView = LayoutInflater.from(context).inflate(R.layout.item_widget_video, null);
            mVideoView = (VideoView) itemView.findViewById(R.id.vidView);

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
                    .setWidget(widget)
                    .setParent(parent)
                    .setShowLabel(false)
                    .build();

            baseHolder.getSubView().addView(itemView);
            update(widget);
        }

        @Override
        public void update(final OHWidget widget) {
            Log.d(TAG, "update " + widget);

            if (widget == null) {
                return;
            }

            if(this.widget == null || !this.widget.getUrl().equals(widget.getUrl())){
                setVideoUri(widget.getUrl());
                launchVideo();
            }

            this.widget = widget;
            baseHolder.update(widget);
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }

        private void setVideoUri(String uri) {
            if(!TextUtils.isEmpty(uri)){
                mVideoView.setVideoURI(Uri.parse(uri));
            }
        }

        private void launchVideo(){
            //Initializing the video player’s media controller.
            MediaController controller = new MediaController(context);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    Log.d(TAG, "Load video widget on prepare");
                    mVideoView.getLayoutParams().height = (int)(((float) mp.getVideoWidth())/mp.getVideoHeight()) * mVideoView.getLayoutParams().width;
                    mVideoView.requestLayout();
                }
            });

            //OHBindingWrapper media controller with VideoView
            mVideoView.setMediaController(controller);
        }
    }
}
