package com.hugeflow.aire.controller;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by moltak on 14. 12. 25..
 * 바람 세기 측정.
 */
public class BlowAirDetector implements Runnable {
    @Override
    public void run() {
        try {
            isBlowing();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void isBlowing() throws InterruptedException {
        int minSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize);

        long startTime = Calendar.getInstance().getTimeInMillis();
        Log.d("timer", "  timer is stared");

        short[] buffer = new short[minSize];
        ar.startRecording();

        while(true) {
            ar.read(buffer, 0, minSize);
            for (short s : buffer) {
                int blow_value = Math.abs(s);
                if (blow_value > 32400) {  //DETECT VOLUME (IF I BLOW IN THE MIC) 27000
                    blowupHandler.sendEmptyMessage(0);
                }
            }

            Thread.sleep(100);

            if (isNotTimeOver(startTime)) break;
        }
    }

    private boolean isNotTimeOver(long startTime) {
        if(Calendar.getInstance().getTimeInMillis() - startTime >= 5000) {
            Log.d("timer", "  time is over.");
            return true;
        }
        return false;
    }

    private Handler blowupHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BlowAirEventBus.getInstance().post(new Event());
        }
    };

    public static class Event {}
}
