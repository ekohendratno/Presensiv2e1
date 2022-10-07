package com.absensi.sekolah.controller.guru;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.absensi.sekolah.controller.siswa.HalamanProfile;
import com.absensi.sekolah.models.Presensi;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

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

    private static BottomSheetDialog mBottomSheetDialog;
    private View bottomSheetLayout;
    private BottomSheetBehavior mBehavior;
    static SharedPreferences sharedpreferences;

    static AppCompatEditText editKey;
    static TextInputEditText tf_absensi_keterangan,tf_absensi_kelas;


    Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

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


        /**
         * BOTTOM SHEET ADD
         */
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(HalamanAbsensi.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_guru_absensi, null);
        mBottomSheetDialog = new BottomSheetDialog(HalamanAbsensi.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });
        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());


        editKey = bottomSheetLayout.findViewById(R.id.editKey);
        tf_absensi_keterangan = bottomSheetLayout.findViewById(R.id.tf_absensi_keterangan);
        tf_absensi_kelas = bottomSheetLayout.findViewById(R.id.tf_absensi_kelas);

        findViewById(R.id.fab_add).setOnClickListener(v->{
            editKey.setText("");
            tf_absensi_keterangan.setText("alfa");
            tf_absensi_kelas.setText("");

            mBottomSheetDialog.show();
        });

        bottomSheetLayout.findViewById(R.id.action_simpan).setOnClickListener(v -> {
            mBottomSheetDialog.dismiss();

            new kirimAbsensiSync().execute();


        });

        getPresensi();

    }



    private void getPresensi(){
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String key = sharedpreferences.getString("uid","");
        try {
            ArrayList<Presensi> items = new ArrayList<>();

            JSONObject j1 = koneksi.getDataServer("/api/presensigroup_byguru?key="+key);
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
                                command.getString("absensi_tanggal"),
                                command.getString("absensi_keterangan"),
                                command.getString("absensi_kelas"),
                                command.getString("absensi_jumlah")
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



    public class kirimAbsensiSync extends AsyncTask<String, String, Boolean> {
        String absensi_id = editKey.getText().toString();
        String keterangan = tf_absensi_keterangan.getText().toString();
        String kelas = tf_absensi_kelas.getText().toString();
        String tanggal = sdf.format(new Date());

        @Override
        protected Boolean doInBackground(String... params) {

            sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            String uid = sharedpreferences.getString("uid", "");

            try {
                JSONObject j1 = koneksi.getDataServer(
                        "/api/presensi_simpan_byguru?"+
                                "keterangan="+keterangan+
                                "&kelas="+kelas+
                                "&tanggal="+tanggal+
                                "&guru_id="+uid
                );
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
            sweetAlertDialog.dismissWithAnimation();

            if(result){

                getPresensi();

                mBottomSheetDialog.dismiss();


                Intent intent = new Intent(context, HalamanAbsensiBy.class);
                intent.putExtra("tanggal",tanggal);
                intent.putExtra("keterangan",keterangan);
                intent.putExtra("kelas",kelas);
                startActivity(intent);


            }else{
            }

        }
        @Override
        protected void onPreExecute() {
            sweetAlertDialog.show();
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

                itemHeaderViewHolder.tvHeader.setText(presensiList.get(position).absensi_tanggal);

            }else {

                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

                final Presensi itemPresensi = presensiList.get(position);

                itemViewHolder.tv1.setVisibility(View.GONE);
                itemViewHolder.tv1.setText(itemPresensi.absensi_keterangan);
                itemViewHolder.tv2.setText("kelas: "+itemPresensi.absensi_kelas + ", jumlah: " +itemPresensi.absensi_jumlah + " siswa");
                itemViewHolder.itemView.setOnClickListener(v->{
                    Intent intent = new Intent(v.getContext(), HalamanAbsensiBy.class);
                    intent.putExtra("tanggal",itemPresensi.absensi_tanggal);
                    intent.putExtra("keterangan",itemPresensi.absensi_keterangan);
                    intent.putExtra("kelas",itemPresensi.absensi_kelas);
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
            ImageButton action_detail, action_lokasi, action_edit, action_del;

            public ItemViewHolder(View itemView) {
                super(itemView);
                action_detail = itemView.findViewById(R.id.action_detail);
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