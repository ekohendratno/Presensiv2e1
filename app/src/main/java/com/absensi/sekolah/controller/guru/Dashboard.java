package com.absensi.sekolah.controller.guru;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.absensi.sekolah.LoginActivity;
import com.absensi.sekolah.R;
import com.absensi.sekolah.Splash;
import com.absensi.sekolah.config.Koneksi;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Dashboard extends AppCompatActivity {
    static SweetAlertDialog sweetAlertDialog;


    static Context context;
    static Koneksi koneksi;
    static SharedPreferences sharedpreferences;

    String nama, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru_dashboard);

        context = Dashboard.this;


        koneksi = new Koneksi(context);

        koneksi.policyAllow();

        TextView tvNama = findViewById(R.id.tvNama);
        TextView tvNIK = findViewById(R.id.tvNIK);

        sharedpreferences = context.getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String nama = sharedpreferences.getString("nama", "");
        String username = sharedpreferences.getString("username", "");

        tvNama.setText(nama);
        tvNIK.setText(username);


        findViewById(R.id.btn_action_logout).setOnClickListener(v -> {
            signOutComplete();
        });

        findViewById(R.id.m3).setOnClickListener(v -> {
            Intent intent = new Intent(context, com.absensi.sekolah.controller.guru.HalamanNilai.class);
            startActivity(intent);
        });
        findViewById(R.id.m4).setOnClickListener(v -> {
            Intent intent = new Intent(context, com.absensi.sekolah.controller.guru.HalamanTugas.class);
            startActivity(intent);
        });

        findViewById(R.id.m5).setOnClickListener(v -> {
            Intent intent = new Intent(context, com.absensi.sekolah.controller.guru.HalamanAbsensi.class);
            startActivity(intent);
        });
        findViewById(R.id.m6).setOnClickListener(v -> {
            Intent intent = new Intent(context, com.absensi.sekolah.controller.guru.HalamanProfile.class);
            startActivity(intent);
        });




        findViewById(R.id.main_row).setOnClickListener(v -> {
            Intent intent = new Intent(context, HalamanProfile.class);
            startActivity(intent);
        });




        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);
        //sweetAlertDialog.show();


        Log.e("a","dash");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {


            finish();
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Ketuk sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    private void signOutComplete(){

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin?")
                .setContentText("Sesi akan kami akhiri segera!");
        sweetAlertDialog.setConfirmText("Keluar!");
        sweetAlertDialog.setConfirmClickListener(sDialog -> {
            sDialog.dismiss();


            sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("uid");
            editor.remove("username");
            editor.remove("password");
            editor.remove("nama");
            editor.remove("foto");
            editor.remove("level");
            editor.apply();

            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();


        });
        sweetAlertDialog.setCancelText("Batal");
        sweetAlertDialog.showCancelButton(true);
        sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismiss());
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

    }
}