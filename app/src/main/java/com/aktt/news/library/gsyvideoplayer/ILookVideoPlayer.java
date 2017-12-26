package com.aktt.news.library.gsyvideoplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.aktt.news.R;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

/**
 * Created by magical on 17/12/26.
 * Description :
 */

public class ILookVideoPlayer extends StandardGSYVideoPlayer {

    public ILookVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public ILookVideoPlayer(Context context) {
        super(context);
    }

    public ILookVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.ilook_video_player;
    }

    @Override
    protected void updateStartImage() {
        if (mStartButton instanceof ImageView) {
            ImageView imageView = (ImageView) mStartButton;
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                imageView.setImageResource(R.drawable.video_click_pause_selector);
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                imageView.setImageResource(R.drawable.video_click_error_selector);
            } else {
                imageView.setImageResource(R.drawable.video_click_play_selector);
            }
        }
    }
}
