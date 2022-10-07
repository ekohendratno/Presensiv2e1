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
import com.absensi.sekolah.models.Nilai;
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
import info.androidhive.fontawesome.FontTextView;

public class HalamanNilai extends AppCompatActivity {
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
    static TextInputEditText tf_nilai_keterangan,tf_nilai_kelas;

    Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru_nilai);

        linierLayout1 = findViewById(R.id.linierLayout1);
        recyclerView = findViewById(R.id.recycle_view);
        empty_view = findViewById(R.id.empty_view);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        context = HalamanNilai.this;


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
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(HalamanNilai.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_guru_nilai, null);
        mBottomSheetDialog = new BottomSheetDialog(HalamanNilai.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });
        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());


        editKey = bottomSheetLayout.findViewById(R.id.editKey);
        tf_nilai_keterangan = bottomSheetLayout.findViewById(R.id.tf_nilai_keterangan);
        tf_nilai_kelas = bottomSheetLayout.findViewById(R.id.tf_nilai_kelas);

        findViewById(R.id.fab_add).setOnClickListener(v->{
            editKey.setText("");
            tf_nilai_keterangan.setText("");
            tf_nilai_kelas.setText("");

            mBottomSheetDialog.show();
        });

        bottomSheetLayout.findViewById(R.id.action_simpan).setOnClickListener(v -> {
            mBottomSheetDialog.dismiss();

            new kirimNilaiSync().execute();


        });

        getNilai();

    }



    private void getNilai(){
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String key = sharedpreferences.getString("uid","");
        try {
            ArrayList<Nilai> items = new ArrayList<>();

            JSONObject j1 = koneksi.getDataServer("/api/nilaigroup_byguru?key="+key);
            if( j1.getBoolean("success") ) {
                JSONArray tanggal = j1.getJSONArray("response");
                for (int i = 0; i < tanggal.length(); i++) {
                    JSONObject command2 = tanggal.getJSONObject(i);

                    items.add(new Nilai(
                            command2.getString("tanggal")
                    ));

                    JSONArray tanggal_data = command2.getJSONArray("tanggal_data");
                    for (int j = 0; j < tanggal_data.length(); j++) {
                        JSONObject command = tanggal_data.getJSONObject(j);

                        items.add(new Nilai(
                                command.getString("nilai_tanggal"),
                                command.getString("nilai_keterangan"),
                                command.getString("nilai_kelas")
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



    public class kirimNilaiSync extends AsyncTask<String, String, Boolean> {
        String nilai_id = editKey.getText().toString();
        String nilai_keterangan = tf_nilai_keterangan.getText().toString();
        String nilai_kelas = tf_nilai_kelas.getText().toString();
        String tanggal = sdf.format(new Date());

        @Override
        protected Boolean doInBackground(String... params) {

            sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            String uid = sharedpreferences.getString("uid", "");

            try {
                JSONObject j1 = koneksi.getDataServer(
                        "/api/nilai_simpan_byguru?"+
                                "keterangan="+nilai_keterangan+
                                "&kelas="+nilai_kelas+
                                "&guru_id="+uid+
                                "&tanggal="+tanggal+
                                "&angka=0"+
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

                getNilai();

                mBottomSheetDialog.dismiss();


                Intent intent = new Intent(context, HalamanNilaiBy.class);
                intent.putExtra("nilai_tanggal",tanggal);
                intent.putExtra("nilai_keterangan",nilai_keterangan);
                intent.putExtra("nilai_kelas",nilai_kelas);
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
        private List<Nilai> nilaiList;
        private static final int LAYOUT_HEADER= 0;
        private static final int LAYOUT_CHILD= 1;
        Context c;

        public RecyclerViewAdapter(List<Nilai> nilaiList, Context c) {
            this.nilaiList = nilaiList;
            this.c = c;
        }

        @Override
        public int getItemViewType(int position) {
            if(nilaiList.get(position).isHeader()) {
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nilai, parent, false);
                holder = new ItemViewHolder(view);
            }
            return holder;

        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            if(holder.getItemViewType()== LAYOUT_HEADER){
                ItemHeaderViewHolder itemHeaderViewHolder = (ItemHeaderViewHolder) holder;

                itemHeaderViewHolder.tvHeader.setText(nilaiList.get(position).nilai_tanggal);

            }else {

                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

                final Nilai itemNilai = nilaiList.get(position);

                itemViewHolder.tv1.setVisibility(View.GONE);
                itemViewHolder.tv2.setText(itemNilai.nilai_keterangan);
                itemViewHolder.tv4.setText(itemNilai.nilai_kelas);

                itemViewHolder.itemView.setOnClickListener(v->{
                    Intent intent = new Intent(v.getContext(), HalamanNilaiBy.class);
                    intent.putExtra("nilai_tanggal",itemNilai.nilai_tanggal);
                    intent.putExtra("nilai_keterangan",itemNilai.nilai_keterangan);
                    intent.putExtra("nilai_kelas",itemNilai.nilai_kelas);
                    v.getContext().startActivity(intent);
                });

            }
        }


        @Override
        public int getItemCount() {
            return nilaiList.size();
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
            FontTextView img1;
            ImageButton action_edit, action_del;

            public ItemViewHolder(View itemView) {
                super(itemView);
                action_edit = itemView.findViewById(R.id.action_edit);
                action_del = itemView.findViewById(R.id.action_del);

                img1 = itemView.findViewById(R.id.img1);
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