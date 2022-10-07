package com.absensi.sekolah.controller.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.absensi.sekolah.R;
import com.absensi.sekolah.Splash;
import com.absensi.sekolah.config.Koneksi;
import com.absensi.sekolah.models.Guru;
import com.absensi.sekolah.models.Siswa;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HalamanSiswa extends AppCompatActivity {
    static SweetAlertDialog sweetAlertDialog;


    static RecyclerViewAdapter adapter;
    static RecyclerView recyclerView;
    LinearLayout linierLayout1;
    static LinearLayout empty_view;
    static Context context;
    static Koneksi koneksi;


    static BottomSheetDialog mBottomSheetDialog;
    View bottomSheetLayout;

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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_siswa);

        linierLayout1 = findViewById(R.id.linierLayout1);
        recyclerView = findViewById(R.id.recycle_view);
        empty_view = findViewById(R.id.empty_view);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        context = HalamanSiswa.this;


        koneksi = new Koneksi(context);

        koneksi.policyAllow();


        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();








        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_admin_siswa, null);
        mBottomSheetDialog = new BottomSheetDialog(context);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });

        bottomSheetLayout.findViewById(R.id.action_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());


        editKey = bottomSheetLayout.findViewById(R.id.editKey);
        tf_nik = bottomSheetLayout.findViewById(R.id.tf_nik);
        tf_nama = bottomSheetLayout.findViewById(R.id.tf_nama);
        tf_kelas = bottomSheetLayout.findViewById(R.id.tf_kelas);
        tf_alamat = bottomSheetLayout.findViewById(R.id.tf_alamat);
        tf_ttl = bottomSheetLayout.findViewById(R.id.tf_ttl);
        tf_agama = bottomSheetLayout.findViewById(R.id.tf_agama);
        tf_jk = bottomSheetLayout.findViewById(R.id.tf_jk);
        tf_notelp = bottomSheetLayout.findViewById(R.id.tf_notelp);
        tf_password = bottomSheetLayout.findViewById(R.id.tf_password);
        tf_nik_ortu = bottomSheetLayout.findViewById(R.id.tf_nik_ortu);

        findViewById(R.id.fab_add).setOnClickListener(v->{
            editKey.setText("");
            tf_nik.setText("");
            tf_nama.setText("");
            tf_kelas.setText("");
            tf_ttl.setText("");
            tf_jk.setText("",false);
            tf_agama.setText("",false);
            tf_notelp.setText("");
            tf_password.setText("");
            tf_nik_ortu.setText("");

            mBottomSheetDialog.show();
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


        bottomSheetLayout.findViewById(R.id.action_simpan).setOnClickListener(v -> {
            new kirimSiswaSync().execute();
        });

        getSiswa();
    }


    private static void getSiswa(){

        try {
            ArrayList<Siswa> items = new ArrayList<>();

            JSONObject j1 = koneksi.getDataServer("/api/siswa");
            if( j1.getBoolean("success") ) {
                JSONArray jsonArray = j1.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject command = jsonArray.getJSONObject(i);

                    items.add(new Siswa(
                            command.getString("siswa_nik"),
                            command.getString("siswa_nama"),
                            command.getString("siswa_kelas"),
                            command.getString("siswa_alamat"),
                            command.getString("siswa_ttl"),
                            command.getString("siswa_agama"),
                            command.getString("siswa_jenis_kelamin"),
                            command.getString("siswa_no_telp"),
                            command.getString("siswa_imei"),
                            command.getString("siswa_password"),
                            command.getString("orangtua_nik")
                    ));

                }

                empty_view.setVisibility(View.VISIBLE);
                if(items.size() > 0){
                    empty_view.setVisibility(View.GONE);
                }

                adapter = new RecyclerViewAdapter(items,context);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);

                sweetAlertDialog.dismissWithAnimation();

            }else{
                empty_view.setVisibility(View.VISIBLE);
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


            try {
                JSONObject j1 = koneksi.getDataServer("/api/siswa_simpan_byadmin?uid="+uid+"&key="+key+
                        "&nik="+nik+
                        "&nama="+nama+
                        "&kelas="+kelas+
                        "&alamat="+alamat+
                        "&ttl="+ttl+
                        "&agama="+agama+
                        "&jk="+jk+
                        "&notelp="+notelp+
                        "&imei="+
                        "&password="+password+
                        "&nik_ortu="+nik_ortu);
                if( j1.getBoolean("success") ) {

                    //JSONObject j2 = j1.getJSONObject("response");

                    //Log.e("a",j1.getString("response"));

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
            mBottomSheetDialog.dismiss();
            getSiswa();
        }
        @Override
        protected void onPreExecute() {
            sweetAlertDialog.show();
        }
    }


    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Siswa> siswaList;
        private static final int LAYOUT_CHILD= 1;
        Context c;

        public RecyclerViewAdapter(List<Siswa> siswaList, Context c) {
            this.siswaList = siswaList;
            this.c = c;
        }

        @Override
        public int getItemViewType(int position) {
            return LAYOUT_CHILD;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder holder;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guru, parent, false);
            holder = new ItemViewHolder(view);

            return holder;

        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {


            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            final Siswa itemSiswa = siswaList.get(position);

            itemViewHolder.tv1.setText(itemSiswa.siswa_kelas+", "+itemSiswa.siswa_nik+", "+itemSiswa.siswa_ttl);
            itemViewHolder.tv2.setText(itemSiswa.siswa_nama);
            itemViewHolder.tv3.setText(itemSiswa.siswa_ttl);
            itemViewHolder.tv3.setVisibility(View.GONE);
            itemViewHolder.tv4.setText(itemSiswa.siswa_alamat);


            itemViewHolder.action_edit.setVisibility(View.VISIBLE);
            itemViewHolder.action_edit.setOnClickListener(v -> {

                editKey.setText(itemSiswa.siswa_nik);

                tf_nik.setText(itemSiswa.siswa_nik);
                tf_nama.setText(itemSiswa.siswa_nama);
                tf_kelas.setText(itemSiswa.siswa_kelas);
                tf_ttl.setText(itemSiswa.siswa_ttl);
                tf_alamat.setText(itemSiswa.siswa_alamat);
                tf_agama.setText(itemSiswa.siswa_agama,false);
                tf_jk.setText(itemSiswa.siswa_jenis_kelamin,false);
                tf_notelp.setText(itemSiswa.siswa_no_telp);
                tf_password.setText(itemSiswa.siswa_password);
                tf_nik_ortu.setText(itemSiswa.nik_ortu);


                mBottomSheetDialog.show();
            });

            itemViewHolder.action_del.setVisibility(View.VISIBLE);
            itemViewHolder.action_del.setOnClickListener(v -> {

                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Anda yakin?")
                        .setContentText("Yakin mau hapus data? Semua yang terhubung dengan data ini akan terhapus semua.");
                sweetAlertDialog.setConfirmText("Yakin!");
                sweetAlertDialog.setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                    try {
                        JSONObject j1 = koneksi.getDataServer("/api/siswa_hapus_byadmin?key="+itemSiswa.siswa_nik);
                        if( j1.getBoolean("success") ) {

                            getSiswa();
                        }

                    }catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
                sweetAlertDialog.setCancelText("Batal");
                sweetAlertDialog.showCancelButton(true);
                sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismissWithAnimation());
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();


            });

        }


        @Override
        public int getItemCount() {
            return siswaList.size();
        }



        public class ItemViewHolder extends RecyclerView.ViewHolder {
            private TextView tv1, tv2, tv3, tv4;
            private ImageButton action_edit, action_del;

            public ItemViewHolder(View itemView) {
                super(itemView);

                action_edit = itemView.findViewById(R.id.action_edit);
                action_del = itemView.findViewById(R.id.action_del);

                tv1 = itemView.findViewById(R.id.tv1);
                tv2 = itemView.findViewById(R.id.tv2);
                tv3 = itemView.findViewById(R.id.tv3);
                tv4 = itemView.findViewById(R.id.tv4);


            }
        }
    }







    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


}