package com.absensi.sekolah.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {
    public static String TAG = "NetworkChangeBroadcastReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.w(TAG,"NetworkChangeBroadcastReceiver!");

        //Intent startMainServiceIntent = new Intent(context, MainService.class);
        //context.startService(startMainServiceIntent);
    }
}
