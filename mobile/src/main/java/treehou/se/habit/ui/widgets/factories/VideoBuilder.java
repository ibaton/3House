package treehou.se.habit.ui.widgets.factories;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import treehou.se.habit.R;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.widgets.WidgetFactory;

/**
 * Created by ibaton on 2014-10-19.
 */
public class VideoBuilder implements IWidgetBuilder {

    private static final String TAG = "VideoBuilder";

    @Override
    public WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {

        return new VideoBuilderHolder(widget, parent, widgetFactory);
    }

    public static class VideoBuilderHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "VideoBuilderHolder";

        private BaseBuilder.BaseBuilderHolder baseHolder;
        private WidgetFactory factory;

        private View itemView;
        private VideoView mVideoView;
        private Widget widget;

        public VideoBuilderHolder(Widget widget, Widget parent, WidgetFactory factory) {
            this.factory = factory;

            itemView = factory.getInflater().inflate(R.layout.item_widget_video, null);
            mVideoView = (VideoView) itemView.findViewById(R.id.vidView);

            baseHolder = new BaseBuilder.BaseBuilderHolder.Builder(factory)
                    .setWidget(widget)
                    .setParent(parent)
                    .setShowLabel(false)
                    .build();

            baseHolder.getSubView().addView(itemView);
            update(widget);
        }

        @Override
        public void update(final Widget widget) {
            Log.d(TAG, "update " + widget);

            if (widget == null) {
                return;
            }

            if(this.widget == null || !this.widget.getUrl().equals(widget.getUrl())){
                setVideoUri(widget.getUrl());
                launchVideo();
            }

            baseHolder.update(widget);
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }

        private void setVideoUri(String uri) {
            mVideoView.setVideoURI(Uri.parse(uri));
        }

        private void launchVideo(){
            //Initializing the video player’s media controller.
            MediaController controller = new MediaController(factory.getContext());
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    Log.d(TAG, "Load video widget on prepare");
                    mVideoView.getLayoutParams().height = (int)(((float) mp.getVideoWidth())/mp.getVideoHeight()) * mVideoView.getLayoutParams().width;
                    mVideoView.requestLayout();
                }
            });

            //Binding media controller with VideoView
            mVideoView.setMediaController(controller);
        }
    }
}
