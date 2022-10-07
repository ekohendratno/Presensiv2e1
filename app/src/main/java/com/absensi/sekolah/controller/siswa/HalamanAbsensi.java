package com.absensi.sekolah.controller.siswa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.absensi.sekolah.R;
import com.absensi.sekolah.Splash;
import com.absensi.sekolah.config.Koneksi;
import com.absensi.sekolah.models.Presensi;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class HalamanAbsensi extends AppCompatActivity {
    SweetAlertDialog sweetAlertDialog;


    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    LinearLayout linierLayout1, empty_view;
    Context context;
    Koneksi koneksi;

    static SharedPreferences sharedpreferences;
    Calendar myCalendar = Calendar.getInstance();
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siswa_absensi);

        linierLayout1 = findViewById(R.id.linierLayout1);
        recyclerView = findViewById(R.id.recycle_view);
        empty_view = findViewById(R.id.empty_view);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        context = HalamanAbsensi.this;


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


        getPresensi();

    }



    private void getPresensi(){
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String nik = sharedpreferences.getString("username","");
        try {
            ArrayList<Presensi> items = new ArrayList<>();

            JSONObject j1 = koneksi.getDataServer("/api/presensi_bysiswa?nik="+nik);
            if( j1.getBoolean("success") ) {
                JSONArray tanggal = j1.getJSONArray("response");
                for (int i = 0; i < tanggal.length(); i++) {
                    JSONObject command2 = tanggal.getJSONObject(i);

                    items.add(new Presensi(
                            command2.getString("tanggal")
                    ));

                    JSONArray tanggal_data = command2.getJSONArray("tanggal_data");
                    for (int j = 0; j < tanggal_data.length(); j++) {
                        JSONObject command = tanggal_data.getJSONObject(j);

                        items.add(new Presensi(
                                command.getString("absensi_id"),
                                command.getString("siswa_nik"),
                                command.getString("absensi_tanggal"),
                                command.getString("absensi_keterangan"),
                                command.getString("absensi_lat"),
                                command.getString("absensi_long"),
                                command.getString("absensi_jam"),
                                command.getString("absensi_foto"),
                                command.getString("siswa_nama")
                        ));

                    }

                }

                empty_view.setVisibility(View.VISIBLE);
                if(items.size() > 0){
                    empty_view.setVisibility(View.GONE);
                }

                sweetAlertDialog.dismiss();


                adapter = new RecyclerViewAdapter(items,context);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);


            }else{
                empty_view.setVisibility(View.VISIBLE);
                sweetAlertDialog.dismiss();
            }



        } catch (IOException e) {
            e.printStackTrace();

            sweetAlertDialog.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();

            sweetAlertDialog.dismiss();

        }
    }




    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Presensi> presensiList;
        private static final int LAYOUT_HEADER= 0;
        private static final int LAYOUT_CHILD= 1;
        Context c;

        public RecyclerViewAdapter(List<Presensi> presensiList, Context c) {
            this.presensiList = presensiList;
            this.c = c;
        }

        @Override
        public int getItemViewType(int position) {
            if(presensiList.get(position).isHeader()) {
                return LAYOUT_HEADER;
            }else
                return LAYOUT_CHILD;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder holder;
            if(viewType==LAYOUT_HEADER){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
                holder = new ItemHeaderViewHolder(view);
            }else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_presensi, parent, false);
                holder = new ItemViewHolder(view);
            }
            return holder;

        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            if(holder.getItemViewType()== LAYOUT_HEADER){
                ItemHeaderViewHolder itemHeaderViewHolder = (ItemHeaderViewHolder) holder;

                String tanggal = presensiList.get(position).absensi_tanggal;
                String tanggal_today = sdf.format(new Date());
                if(tanggal.equalsIgnoreCase(tanggal_today)){
                    tanggal+= " - hari ini";
                }

                itemHeaderViewHolder.tvHeader.setText(tanggal);

            }else {

                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

                final Presensi itemPresensi = presensiList.get(position);

                itemViewHolder.tv1.setText(itemPresensi.absensi_keterangan);
                itemViewHolder.tv2.setText(itemPresensi.absensi_jam);


                itemViewHolder.action_lokasi.setOnClickListener(v->{
                    Intent intent = new Intent(v.getContext(), com.absensi.sekolah.controller.HalamanLokasi.class);
                    intent.putExtra("xlat",itemPresensi.absensi_lat);
                    intent.putExtra("xlong",itemPresensi.absensi_long);
                    intent.putExtra("dari","siswa");
                    v.getContext().startActivity(intent);
                });
            }
        }


        @Override
        public int getItemCount() {
            return presensiList.size();
        }

        class ItemHeaderViewHolder extends RecyclerView.ViewHolder{

            TextView tvHeader;
            public ItemHeaderViewHolder(View itemView) {
                super(itemView);

                tvHeader = itemView.findViewById(R.id.tv_date);
            }

        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            private TextView tv1, tv2, tv3, tv4;
            CircleImageView foto;
            ImageButton action_lokasi, action_edit, action_del;

            public ItemViewHolder(View itemView) {
                super(itemView);
                action_lokasi = itemView.findViewById(R.id.action_lokasi);
                action_edit = itemView.findViewById(R.id.action_edit);
                action_del = itemView.findViewById(R.id.action_del);

                tv1 = itemView.findViewById(R.id.tv1);
                tv2 = itemView.findViewById(R.id.tv2);
                tv3 = itemView.findViewById(R.id.tv3);
                tv4 = itemView.findViewById(R.id.tv4);
                foto = itemView.findViewById(R.id.foto);


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