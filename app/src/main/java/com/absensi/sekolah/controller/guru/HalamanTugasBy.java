package com.absensi.sekolah.controller.guru;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.absensi.sekolah.R;
import com.absensi.sekolah.Splash;
import com.absensi.sekolah.config.Koneksi;
import com.absensi.sekolah.models.Tugas;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class HalamanTugasBy extends AppCompatActivity {
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
    static TextInputEditText tugas_dikerjakan_point, tugas_dikerjakan_text_balas;
    static TextView tv1,tv2,tv3,tv4,tv5;

    private static CardView lyCard;
    private static TextView tv1Card;
    private static MaterialButton sendCard;
    File uriFile;

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


        TextView toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText( getIntent().getStringExtra("tugas_title") );



        context = HalamanTugasBy.this;


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
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(HalamanTugasBy.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_guru_tugas_dikerjakan, null);
        mBottomSheetDialog = new BottomSheetDialog(HalamanTugasBy.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });

        editKey = bottomSheetLayout.findViewById(R.id.editKey);
        tugas_dikerjakan_point = bottomSheetLayout.findViewById(R.id.tugas_dikerjakan_point);
        tugas_dikerjakan_text_balas = bottomSheetLayout.findViewById(R.id.tugas_dikerjakan_text_balas);

        tv1 = bottomSheetLayout.findViewById(R.id.tv1);
        tv2 = bottomSheetLayout.findViewById(R.id.tv2);
        tv3 = bottomSheetLayout.findViewById(R.id.tv3);
        tv4 = bottomSheetLayout.findViewById(R.id.tv4);
        tv5 = bottomSheetLayout.findViewById(R.id.tv5);

        editKey.setText("");
        tugas_dikerjakan_point.setText("0");
        tugas_dikerjakan_text_balas.setText("");

        tv1.setText("");
        tv2.setText("");
        tv3.setText("");
        tv4.setText("");
        tv5.setText("");

        lyCard = bottomSheetLayout.findViewById(R.id.lyCard);
        tv1Card = bottomSheetLayout.findViewById(R.id.tv1Card);

        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());
        findViewById(R.id.fab_add).setVisibility(View.GONE);
        sendCard = bottomSheetLayout.findViewById(R.id.action_simpan);
        sendCard.setOnClickListener(view -> {
            new kirimTugasSync().execute();
        });

        getTugas();

    }



    private void getTugas(){
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        //String key = sharedpreferences.getString("uid","");
        String tugas_id = getIntent().getStringExtra("tugas_id");
        try {
            ArrayList<Tugas> items = new ArrayList<>();

            JSONObject j1 = koneksi.getDataServer("/api/tugasdikerjakan_byguru?key="+tugas_id);
            if( j1.getBoolean("success") ) {
                JSONArray response = j1.getJSONArray("response");
                for (int j = 0; j < response.length(); j++) {
                    JSONObject command = response.getJSONObject(j);

                    items.add(new Tugas(
                            command.getString("tugas_id"),
                            command.getString("tugas_tanggal"),
                            command.getString("tugas_title"),
                            command.getString("tugas_keterangan"),
                            command.getString("tugas_point"),
                            command.getString("guru_id"),
                            command.getString("guru_nama"),
                            command.getString("siswa_nik"),
                            command.getString("siswa_nama"),
                            command.getString("siswa_foto"),
                            command.getString("tugas_dikerjakan_id"),
                            command.getString("tugas_dikerjakan_text"),
                            command.getString("tugas_dikerjakan_text_balas"),
                            command.getString("tugas_dikerjakan_file"),
                            command.getString("tugas_dikerjakan_status"),
                            command.getString("tugas_dikerjakan_point"),
                            command.getString("tugas_dikerjakan_tanggal")
                    ));

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

            //sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            //String username = sharedpreferences.getString("username", "");
            String key = editKey.getText().toString();
            String dikerjakan_point = tugas_dikerjakan_point.getText().toString();
            String dikerjakan_text_balas = tugas_dikerjakan_text_balas.getText().toString();

            try {
                JSONObject j1 = koneksi.getDataServer(
                        "/api/tugasdikerjakan_simpan_byguru?"+
                                "dikerjakan_id="+key+
                                "&dikerjakan_point="+dikerjakan_point+
                                "&dikerjakan_text_balas="+dikerjakan_text_balas
                );
                if( j1.getBoolean("success") ) {

                    //JSONObject j2 = j1.getJSONObject("response");

                    //Log.e("a",j1.getString("response"));

                    //Toast.makeText(context,"Data berhasil dikirim/diperbarui!",Toast.LENGTH_SHORT).show();

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
        Context c;

        public RecyclerViewAdapter(List<Tugas> nilaiList, Context c) {
            this.nilaiList = nilaiList;
            this.c = c;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder holder;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tugas, parent, false);
            holder = new ItemViewHolder(view);
            return holder;

        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            final Tugas itemTugas = nilaiList.get(position);

            itemViewHolder.tv1.setText(itemTugas.siswa_nama);
            itemViewHolder.tv2.setText(itemTugas.tugas_dikerjakan_text);
            itemViewHolder.tv4.setText(itemTugas.tugas_dikerjakan_status);


            itemViewHolder.layoutAvatar.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(itemTugas.siswa_foto)){
                Picasso.with(itemViewHolder.itemView.getContext())
                        .load(itemTugas.siswa_foto)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(itemViewHolder.foto);
            }

            itemViewHolder.itemView.setOnClickListener(view -> {

                editKey.setText(itemTugas.tugas_dikerjakan_id);
                tugas_dikerjakan_point.setText(itemTugas.tugas_dikerjakan_point);
                tugas_dikerjakan_text_balas.setText(itemTugas.tugas_dikerjakan_text_balas);

                tv1.setText(itemTugas.tugas_tanggal);
                tv2.setText(itemTugas.tugas_title);
                tv3.setText("point: "+itemTugas.tugas_point);
                tv4.setText(itemTugas.tugas_keterangan);

                tv5.setText(itemTugas.tugas_dikerjakan_text);

                lyCard.setVisibility(View.GONE);
                sendCard.setText("Kembalikan");
                sendCard.setEnabled(true);
                if(!TextUtils.isEmpty(itemTugas.tugas_dikerjakan_file)){
                    sendCard.setText("Telah diperiksa");
                    sendCard.setEnabled(false);

                    lyCard.setVisibility(View.VISIBLE);
                    tv1Card.setText(itemTugas.tugas_dikerjakan_file);

                }
                mBottomSheetDialog.show();

            });
        }


        @Override
        public int getItemCount() {
            return nilaiList.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            private TextView tv1, tv2, tv3, tv4;
            CircleImageView foto;
            ImageButton action_detail, action_attachment, action_edit, action_del;
            RelativeLayout layoutAvatar;


            public ItemViewHolder(View itemView) {
                super(itemView);
                layoutAvatar = itemView.findViewById(R.id.layoutAvatar);
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