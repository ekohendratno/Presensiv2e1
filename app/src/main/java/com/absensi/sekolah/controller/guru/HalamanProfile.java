package com.absensi.sekolah.controller.guru;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import com.absensi.sekolah.R;
import com.absensi.sekolah.Splash;
import com.absensi.sekolah.config.Koneksi;
import com.absensi.sekolah.controller.guru.Dashboard;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HalamanProfile extends AppCompatActivity {
    static SweetAlertDialog sweetAlertDialog;

    static Context context;
    static Koneksi koneksi;

    static TextInputEditText tf_nama;
    static AutoCompleteTextView tf_jk;
    static TextInputEditText tf_alamat;
    static TextInputEditText tf_notelp;
    static TextInputEditText tf_password;
    static AppCompatEditText editKey;
    static String jk, agama;
    static SharedPreferences sharedpreferences;
    static String uid;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_guru);



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
        tf_nama = findViewById(R.id.tf_nama);
        tf_jk = findViewById(R.id.tf_jk);
        tf_alamat = findViewById(R.id.tf_alamat);
        tf_notelp = findViewById(R.id.tf_notelp);
        tf_notelp.setEnabled(false);
        tf_password = findViewById(R.id.tf_password);


        findViewById(R.id.actSimpan).setOnClickListener(v -> {
            new kirimSiswaSync().execute();
        });

        ArrayAdapter autocompletetextAdapterJK = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, context.getResources().getStringArray(R.array.jk));
        tf_jk.setAdapter(autocompletetextAdapterJK);
        tf_jk.setOnItemClickListener((parent, view, position, id) -> {
            String str = (String) parent.getItemAtPosition(position);
            jk = str;
        });

        getSiswa();

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void getSiswa(){

        try {

            JSONObject j1 = koneksi.getDataServer("/api/guru?key="+uid);
            if( j1.getBoolean("success") ) {
                JSONArray jsonArray = j1.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject command = jsonArray.getJSONObject(i);

                    editKey.setText(command.getString("guru_id"));
                    tf_nama.setText(command.getString("guru_nama"));
                    tf_alamat.setText(command.getString("guru_alamat"));
                    tf_jk.setText(command.getString("guru_jenis_kelamin"),false);

                    tf_notelp.setText(command.getString("guru_no_telp"));

                    tf_password.setText(command.getString("guru_password"));


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

        String uid;
        String key;
        String nama;
        String alamat;
        String notelp;
        String password;

        @Override
        protected Boolean doInBackground(String... params) {

            sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            uid = sharedpreferences.getString("uid", "");
            key = editKey.getText().toString();
            nama = tf_nama.getText().toString();
            alamat = tf_alamat.getText().toString();
            notelp = tf_notelp.getText().toString();
            password = tf_password.getText().toString();

            if(jk == null){
                jk = tf_jk.getText().toString();
            }

            try {
                JSONObject j1 = koneksi.getDataServer("/api/guru_simpan_byguru?uid="+uid+"&key="+key+
                        "&nama="+nama+
                        "&alamat="+alamat+
                        "&agama="+agama+
                        "&jk="+jk+
                        "&notelp="+notelp+
                        "&password="+password);
                if( j1.getBoolean("success") ) {

                    Log.e("a",j1.getString("response"));

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


            sharedpreferences = context.getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("password",password);
            editor.putString("nama",nama);
            editor.apply();

            startActivity( new Intent(context, com.absensi.sekolah.controller.guru.Dashboard.class) );
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

            startActivity( new Intent(context, Dashboard.class) );
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}