package com.absensi.sekolah.controller.siswa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.absensi.sekolah.LoginActivity;
import com.absensi.sekolah.R;
import com.absensi.sekolah.Splash;
import com.absensi.sekolah.config.Koneksi;
import com.absensi.sekolah.controller.admin.HalamanSiswa;
import com.absensi.sekolah.models.Pelajaran;
import com.absensi.sekolah.models.Siswa;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HalamanProfile extends AppCompatActivity {
    static SweetAlertDialog sweetAlertDialog;

    static Context context;
    static Koneksi koneksi;

    static TextInputEditText tf_nik;
    static TextInputEditText tf_nama;
    static TextInputEditText tf_kelas;
    static TextInputEditText tf_alamat;
    static TextInputEditText tf_ttl;
    static AutoCompleteTextView tf_jk,tf_agama;
    static TextInputEditText tf_notelp;
    static TextInputEditText tf_password;
    static TextInputEditText tf_nik_ortu;
    static AppCompatEditText editKey;
    static String jk, agama;
    static SharedPreferences sharedpreferences;
    static String uid;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_siswa);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = HalamanProfile.this;


        koneksi = new Koneksi(context);

        koneksi.policyAllow();


        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        uid = sharedpreferences.getString("uid", "");
        String username = sharedpreferences.getString("username", "");
        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();



        editKey = findViewById(R.id.editKey);
        tf_nik = findViewById(R.id.tf_nik);
        tf_nama = findViewById(R.id.tf_nama);
        tf_kelas = findViewById(R.id.tf_kelas);
        tf_alamat = findViewById(R.id.tf_alamat);
        tf_ttl = findViewById(R.id.tf_ttl);
        tf_agama = findViewById(R.id.tf_agama);
        tf_jk = findViewById(R.id.tf_jk);
        tf_notelp = findViewById(R.id.tf_notelp);
        tf_password = findViewById(R.id.tf_password);
        tf_nik_ortu = findViewById(R.id.tf_nik_ortu);


        findViewById(R.id.actSimpan).setOnClickListener(v -> {
            new kirimSiswaSync().execute();
        });

        ArrayAdapter autocompletetextAdapterJK = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, context.getResources().getStringArray(R.array.jk));
        tf_jk.setAdapter(autocompletetextAdapterJK);
        tf_jk.setOnItemClickListener((parent, view, position, id) -> {
            String str = (String) parent.getItemAtPosition(position);
            jk = str;
        });

        ArrayAdapter autocompletetextAdapterAgama = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, context.getResources().getStringArray(R.array.agama));
        tf_agama.setAdapter(autocompletetextAdapterAgama);
        tf_agama.setOnItemClickListener((parent, view, position, id) -> {
            String str = (String) parent.getItemAtPosition(position);
            agama = str;
        });

        getSiswa();

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void getSiswa(){

        try {

            JSONObject j1 = koneksi.getDataServer("/api/siswa?key="+uid);
            if( j1.getBoolean("success") ) {
                JSONArray jsonArray = j1.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject command = jsonArray.getJSONObject(i);

                    editKey.setText(command.getString("siswa_nik"));
                    tf_nik.setText(command.getString("siswa_nik"));
                    tf_nama.setText(command.getString("siswa_nama"));
                    tf_kelas.setText(command.getString("siswa_kelas"));
                    tf_alamat.setText(command.getString("siswa_alamat"));
                    tf_ttl.setText(command.getString("siswa_ttl"));

                    tf_agama.setText(command.getString("siswa_agama"),false);
                    tf_jk.setText(command.getString("siswa_jenis_kelamin"),false);

                    tf_notelp.setText(command.getString("siswa_no_telp"));

                    tf_password.setText(command.getString("siswa_password"));
                    tf_nik_ortu.setText(command.getString("orangtua_nik"));


                }
                sweetAlertDialog.dismissWithAnimation();

            }else{
                sweetAlertDialog.dismissWithAnimation();
            }



        } catch (IOException e) {
            e.printStackTrace();

            sweetAlertDialog.dismissWithAnimation();

        } catch (JSONException e) {
            e.printStackTrace();

            sweetAlertDialog.dismissWithAnimation();

        }

    }


    public class kirimSiswaSync extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            String uid = sharedpreferences.getString("uid", "");
            String key = editKey.getText().toString();
            String nik = tf_nik.getText().toString();
            String nama = tf_nama.getText().toString();
            String kelas = tf_kelas.getText().toString();
            String alamat = tf_alamat.getText().toString();
            String ttl = tf_ttl.getText().toString();
            String notelp = tf_notelp.getText().toString();
            String password = tf_password.getText().toString();
            String nik_ortu = tf_nik_ortu.getText().toString();

            if(agama == null){
                agama = tf_agama.getText().toString();
            }

            if(jk == null){
                jk = tf_jk.getText().toString();
            }

            try {
                JSONObject j1 = koneksi.getDataServer("/api/siswa_simpan_bysiswa?uid="+uid+"&key="+key+
                        "&nik="+nik+
                        "&nama="+nama+
                        "&kelas="+kelas+
                        "&alamat="+alamat+
                        "&ttl="+ttl+
                        "&agama="+agama+
                        "&jk="+jk+
                        "&notelp="+notelp+
                        "&password="+password+
                        "&nik_ortu="+nik_ortu);
                if( j1.getBoolean("success") ) {

                    JSONObject j2 = j1.getJSONObject("response");

                    Log.e("a",j1.getString("response"));




                    sharedpreferences = context.getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("password",j2.getString("siswa_password"));
                    editor.putString("nama",j2.getString("siswa_nama"));
                    editor.putString("imei",j2.getString("siswa_imei"));
                    editor.apply();

                    return true;

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
            sweetAlertDialog.dismiss();

            startActivity( new Intent(context, com.absensi.sekolah.controller.siswa.Dashboard.class) );
        }
        @Override
        protected void onPreExecute() {
            sweetAlertDialog.show();
        }
    }





    /**
    public void onBackPressed() {
        super.onBackPressed();
    }
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //onBackPressed();

            startActivity( new Intent(context, com.absensi.sekolah.controller.siswa.Dashboard.class) );
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}