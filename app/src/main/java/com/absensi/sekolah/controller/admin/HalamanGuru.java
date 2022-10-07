package com.absensi.sekolah.controller.admin;

import android.content.Context;
import android.content.Intent;
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
import com.absensi.sekolah.controller.guru.Dashboard;
import com.absensi.sekolah.models.Guru;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HalamanGuru extends AppCompatActivity {
    static SweetAlertDialog sweetAlertDialog;


    static RecyclerViewAdapter adapter;
    static RecyclerView recyclerView;
    LinearLayout linierLayout1;
    static LinearLayout empty_view;
    static Context context;
    static Koneksi koneksi;


    static BottomSheetDialog mBottomSheetDialog;
    View bottomSheetLayout;

    static TextInputEditText tf_guru;
    static AutoCompleteTextView tf_jk;
    static TextInputEditText tf_notelp;
    static TextInputEditText tf_alamat;
    static AppCompatEditText editKey;
    static String jk;
    static SharedPreferences sharedpreferences;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_guru);

        linierLayout1 = findViewById(R.id.linierLayout1);
        recyclerView = findViewById(R.id.recycle_view);
        empty_view = findViewById(R.id.empty_view);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        context = HalamanGuru.this;


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
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_admin_guru, null);
        mBottomSheetDialog = new BottomSheetDialog(context);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });

        bottomSheetLayout.findViewById(R.id.action_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());


        editKey = bottomSheetLayout.findViewById(R.id.editKey);
        tf_guru = bottomSheetLayout.findViewById(R.id.tf_guru);
        tf_jk = bottomSheetLayout.findViewById(R.id.tf_jk);
        tf_notelp = bottomSheetLayout.findViewById(R.id.tf_notelp);
        tf_alamat = bottomSheetLayout.findViewById(R.id.tf_alamat);

        findViewById(R.id.fab_add).setOnClickListener(v->{
            editKey.setText("");
            tf_guru.setText("");
            tf_jk.setText("",false);
            tf_notelp.setText("");
            tf_alamat.setText("");

            mBottomSheetDialog.show();
        });

        ArrayAdapter autocompletetextAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, context.getResources().getStringArray(R.array.jk));
        tf_jk.setAdapter(autocompletetextAdapter);
        tf_jk.setOnItemClickListener((parent, view, position, id) -> {
            String str = (String) parent.getItemAtPosition(position);
            jk = str;
        });


        bottomSheetLayout.findViewById(R.id.action_simpan).setOnClickListener(v -> {
            new kirimPelajaranSync().execute();
        });

        getGuru();
    }


    private static void getGuru(){

        try {
            ArrayList<Guru> items = new ArrayList<>();

            JSONObject j1 = koneksi.getDataServer("/api/guru");
            if( j1.getBoolean("success") ) {
                JSONArray jsonArray = j1.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject command = jsonArray.getJSONObject(i);

                    items.add(new Guru(
                            command.getString("guru_id"),
                            command.getString("guru_nama"),
                            command.getString("guru_jenis_kelamin"),
                            command.getString("guru_alamat"),
                            command.getString("guru_no_telp"),
                            command.getString("guru_mapel")
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


    public class kirimPelajaranSync extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            String uid = sharedpreferences.getString("uid", "");
            String key = editKey.getText().toString();
            String guru = tf_guru.getText().toString();
            String notelp = tf_notelp.getText().toString();
            String alamat = tf_alamat.getText().toString();

            try {
                JSONObject j1 = koneksi.getDataServer("/api/guru_simpan_byadmin?uid="+uid+"&key="+key+"&guru="+guru+"&jk="+jk+"&notelp="+notelp+"&alamat="+alamat);
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
            getGuru();
        }
        @Override
        protected void onPreExecute() {
            sweetAlertDialog.show();
        }
    }


    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Guru> guruList;
        private static final int LAYOUT_CHILD= 1;
        Context c;

        public RecyclerViewAdapter(List<Guru> guruList, Context c) {
            this.guruList = guruList;
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

            final Guru itemGuru = guruList.get(position);

            itemViewHolder.tv1.setText(itemGuru.guru_no_telp);
            itemViewHolder.tv2.setText(itemGuru.guru_nama);
            itemViewHolder.tv4.setText(itemGuru.guru_alamat);


            itemViewHolder.action_edit.setVisibility(View.VISIBLE);
            itemViewHolder.action_edit.setOnClickListener(v -> {

                editKey.setText(itemGuru.guru_id);
                tf_guru.setText(itemGuru.guru_nama);

                tf_jk.setText(itemGuru.guru_jenis_kelamin,false);

                tf_notelp.setText(itemGuru.guru_no_telp);
                tf_alamat.setText(itemGuru.guru_alamat);

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
                        JSONObject j1 = koneksi.getDataServer("/api/guru_hapus_byadmin?key="+itemGuru.guru_id);
                        if( j1.getBoolean("success") ) {

                            getGuru();
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
            return guruList.size();
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