package com.hugeflow.aire;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.myplaysurface) VideoView mVideoView;
    @InjectView(R.id.onetimebtn) Button mOneTimeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        hideSystemUI();
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        mVideoView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public void startPlayback() {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample);
        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();
        mVideoView.start();
        mVideoView.setOnCompletionListener(getOnCompletionListener());
    }

    private MediaPlayer.OnCompletionListener getOnCompletionListener() {
        return new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPeriodicalHandler.sendEmptyMessageDelayed(0, 2 * 60 * 1000);
                SimpleSocketClient simpleSocketClient = new SimpleSocketClient(
                        "ip", 5000, "msg");
                new Thread(simpleSocketClient).start();
            }
        };
    }

    private Handler mPeriodicalHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startPlayback();
        }
    };

    public void oneTimeClickable(View view) {
        mOneTimeBtn.setVisibility(View.GONE);
    }
}