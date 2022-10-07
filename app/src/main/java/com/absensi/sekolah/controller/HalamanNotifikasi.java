package com.absensi.sekolah.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.absensi.sekolah.LoginActivity;
import com.absensi.sekolah.R;
import com.absensi.sekolah.Splash;
import com.absensi.sekolah.config.Koneksi;
import com.absensi.sekolah.controller.admin.HalamanOrangTua;
import com.absensi.sekolah.models.Notif;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HalamanNotifikasi extends AppCompatActivity {
    static SweetAlertDialog sweetAlertDialog;


    static RecyclerViewAdapter adapter;
    static RecyclerView recyclerView;
    LinearLayout linierLayout1;
    static LinearLayout empty_view;
    static Context context;
    static Koneksi koneksi;
    static SharedPreferences sharedpreferences;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);


        this.context = HalamanNotifikasi.this;

        koneksi = new Koneksi(context);

        koneksi.policyAllow();




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedpreferences = context.getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String level = sharedpreferences.getString("level", "");
        String username = sharedpreferences.getString("username", "");



        linierLayout1 = findViewById(R.id.linierLayout1);
        recyclerView = findViewById(R.id.recycle_view);
        empty_view = findViewById(R.id.empty_view);

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



        try {
            ArrayList<Notif> items = new ArrayList<>();

            JSONObject j1 = koneksi.getDataServer("/api/notif_by?l="+level+"&u="+username);
            if( j1.getBoolean("success") ) {
                JSONArray jsonArray = j1.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject command = jsonArray.getJSONObject(i);

                    items.add(new Notif(
                            command.getString("notif_id"),
                            command.getString("notif_title"),
                            command.getString("notif_body"),
                            command.getString("notif_topik"),
                            command.getString("notif_tanggal")
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




    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Notif> notifList;
        private static final int LAYOUT_CHILD= 1;
        Context c;

        public RecyclerViewAdapter(List<Notif> notifList, Context c) {
            this.notifList = notifList;
            this.c = c;
        }

        @Override
        public int getItemViewType(int position) {
            return LAYOUT_CHILD;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder holder;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notif, parent, false);
            holder = new ItemViewHolder(view);

            return holder;

        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            final Notif itemNotif = notifList.get(position);

            itemViewHolder.tv1.setText(itemNotif.notif_tanggal);
            itemViewHolder.tv2.setText(itemNotif.notif_title);
            itemViewHolder.tv3.setText("");
            itemViewHolder.tv4.setText(itemNotif.notif_body);

        }


        @Override
        public int getItemCount() {
            return notifList.size();
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
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String level = sharedpreferences.getString("level", "");

        Intent intent = null;

        if(level.equalsIgnoreCase("admin")){
            intent = new Intent(HalamanNotifikasi.this, com.absensi.sekolah.controller.admin.Dashboard.class);

        }else if(level.equalsIgnoreCase("orangtua")){
            intent = new Intent(HalamanNotifikasi.this, com.absensi.sekolah.controller.orangtua.Dashboard.class);

        }else if(level.equalsIgnoreCase("guru")){
            intent = new Intent(HalamanNotifikasi.this, com.absensi.sekolah.controller.guru.Dashboard.class);

        }else if(level.equalsIgnoreCase("siswa")){
            intent = new Intent(HalamanNotifikasi.this, com.absensi.sekolah.controller.siswa.Dashboard.class);
        }

        if(!level.isEmpty()){

            startActivity(intent);
            finish();

        }
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
