package com.absensi.sekolah.config;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.absensi.sekolah.services.AlarmBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.ALARM_SERVICE;

public class Koneksi {
    private static String url_server = "http://192.168.70.233/presensiv2e1-web";
    static Context context;

    public Koneksi(Context context){

        this.context = context;
    }

    public static JSONObject getDataServer(String requestPath) throws IOException, JSONException {
        Log.e("getDataServer",url_server + requestPath);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url( url_server + requestPath )
                .build();
        Response response = client.newCall(request).execute();
        String response_body = response.body().string();

        Log.e("response_body",response_body);

        return new JSONObject(response_body);

    }

    public static JSONObject setDataFileServer(String requestPath, byte[] byteArray) throws IOException, JSONException {
        Log.e("uploadDataServer",url_server + requestPath);

        OkHttpClient client = new OkHttpClient();


        /**
        Log.d("imagePath", imagePath);
        File file = new File(imagePath);
        RequestBody image = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imagePath, image)
                .build();*/


        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("capture", "filename.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();

        Request request = new Request.Builder()
                .url( url_server + requestPath )
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        String response_body = response.body().string();

        Log.e("response_body",response_body);

        return new JSONObject(response_body);

    }



    public static JSONObject setDataFileServer2(String requestPath, byte[] byteArray) throws IOException, JSONException {
        Log.e("uploadDataServer",url_server + requestPath);

        OkHttpClient client = new OkHttpClient();


        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "filename.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();

        Request request = new Request.Builder()
                .url( url_server + requestPath )
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        String response_body = response.body().string();

        Log.e("response_body",response_body);

        return new JSONObject(response_body);

    }

    public void policyAllow(){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public String getDeviceIMEI() {
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            String deviceId;

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                deviceId = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);

            }else {

                final TelephonyManager mTelephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

                if (mTelephony.getDeviceId() != null) {
                    deviceId = mTelephony.getDeviceId();
                } else {
                    deviceId = Settings.Secure.getString(
                            context.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }
            }

            return deviceId;
        }
        return null;
    }

    public static void createOneTimeExactAlarm() {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 10, intent, 0);
        AlarmManager alarmManager = null;
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);//kill any pre-existing alarms
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60000, pendingIntent);
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60000, pendingIntent);
            else
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60000, pendingIntent);
        }
    }

    public static Collection getThreadsByName(String threadName) {
        List<Thread> retval = new ArrayList<>();
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getName().equals(threadName))
                retval.add(thread);
        }
        return retval;
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().toLowerCase().equals("wifi"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().toLowerCase().equals("mobile"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static boolean serviceIsRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
