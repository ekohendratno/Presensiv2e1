package com.absensi.sekolah;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.absensi.sekolah.config.Koneksi;
import com.absensi.sekolah.controller.siswa.Dashboard;
import com.absensi.sekolah.services.MainService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    public static String TAG = "LoginActivity";

    String username, password;
    SweetAlertDialog sweetAlertDialog;
    Koneksi koneksi;
    static SharedPreferences sharedpreferences;
    String deviceId = null;
    static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestStoragePermission();

        setContentView(R.layout.activity_login);


        context = LoginActivity.this;

        koneksi = new Koneksi(LoginActivity.this);

        koneksi.policyAllow();

        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);


        final TextInputEditText editUsername = findViewById(R.id.editUsername);
        final TextInputEditText editPassword = findViewById(R.id.editPassword);

        final MaterialButton actSignIn = findViewById(R.id.actSignIn);
        actSignIn.setOnClickListener(v -> {
            username = editUsername.getText().toString();
            password = editPassword.getText().toString();

            if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                Toast.makeText(v.getContext(),"Username atau Sandi belum diisi!",Toast.LENGTH_LONG).show();
            }else{
                new getSignInSync().execute();
            }
        });



        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String username = sharedpreferences.getString("username", "");
        if(!TextUtils.isEmpty(username)){
            getDashboard();
        }

    }


    public class getSignInSync extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                JSONObject j1 = koneksi.getDataServer("/api/signin?u="+username+"&p="+password+"&deviceid="+deviceId);
                if( j1.getBoolean("success") ) {
                    JSONObject j2 = j1.getJSONObject("response");

                    sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("uid",j2.getString("uid"));
                    editor.putString("username",j2.getString("username"));
                    editor.putString("password",j2.getString("password"));
                    editor.putString("nama",j2.getString("nama"));
                    editor.putString("foto",j2.getString("foto"));
                    editor.putString("level",j2.getString("level"));
                    //editor.putString("imei",j2.getString("imei"));

                    if(j2.getString("level").equalsIgnoreCase("siswa")){
                        editor.putString("kelas",j2.getString("kelas"));
                    }

                    editor.apply();


                    return true;

                }else{
                    return false;
                }

            }catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            sweetAlertDialog.dismissWithAnimation();
            if(result){

                getDashboard();

            }else{

                sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Terjadi gangguan!")
                        .setContentText("Maaf akun atau device tidak cocok!!");
                sweetAlertDialog.setConfirmText("Baiklah!");
                sweetAlertDialog.setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                });

                sweetAlertDialog.setCancelText("Batal");
                sweetAlertDialog.showCancelButton(true);
                sweetAlertDialog.setCancelClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                    finish();
                    System.exit(0);
                });

                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
            }

        }
        @Override
        protected void onPreExecute() {
            if(!sweetAlertDialog.isShowing()) sweetAlertDialog.show();


            deviceId = new Koneksi(context).getDeviceIMEI();

            Log.e("deviceId",deviceId);

        }
    }

    private void getDashboard(){
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String level = sharedpreferences.getString("level", "");

        Intent intent = null;

        if(level.equalsIgnoreCase("admin")){
            intent = new Intent(LoginActivity.this, com.absensi.sekolah.controller.admin.Dashboard.class);

        }else if(level.equalsIgnoreCase("orangtua")){
            intent = new Intent(LoginActivity.this, com.absensi.sekolah.controller.orangtua.Dashboard.class);

        }else if(level.equalsIgnoreCase("guru")){
            intent = new Intent(LoginActivity.this, com.absensi.sekolah.controller.guru.Dashboard.class);

        }else if(level.equalsIgnoreCase("siswa")){
            intent = new Intent(LoginActivity.this, com.absensi.sekolah.controller.siswa.Dashboard.class);
        }

        if(!level.isEmpty()){

            startActivity(intent);
            finish();

        }


        if (!new Koneksi(getApplicationContext()).serviceIsRunning(MainService.class)) {
            Log.i(TAG, "Starting Service ...");
            //startService(new Intent(getApplicationContext(), MainService.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Membuat fungsi untuk akses ke perangkat
     */

    private void requestStoragePermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                Log.i("izin", "Semua izin telah disetujui!");
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // permission denied, block the feature.
                showSettingsDialog();
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Membutuhkan Izin");
        builder.setMessage("Beberapa fitur diperlukan untuk aplikasi ini. Kamu Setujui di pengaturan.");
        builder.setPositiveButton("KE PENGATURAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}