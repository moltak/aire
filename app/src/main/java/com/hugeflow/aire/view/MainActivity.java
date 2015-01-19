package com.hugeflow.aire.view;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.VideoView;

import com.hugeflow.aire.R;
import com.hugeflow.aire.controller.BlowAirDetector;
import com.hugeflow.aire.controller.BlowAirEventBus;
import com.hugeflow.aire.controller.UdpSocketClient;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @InjectView(R.id.videoview) VideoView videoView;
    private boolean isPossiblePlay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initializeVideoView();
        BlowAirEventBus.getInstance().register(this);
    }

    private void initializeVideoView() {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.seed);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.setOnCompletionListener(getOnCompletionListener());
        videoView.seekTo(10);
    }

    @Subscribe
    public void onBlowuped(BlowAirDetector.Event event) {
        if(isPossiblePlay) {
            Log.d("aire", "  -- Play movie!");
            isPossiblePlay = false;
            videoView.start();
        }
    }

    private MediaPlayer.OnCompletionListener getOnCompletionListener() {
        return new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPeriodicalHandler.sendEmptyMessageDelayed(0, 2 * 60 * 1000);

                UdpSocketClient udpSocketClient
                        = new UdpSocketClient("192.168.10.41", 12800, "start");
                new Thread(udpSocketClient).start();
            }
        };
    }

    private Handler mPeriodicalHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            videoView.seekTo(10);
            isPossiblePlay = true;
            Log.d("aire", " -- initialized");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BlowAirEventBus.getInstance().unregister(this);
    }

    @OnClick(R.id.btn_start)
    public void onVideoViewClick() {
        new Thread(new BlowAirDetector()).start();
    }
}