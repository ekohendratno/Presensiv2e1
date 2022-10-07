package com.absensi.sekolah.controller.guru;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.absensi.sekolah.R;
import com.absensi.sekolah.Splash;
import com.absensi.sekolah.config.Koneksi;
import com.absensi.sekolah.models.Presensi;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import info.androidhive.fontawesome.FontTextView;

public class HalamanAbsensiBy extends AppCompatActivity {
    SweetAlertDialog sweetAlertDialog;


    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    LinearLayout linierLayout1, empty_view;
    Context context;
    Koneksi koneksi;



    private static BottomSheetDialog mBottomSheetDialog;
    private View bottomSheetLayout;
    private BottomSheetBehavior mBehavior;
    static SharedPreferences sharedpreferences;

    static AppCompatEditText editKey;
    static TextInputEditText tf_absensi_keterangan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru_absensi);

        linierLayout1 = findViewById(R.id.linierLayout1);
        recyclerView = findViewById(R.id.recycle_view);
        empty_view = findViewById(R.id.empty_view);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        context = HalamanAbsensiBy.this;


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


        findViewById(R.id.fab_add).setVisibility(View.GONE);



        /**
         * BOTTOM SHEET ADD
         */
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(HalamanAbsensiBy.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_guru_absensi_byguru, null);
        mBottomSheetDialog = new BottomSheetDialog(HalamanAbsensiBy.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });
        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());

        editKey = bottomSheetLayout.findViewById(R.id.editKey);
        tf_absensi_keterangan = bottomSheetLayout.findViewById(R.id.tf_absensi_keterangan);
        findViewById(R.id.fab_add).setVisibility(View.GONE);
        /**bottomSheetLayout.findViewById(R.id.action_simpan).setOnClickListener(v -> {
            mBottomSheetDialog.dismiss();

            new kirimAbsensiBySync().execute();


        });*/


        getPresensi();

    }



    private void getPresensi(){
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String key = sharedpreferences.getString("uid","");
        String tanggal = getIntent().getStringExtra("tanggal");
        String keterangan = getIntent().getStringExtra("keterangan");
        String kelas = getIntent().getStringExtra("kelas");
        try {
            ArrayList<Presensi> items = new ArrayList<>();

            JSONObject j1 = koneksi.getDataServer("/api/presensi_byguru?key="+key+"&tanggal="+tanggal+"&keterangan="+keterangan+"&kelas="+kelas);
            if( j1.getBoolean("success") ) {
                JSONArray tanggalBy = j1.getJSONArray("response");
                for (int i = 0; i < tanggalBy.length(); i++) {
                    JSONObject command2 = tanggalBy.getJSONObject(i);

                    //items.add(new Presensi(
                            //command2.getString("tanggal")
                    //));

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



    public class kirimAbsensiBySync extends AsyncTask<String, String, Boolean> {
        String nilai_id = editKey.getText().toString();
        String absensi_keterangan = tf_absensi_keterangan.getText().toString();

        @Override
        protected Boolean doInBackground(String... params) {

            sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            String uid = sharedpreferences.getString("uid", "");

            try {
                JSONObject j1 = koneksi.getDataServer(
                        "/api/presensi_simpan_byguru?"+
                                "id="+nilai_id+
                                "&keterangan="+absensi_keterangan
                );
                if( j1.getBoolean("success") ) {

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
            sweetAlertDialog.dismissWithAnimation();

            if(result){

                getPresensi();


            }else{
            }

        }
        @Override
        protected void onPreExecute() {
            sweetAlertDialog.show();
        }
    }



    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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

                itemHeaderViewHolder.tvHeader.setText(presensiList.get(position).absensi_tanggal);

            }else {

                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

                final Presensi itemPresensi = presensiList.get(position);

                itemViewHolder.tv1.setText(itemPresensi.siswa_nama);
                itemViewHolder.tv2.setText(itemPresensi.absensi_keterangan);
                itemViewHolder.tv3.setVisibility(View.VISIBLE);
                itemViewHolder.tv3.setText(itemPresensi.absensi_jam);


                if(!TextUtils.isEmpty(itemPresensi.absensi_foto)){
                    Picasso.with(itemViewHolder.itemView.getContext())
                            .load(itemPresensi.absensi_foto)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(itemViewHolder.foto);
                }

                itemViewHolder.layoutAvatar.setVisibility(View.VISIBLE);
                itemViewHolder.action_lokasi.setOnClickListener(v->{
                    Intent intent = new Intent(v.getContext(), com.absensi.sekolah.controller.HalamanLokasi.class);
                    intent.putExtra("xlat",itemPresensi.absensi_lat);
                    intent.putExtra("xlong",itemPresensi.absensi_long);
                    intent.putExtra("dari","siswa");
                    v.getContext().startActivity(intent);
                });



                editKey.setText("");


                /**itemViewHolder.itemView.setOnClickListener(v->{
                    mBottomSheetDialog.show();

                    editKey.setText(itemPresensi.absensi_id);
                    tf_absensi_keterangan.setText(itemPresensi.absensi_keterangan);


                });*/


                holder.itemView.setOnClickListener(v->{
                    mBottomSheetDialog.show();

                    editKey.setText(itemPresensi.absensi_id);

                    FontTextView action_hadir_icon = bottomSheetLayout.findViewById(R.id.action_hadir_icon);
                    FontTextView action_alfa_icon = bottomSheetLayout.findViewById(R.id.action_alfa_icon);
                    FontTextView action_izin_icon = bottomSheetLayout.findViewById(R.id.action_izin_icon);
                    FontTextView action_sakit_icon = bottomSheetLayout.findViewById(R.id.action_sakit_icon);
                    FontTextView action_terlambat_icon = bottomSheetLayout.findViewById(R.id.action_terlambat_icon);

                    action_hadir_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));
                    action_alfa_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));
                    action_izin_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));
                    action_sakit_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));
                    action_terlambat_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));



                    if (itemPresensi.absensi_keterangan.equalsIgnoreCase("hadir")) {
                        action_hadir_icon.setTextColor(context.getResources().getColor(R.color.colorMasuk));

                    } else if (itemPresensi.absensi_keterangan.equalsIgnoreCase("alfa")) {
                        action_alfa_icon.setTextColor(context.getResources().getColor(R.color.colorAlfa));

                    } else if (itemPresensi.absensi_keterangan.equalsIgnoreCase("izin")) {
                        action_izin_icon.setTextColor(context.getResources().getColor(R.color.colorIzin));

                    } else if (itemPresensi.absensi_keterangan.equalsIgnoreCase("sakit")) {
                        action_sakit_icon.setTextColor(context.getResources().getColor(R.color.colorSakit));

                    } else if (itemPresensi.absensi_keterangan.equalsIgnoreCase("terlambat")) {
                        action_terlambat_icon.setTextColor(context.getResources().getColor(R.color.colorTerlambat));

                    }



                    bottomSheetLayout.findViewById(R.id.action_hadir).setOnClickListener(y->{
                        mBottomSheetDialog.dismiss();

                        editKey.setText(itemPresensi.absensi_id);
                        tf_absensi_keterangan.setText("hadir");
                        action_hadir_icon.setTextColor(context.getResources().getColor(R.color.colorMasuk));

                        new kirimAbsensiBySync().execute();

                    });
                    bottomSheetLayout.findViewById(R.id.action_alfa).setOnClickListener(y->{
                        mBottomSheetDialog.dismiss();

                        editKey.setText(itemPresensi.absensi_id);
                        tf_absensi_keterangan.setText("alfa");
                        action_alfa_icon.setTextColor(context.getResources().getColor(R.color.colorAlfa));

                        new kirimAbsensiBySync().execute();

                    });
                    bottomSheetLayout.findViewById(R.id.action_izin).setOnClickListener(y->{
                        mBottomSheetDialog.dismiss();

                        editKey.setText(itemPresensi.absensi_id);
                        tf_absensi_keterangan.setText("izin");
                        action_izin_icon.setTextColor(context.getResources().getColor(R.color.colorIzin));

                        new kirimAbsensiBySync().execute();

                    });
                    bottomSheetLayout.findViewById(R.id.action_sakit).setOnClickListener(y->{
                        mBottomSheetDialog.dismiss();

                        editKey.setText(itemPresensi.absensi_id);
                        tf_absensi_keterangan.setText("sakit");
                        action_sakit_icon.setTextColor(context.getResources().getColor(R.color.colorSakit));

                        new kirimAbsensiBySync().execute();

                    });
                    bottomSheetLayout.findViewById(R.id.action_terlambat).setOnClickListener(y->{
                        mBottomSheetDialog.dismiss();

                        editKey.setText(itemPresensi.absensi_id);
                        tf_absensi_keterangan.setText("terlambat");
                        action_terlambat_icon.setTextColor(context.getResources().getColor(R.color.colorTerlambat));

                        new kirimAbsensiBySync().execute();

                    });

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
            RelativeLayout layoutAvatar;

            public ItemViewHolder(View itemView) {
                super(itemView);
                layoutAvatar = itemView.findViewById(R.id.layoutAvatar);
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