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
import com.absensi.sekolah.models.OrangTua;
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

public class HalamanOrangTua extends AppCompatActivity {
    static SweetAlertDialog sweetAlertDialog;


    static RecyclerViewAdapter adapter;
    static RecyclerView recyclerView;
    LinearLayout linierLayout1;
    static LinearLayout empty_view;
    static Context context;
    static Koneksi koneksi;


    static BottomSheetDialog mBottomSheetDialog;
    View bottomSheetLayout;

    static TextInputEditText tf_nama;
    static TextInputEditText tf_notelp;
    static TextInputEditText tf_password;
    static TextInputEditText tf_nik_ortu;
    static AppCompatEditText editKey;
    static SharedPreferences sharedpreferences;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orangtua);

        linierLayout1 = findViewById(R.id.linierLayout1);
        recyclerView = findViewById(R.id.recycle_view);
        empty_view = findViewById(R.id.empty_view);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        context = HalamanOrangTua.this;


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
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_admin_orangtua, null);
        mBottomSheetDialog = new BottomSheetDialog(context);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });

        bottomSheetLayout.findViewById(R.id.action_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());


        editKey = bottomSheetLayout.findViewById(R.id.editKey);
        tf_nama = bottomSheetLayout.findViewById(R.id.tf_nama);
        tf_notelp = bottomSheetLayout.findViewById(R.id.tf_notelp);
        tf_password = bottomSheetLayout.findViewById(R.id.tf_password);
        tf_nik_ortu = bottomSheetLayout.findViewById(R.id.tf_nik_ortu);

        findViewById(R.id.fab_add).setOnClickListener(v->{
            editKey.setText("");
            tf_nama.setText("");
            tf_notelp.setText("");
            tf_password.setText("");
            tf_nik_ortu.setText("");

            mBottomSheetDialog.show();
        });

        bottomSheetLayout.findViewById(R.id.action_simpan).setOnClickListener(v -> {
            new kirimSiswaSync().execute();
        });

        getOrangTua();
    }


    private static void getOrangTua(){

        try {
            ArrayList<OrangTua> items = new ArrayList<>();

            JSONObject j1 = koneksi.getDataServer("/api/orangtua");
            if( j1.getBoolean("success") ) {
                JSONArray jsonArray = j1.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject command = jsonArray.getJSONObject(i);

                    items.add(new OrangTua(
                            command.getString("orangtua_id"),
                            command.getString("orangtua_nama"),
                            command.getString("orangtua_nik"),
                            command.getString("orangtua_no_telp"),
                            command.getString("orangtua_password")
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
            String nama = tf_nama.getText().toString();
            String notelp = tf_notelp.getText().toString();
            String password = tf_password.getText().toString();
            String nik_ortu = tf_nik_ortu.getText().toString();


            try {
                JSONObject j1 = koneksi.getDataServer("/api/orangtua_simpan_byadmin?uid="+uid+"&key="+key+
                        "&nama="+nama+
                        "&notelp="+notelp+
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
            getOrangTua();
        }
        @Override
        protected void onPreExecute() {
            sweetAlertDialog.show();
        }
    }


    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<OrangTua> orangTuaList;
        private static final int LAYOUT_CHILD= 1;
        Context c;

        public RecyclerViewAdapter(List<OrangTua> orangTuaList, Context c) {
            this.orangTuaList = orangTuaList;
            this.c = c;
        }

        @Override
        public int getItemViewType(int position) {
            return LAYOUT_CHILD;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder holder;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orangtua, parent, false);
            holder = new ItemViewHolder(view);

            return holder;

        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {


            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            final OrangTua itemOrangTua = orangTuaList.get(position);

            itemViewHolder.tv1.setText(itemOrangTua.orangtua_nama);
            itemViewHolder.tv2.setText(itemOrangTua.orangtua_no_telp);
            itemViewHolder.tv3.setText(itemOrangTua.orangtua_nik);


            itemViewHolder.action_edit.setVisibility(View.VISIBLE);
            itemViewHolder.action_edit.setOnClickListener(v -> {

                editKey.setText(itemOrangTua.orangtua_id);

                tf_nama.setText(itemOrangTua.orangtua_nama);
                tf_notelp.setText(itemOrangTua.orangtua_no_telp);
                tf_password.setText(itemOrangTua.orangtua_password);
                tf_nik_ortu.setText(itemOrangTua.orangtua_nik);


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
                        JSONObject j1 = koneksi.getDataServer("/api/orangtua_hapus_byadmin?key="+itemOrangTua.orangtua_id);
                        if( j1.getBoolean("success") ) {

                            getOrangTua();
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
            return orangTuaList.size();
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