package com.absensi.sekolah.controller.guru;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.absensi.sekolah.R;
import com.absensi.sekolah.Splash;
import com.absensi.sekolah.config.Koneksi;
import com.absensi.sekolah.models.Tugas;
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

public class HalamanTugas extends AppCompatActivity {
    static SweetAlertDialog sweetAlertDialog;


    static RecyclerViewAdapter adapter;
    static RecyclerView recyclerView;
    LinearLayout linierLayout1;
    static LinearLayout empty_view;
    static Context context;
    static Koneksi koneksi;

    private static BottomSheetDialog mBottomSheetDialog;
    private View bottomSheetLayout;
    private BottomSheetBehavior mBehavior;
    static SharedPreferences sharedpreferences;

    static AppCompatEditText editKey;
    static TextInputEditText tf_tugas_title,tf_tugas_keterangan, tf_tugas_point, tf_tugas_kelas, tf_tugas_terlambat;

    Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru_tugas);

        linierLayout1 = findViewById(R.id.linierLayout1);
        recyclerView = findViewById(R.id.recycle_view);
        empty_view = findViewById(R.id.empty_view);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        context = HalamanTugas.this;


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
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(HalamanTugas.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_guru_tugas, null);
        mBottomSheetDialog = new BottomSheetDialog(HalamanTugas.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });

        editKey = bottomSheetLayout.findViewById(R.id.editKey);
        tf_tugas_title = bottomSheetLayout.findViewById(R.id.tf_tugas_title);
        tf_tugas_keterangan = bottomSheetLayout.findViewById(R.id.tf_tugas_keterangan);
        tf_tugas_point = bottomSheetLayout.findViewById(R.id.tf_tugas_point);
        tf_tugas_kelas = bottomSheetLayout.findViewById(R.id.tf_tugas_kelas);
        tf_tugas_terlambat = bottomSheetLayout.findViewById(R.id.tf_tugas_terlambat);

        tf_tugas_terlambat.setText(sdf.format(new Date()));
        tf_tugas_terlambat.setOnClickListener(v->{

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tf_tugas_terlambat.setText(sdf.format(myCalendar.getTime()));
            };

            new DatePickerDialog(
                    context,
                    date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();

        });

        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());

        findViewById(R.id.fab_add).setOnClickListener(v->{

            editKey.setText("");
            tf_tugas_title.setText("");
            tf_tugas_keterangan.setText("");
            tf_tugas_point.setText("");
            tf_tugas_kelas.setText("");
            tf_tugas_terlambat.setText("");

            mBottomSheetDialog.show();
        });



        bottomSheetLayout.findViewById(R.id.action_simpan).setOnClickListener(v -> {
            new kirimTugasSync().execute();
        });



        getTugas();

    }



    private static void getTugas(){
        sharedpreferences = context.getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String key = sharedpreferences.getString("uid","");
        try {
            ArrayList<Tugas> items = new ArrayList<>();

            JSONObject j1 = koneksi.getDataServer("/api/tugas_byguru?key="+key);
            if( j1.getBoolean("success") ) {
                JSONArray tanggal = j1.getJSONArray("response");
                for (int i = 0; i < tanggal.length(); i++) {
                    JSONObject command2 = tanggal.getJSONObject(i);

                    items.add(new Tugas(
                            command2.getString("tanggal")
                    ));

                    JSONArray tanggal_data = command2.getJSONArray("tanggal_data");
                    for (int j = 0; j < tanggal_data.length(); j++) {
                        JSONObject command = tanggal_data.getJSONObject(j);

                        items.add(new Tugas(
                                command.getString("tugas_id"),
                                command.getString("tugas_tanggal"),
                                command.getString("tugas_title"),
                                command.getString("tugas_keterangan"),
                                command.getString("tugas_point"),
                                command.getString("tugas_kelas"),
                                command.getString("tugas_terlambat"),
                                ""
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


    public class kirimTugasSync extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            String uid = sharedpreferences.getString("uid", "");
            String tugas_id = editKey.getText().toString();
            String tugas_title = tf_tugas_title.getText().toString();
            String tugas_keterangan = tf_tugas_keterangan.getText().toString();
            String tugas_kelas = tf_tugas_kelas.getText().toString();
            String tugas_terlambat = tf_tugas_terlambat.getText().toString();
            String tugas_point = tf_tugas_point.getText().toString();

            try {
                JSONObject j1 = koneksi.getDataServer(
                        "/api/tugas_simpan_byguru?"+
                                "tugas_id="+tugas_id+
                                "&tugas_title="+tugas_title+
                                "&tugas_keterangan="+tugas_keterangan+
                                "&tugas_kelas="+tugas_kelas+
                                "&tugas_terlambat="+tugas_terlambat+
                                "&tugas_point="+tugas_point+
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
                getTugas();

                mBottomSheetDialog.dismiss();

            }else{
            }

        }
        @Override
        protected void onPreExecute() {
            sweetAlertDialog.show();
        }
    }


    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Tugas> nilaiList;
        private static final int LAYOUT_HEADER= 0;
        private static final int LAYOUT_CHILD= 1;
        Context c;

        public RecyclerViewAdapter(List<Tugas> nilaiList, Context c) {
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tugas, parent, false);
                holder = new ItemViewHolder(view);
            }
            return holder;

        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            if(holder.getItemViewType()== LAYOUT_HEADER){
                ItemHeaderViewHolder itemHeaderViewHolder = (ItemHeaderViewHolder) holder;

                itemHeaderViewHolder.tvHeader.setText(nilaiList.get(position).tugas_tanggal);

            }else {

                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

                final Tugas itemTugas = nilaiList.get(position);

                itemViewHolder.tv1.setText(itemTugas.tugas_tanggal +" - "+itemTugas.tugas_terlambat);
                itemViewHolder.tv2.setText(itemTugas.tugas_title);
                itemViewHolder.tv4.setText("kelas "+itemTugas.tugas_kelas +", point: " +itemTugas.tugas_point +" ");

                itemViewHolder.itemView.setOnClickListener(v->{
                    Intent intent = new Intent(v.getContext(), HalamanTugasBy.class);
                    intent.putExtra("tugas_id",itemTugas.tugas_id);
                    intent.putExtra("tugas_title",itemTugas.tugas_title);
                    v.getContext().startActivity(intent);
                });

                itemViewHolder.action_edit.setVisibility(View.VISIBLE);
                itemViewHolder.action_edit.setOnClickListener(view -> {

                    editKey.setText(itemTugas.tugas_id);
                    tf_tugas_title.setText(itemTugas.tugas_title);
                    tf_tugas_keterangan.setText(itemTugas.tugas_keterangan);
                    tf_tugas_point.setText(itemTugas.tugas_point);
                    tf_tugas_kelas.setText(itemTugas.tugas_kelas);
                    tf_tugas_terlambat.setText(itemTugas.tugas_terlambat);

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
                            JSONObject j1 = koneksi.getDataServer("/api/tugas_hapus_byguru?key="+itemTugas.tugas_id);
                            if( j1.getBoolean("success") ) {

                                getTugas();
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
            CircleImageView foto;
            ImageButton action_detail, action_attachment, action_edit, action_del;

            public ItemViewHolder(View itemView) {
                super(itemView);
                action_detail = itemView.findViewById(R.id.action_detail);
                action_attachment = itemView.findViewById(R.id.action_attachment);
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