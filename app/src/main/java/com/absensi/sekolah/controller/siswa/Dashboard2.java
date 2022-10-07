package com.absensi.sekolah.controller.siswa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.absensi.sekolah.LoginActivity;
import com.absensi.sekolah.R;
import com.absensi.sekolah.Splash;
import com.absensi.sekolah.config.Koneksi;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Dashboard2 extends AppCompatActivity {
    SweetAlertDialog sweetAlertDialog;

    Context context;
    Koneksi koneksi;
    BottomSheetDialog mBottomSheetDialog;
    View bottomSheetLayout;
    MaterialButton action_presensi;

    static SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_siswa_dashboard);


        context = Dashboard2.this;


        koneksi = new Koneksi(context);

        koneksi.policyAllow();




        TextView tvNama = findViewById(R.id.tvNama);
        TextView tvNIK = findViewById(R.id.tvNIK);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String nama = sharedpreferences.getString("nama", "");
        String username = sharedpreferences.getString("username", "");


        tvNama.setText(nama);
        tvNIK.setText(username);




        findViewById(R.id.btn_action_logout).setOnClickListener(v -> {
            signOutComplete();
        });


        findViewById(R.id.m1).setOnClickListener(v -> {
            Intent intent = new Intent(context, HalamanGuru.class);
            startActivity(intent);
        });
        findViewById(R.id.m2).setOnClickListener(v -> {
            Intent intent = new Intent(context, HalamanPelajaran.class);
            startActivity(intent);
        });

        findViewById(R.id.m3).setOnClickListener(v -> {
            Intent intent = new Intent(context, HalamanNilai.class);
            startActivity(intent);
        });
        findViewById(R.id.m4).setOnClickListener(v -> {
            Intent intent = new Intent(context, HalamanTugas.class);
            startActivity(intent);
        });

        findViewById(R.id.m5).setOnClickListener(v -> {
            Intent intent = new Intent(context, HalamanAbsensi.class);
            startActivity(intent);
        });
        findViewById(R.id.m6).setOnClickListener(v -> {
            Intent intent = new Intent(context, HalamanProfile.class);
            startActivity(intent);
        });



        findViewById(R.id.btn_action_notif).setOnClickListener(v -> {
            startActivity(new Intent(v.getContext(), com.absensi.sekolah.controller.HalamanNotifikasi.class));
        });


        findViewById(R.id.main_row).setOnClickListener(v -> {
            Intent intent = new Intent(context, HalamanProfile.class);
            startActivity(intent);
        });



        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_siswa_dashboard, null);
        mBottomSheetDialog = new BottomSheetDialog(context);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });

        bottomSheetLayout.findViewById(R.id.action_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());

        findViewById(R.id.fab_add).setOnClickListener(v->{
            mBottomSheetDialog.show();
        });

        bottomSheetLayout.findViewById(R.id.presensiMasuk).setOnClickListener(v -> {

            Intent intent = new Intent(context, HalamanPresensi.class);
            intent.putExtra("jenis","Absensi Masuk");
            startActivity(intent);

        });

        bottomSheetLayout.findViewById(R.id.presensiPulang).setOnClickListener(v -> {

            Intent intent = new Intent(context, HalamanPresensi.class);
            intent.putExtra("jenis","Absensi Pulang");
            startActivity(intent);

        });


        /**
        action_presensi = bottomSheetLayout.findViewById(R.id.action_presensi);
        action_presensi.setEnabled(false);



        AutoCompleteTextView myAutocompleteTextView = bottomSheetLayout.findViewById(R.id.tf_jenis_presensi);
        ArrayAdapter autocompletetextAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, context.getResources().getStringArray(R.array.jenis_presensi));
        myAutocompleteTextView.setAdapter(autocompletetextAdapter);
        myAutocompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String str = (String) parent.getItemAtPosition(position);

            action_presensi.setEnabled(false);
            if(str.equalsIgnoreCase("Absensi Masuk") || str.equalsIgnoreCase("Absensi Pulang")){
                action_presensi.setEnabled(true);
            }

        });


        bottomSheetLayout.findViewById(R.id.action_presensi).setOnClickListener(v -> {


            AutoCompleteTextView tf_jenis_presensi = bottomSheetLayout.findViewById(R.id.tf_jenis_presensi);
            String jenis = tf_jenis_presensi.getText().toString();


            Intent intent = new Intent(context, com.absensi.sekolah.controller.siswa.HalamanPresensi.class);
            intent.putExtra("jenis",jenis);
            startActivity(intent);

        });*/


        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);
        //sweetAlertDialog.show();




        //getPresensi();


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
            editor.remove("imei");
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