package com.absensi.sekolah.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.absensi.sekolah.config.Koneksi;

import java.util.Timer;
import java.util.TimerTask;

public class MainService extends Service {
    public static String TAG = "MainService";
    public int counter = 0;
    private static final double UPLOAD_INTERVAL = 2;//in minutes

    @Override
    public void onCreate() {
        Log.i(TAG, "Service started.");
        new Koneksi(this).createOneTimeExactAlarm();

        /*Start a server-talking loop thread*/
        if (Koneksi.getThreadsByName("ServerTalkLoopThread").size() == 0) {//if server-talking thread doesn't exist
            ServerTalkLoopThread serverTalkLoopThread = new ServerTalkLoopThread(this);
            serverTalkLoopThread.setName("ServerTalkLoopThread");
            serverTalkLoopThread.start();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "Service about to be destroyed");
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 10000, 10000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {


                Log.i("in timer", "in timer ++++  " + (counter++));
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
