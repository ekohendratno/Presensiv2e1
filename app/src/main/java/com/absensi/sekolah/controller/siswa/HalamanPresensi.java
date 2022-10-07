package com.absensi.sekolah.controller.siswa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import com.absensi.sekolah.R;
import com.absensi.sekolah.Splash;
import com.absensi.sekolah.config.Koneksi;
import com.absensi.sekolah.config.Location;
import com.absensi.sekolah.models.Pelajaran;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HalamanPresensi extends AppCompatActivity {
    SweetAlertDialog sweetAlertDialog;

    Context context;
    Koneksi koneksi;
    static SharedPreferences sharedpreferences;
    String jenis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siswa_presensi);

        context = HalamanPresensi.this;


        koneksi = new Koneksi(context);

        koneksi.policyAllow();


        jenis = getIntent().getStringExtra("jenis");

        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);
        //sweetAlertDialog.show();

        TextView txtNama = findViewById(R.id.txtNama);
        TextView txtNIK = findViewById(R.id.txtNIK);
        TextView txtLokasi = findViewById(R.id.txtLokasi);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String nama = sharedpreferences.getString("nama", "");
        String username = sharedpreferences.getString("username", "");

        txtNama.setText(nama);
        txtNIK.setText(username);
        txtLokasi.setText("");

        AppCompatImageView foto = findViewById(R.id.selfifoto);
        TextView selfifoto_text = findViewById(R.id.selfifoto_text);

        findViewById(R.id.actPresensi).setOnClickListener(v->{
            new kirimPresensiSync().execute();
        });

        findViewById(R.id.actBatal).setOnClickListener(v->{
            Intent intent = new Intent(context, com.absensi.sekolah.controller.siswa.Dashboard.class);
            startActivity(intent);
            finish();
        });

        if(jenis.equalsIgnoreCase("Absensi Masuk")){
            foto.setImageResource(R.drawable.checklist);
            selfifoto_text.setText("Absensi Masuk");
        }

        if(jenis.equalsIgnoreCase("Absensi Pulang")){
            foto.setImageResource(R.drawable.campus);
            selfifoto_text.setText("Absensi Pulang");

        }


    }


    public class kirimPresensiSync extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            String username = sharedpreferences.getString("username", "");
            String kelas = sharedpreferences.getString("kelas", "");

            try {
                JSONObject j1 = koneksi.getDataServer("/api/presensi_simpan_bysiswa?nik="+username+"&jenis="+jenis+"&kelas="+kelas);
                if( j1.getBoolean("success") ) {

                    //JSONObject j2 = j1.getJSONObject("response");
                    //Log.e("a",j1.getString("response"));

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

                sweetAlertDialog = new SweetAlertDialog(HalamanPresensi.this, SweetAlertDialog.SUCCESS_TYPE);
                sweetAlertDialog.setTitleText("Berhasil");
                sweetAlertDialog.setContentText("Berhasil presensi, Kamu sudah presensi.");
                sweetAlertDialog.showCancelButton(true);
                sweetAlertDialog.setConfirmText("Ya Baiklah!");
                sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.cancel());
                sweetAlertDialog.show();
                sweetAlertDialog.setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();

                    getBackDashboard();

                });
            }else{

                sweetAlertDialog = new SweetAlertDialog(HalamanPresensi.this, SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitleText("Oops");
                sweetAlertDialog.setContentText("Gagal presensi, kemungkinan sudah presensi.");
                sweetAlertDialog.showCancelButton(true);
                sweetAlertDialog.setConfirmText("Ya Baiklah!");
                sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.cancel());
                sweetAlertDialog.show();
                sweetAlertDialog.setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();

                    getBackDashboard();

                });
            }

        }
        @Override
        protected void onPreExecute() {
            sweetAlertDialog.show();
        }
    }

    private void getBackDashboard(){
        Intent intent = new Intent(context, com.absensi.sekolah.controller.siswa.HalamanAbsensi.class);
        startActivity(intent);
        finish();
    }

}