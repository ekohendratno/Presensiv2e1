package com.absensi.sekolah.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootCompletedReceiver extends BroadcastReceiver {
    public static String TAG = "OnBootCompletedReceiver";
    //starts main service on device boot
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Log.w(TAG,"OnBootCompletedReceiver!");

            //Intent startMainServiceIntent = new Intent(context, MainService.class);
            //context.startService(startMainServiceIntent);

        }
    }
}
