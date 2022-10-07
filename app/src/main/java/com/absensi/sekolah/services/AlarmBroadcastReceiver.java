package com.absensi.sekolah.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.absensi.sekolah.config.Koneksi;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static String TAG = "AlarmBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(TAG,"AlarmBroadcastReceiver!");
        new Koneksi(context).createOneTimeExactAlarm();//re-set alarm

        //Intent serviceIntent = new Intent(context, MainService.class);
        //context.startService(serviceIntent);//the service may already be running, in which case nothing happens here; if it's not, service is started
    }
}
