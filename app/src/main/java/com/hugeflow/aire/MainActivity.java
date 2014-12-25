package com.hugeflow.aire;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.seed);
        Log.d("uri", uri.toString());
        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();
        mVideoView.setOnCompletionListener(getOnCompletionListener());
        mVideoView.start();
    }

    private MediaPlayer.OnCompletionListener getOnCompletionListener() {
        return new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPeriodicalHandler.sendEmptyMessageDelayed(0, 2 * 60 * 1000);
                sendStartMessage();
            }
        };
    }

    private void sendStartMessage() {
        SimpleSocketClient simpleSocketClient = new SimpleSocketClient(
                "192.168.10.41", 12800, "start");
        new Thread(simpleSocketClient).start();
    }

    private Handler mPeriodicalHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mOneTimeBtn.setVisibility(View.VISIBLE);
        }
    };

    public void oneTimeClickable(View view) {
        startPlayback();
        mOneTimeBtn.setVisibility(View.GONE);
    }
}