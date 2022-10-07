package com.absensi.sekolah.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.absensi.sekolah.LoginActivity;
import com.absensi.sekolah.R;
import com.absensi.sekolah.Splash;
import com.absensi.sekolah.config.Koneksi;
import com.absensi.sekolah.controller.HalamanNotifikasi;
import com.absensi.sekolah.controller.orangtua.Dashboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

public class ServerTalkLoopThread extends Thread {
    public static String TAG = "ServerTalkLoopThread";

    static Koneksi koneksi;
    private Context context;

    ServerTalkLoopThread(Context context) {
        this.context = context;


        koneksi = new Koneksi(context);

        koneksi.policyAllow();
    }

    @Override
    public void run() {

        try {
            Looper.prepare();
            while (!Thread.currentThread().isInterrupted()) {
                if (new Koneksi(context).haveNetworkConnection()) {
                    //talk to server
                    try {
                        executeCommands(); //executes any pending web commands and send output to server
                    } catch (MalformedURLException muex) {
                        Log.w(TAG, "MalformedURLException @ServerTalkLoopThread.run()\n" + muex.getMessage());
                    } catch (IOException ioex) {
                        Log.w(TAG, "IOException @ServerTalkLoopThread.run()\nEither .getOutputStream() or .getResponseMessage() timed out\n" + ioex.getMessage());
                    }
                }
                Thread.sleep(5000);
            }
            Looper.loop();
        } catch (InterruptedException iex) {
            //out of the loop if thread interrupted
        }
    }

    static SharedPreferences sharedpreferences;
    private void executeCommands() throws IOException {

        sharedpreferences = context.getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String level = sharedpreferences.getString("level", "");
        String username = sharedpreferences.getString("username", "");

        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(level)){
            try {
                JSONObject j1 = koneksi.getDataServer("/api/notif?l="+level+"&u="+username);
                if( j1.getBoolean("success") ) {
                    JSONObject j2 = j1.getJSONObject("response");

                    Intent intent = new Intent(context, HalamanNotifikasi.class);
                    intent.putExtra("goto","notif");

                    tampilNotifikasi(
                            j2.getString("notif_title"),
                            j2.getString("notif_body"),
                            intent
                    );

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    // deklarasi kode request
    public static final int notifikasi = 1;

    private void tampilNotifikasi(String title, String content, Intent intent) {
        // membuat komponen pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(context
                , notifikasi, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // membuat komponen notifikasi
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification;
        notification = builder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.smpn2tarik)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources()
                        , R.drawable.smpn2tarik))
                .setContentText(content)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifikasi, notification);
    }

}
