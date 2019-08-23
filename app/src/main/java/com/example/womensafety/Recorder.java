package com.example.womensafety;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Recorder extends Service {
   Timer t;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("recorder1","Oncreate");
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                //Called each time when 1000 milliseconds (1 second) (the period parameter)
                startRecording();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopRecording();
            }

        }, 0, 500);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("recorder1","start command");

      //  MainActivity.mStorageReference = FirebaseStorage.getInstance().getReference();

        MainActivity.mfilename= Environment.getExternalStorageDirectory().getAbsolutePath();
        MainActivity.mfilename+= "/record_audio.mp3";
        startRecording();

        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification notification= new NotificationCompat.Builder(this,MainActivity.CHANNEL_ID)
                .setContentTitle("Safety")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();
        Log.d("recorder1","start notification");

        startForeground(1,notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("recorder1","On Destroy");
        stopRecording();
        t.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopRecording() {
        MainActivity.mRecorder.stop();
        MainActivity.mRecorder.release();
        Log.d("recorder1","stop Recording");

       MainActivity.mRecorder = null;
        //  uploadAudio();
    }
    private void startRecording() {
        MainActivity.mRecorder = new MediaRecorder();
        MainActivity.mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        MainActivity.mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        MainActivity.mRecorder.setOutputFile(MainActivity.mfilename);
        MainActivity.mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        Log.d("recorder1","start recording fn");

        try {
            MainActivity.mRecorder.prepare();
            Log.d("recorder1","mrocrder.prepare");

        } catch (IOException e) {
            Log.e("recorder_error", "prepare() failed");
        }

        MainActivity.mRecorder.start();
    }
}
