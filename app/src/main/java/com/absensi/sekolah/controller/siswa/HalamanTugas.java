package com.absensi.sekolah.controller.siswa;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.absensi.sekolah.config.FileUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class HalamanTugas extends AppCompatActivity {
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

    static AppCompatEditText editKey,editKey2,editFile,editText;
    static TextView tv1,tv2,tv3,tv4;

    Calendar myCalendar = Calendar.getInstance();
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private static int REQUEST_PERMISSION_GALLERY = 11101;
    private static int REQUEST_PERMISSION_CAMERA = 11102;
    private static int REQUEST_PERMISSION_FILE = 11103;

    private static CardView lyCard;
    private static TextView tv1Card;
    private static MaterialButton sendCard;
    File uriFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siswa_tugas);

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
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_siswa_tugas, null);
        mBottomSheetDialog = new BottomSheetDialog(HalamanTugas.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });

        editKey = bottomSheetLayout.findViewById(R.id.editKey);
        editKey2 = bottomSheetLayout.findViewById(R.id.editKey2);
        editFile = bottomSheetLayout.findViewById(R.id.editFile);
        editText = bottomSheetLayout.findViewById(R.id.editText);

        tv1 = bottomSheetLayout.findViewById(R.id.tv1);
        tv2 = bottomSheetLayout.findViewById(R.id.tv2);
        tv3 = bottomSheetLayout.findViewById(R.id.tv3);
        tv4 = bottomSheetLayout.findViewById(R.id.tv4);

        editKey.setText("");
        editKey2.setText("");
        editFile.setText("");
        editText.setText("");

        tv1.setText("");
        tv2.setText("");
        tv3.setText("");
        tv4.setText("");

        lyCard = bottomSheetLayout.findViewById(R.id.lyCard);
        tv1Card = bottomSheetLayout.findViewById(R.id.tv1Card);

        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());
        bottomSheetLayout.findViewById(R.id.fab_upload).setOnClickListener(view -> {

            showPictureDialog(view.getContext());

        });
        sendCard = bottomSheetLayout.findViewById(R.id.action_send);
        sendCard.setOnClickListener(view -> {

            String file = editFile.getText().toString();

            if(!TextUtils.isEmpty(file) ){
                new kirimTugas2Sync().execute();

            }else{
                new kirimTugasSync().execute();
            }

        });

        getTugas();

    }



    private void getTugas(){
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String nik = sharedpreferences.getString("username","");
        try {
            ArrayList<Tugas> items = new ArrayList<>();

            JSONObject j1 = koneksi.getDataServer("/api/tugas_bysiswa?nik="+nik);
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
                                command.getString("guru_nama"),
                                command.getString("tugas_dikerjakan_id"),
                                command.getString("tugas_dikerjakan_text"),
                                command.getString("tugas_dikerjakan_file"),
                                command.getString("tugas_dikerjakan_status"),
                                command.getString("tugas_dikerjakan_point"),
                                command.getString("tugas_dikerjakan_tanggal")
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

    private void showPictureDialog(Context context){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(context);
        pictureDialog.setTitle("Pilih Data Tugas dari:");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Photo Camera" };
        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            choosePhotoFromGallary();
                            break;
                        case 1:
                            takePhotoFromCamera();
                            break;
                        case 2:
                            chooseFileFromStorage();
                            break;
                    }
                });

        pictureDialog.show();//.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


    }


    public class kirimTugasSync extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            String username = sharedpreferences.getString("username", "");
            String tkid = editKey.getText().toString();
            String tid = editKey2.getText().toString();
            String text = editText.getText().toString();

            try {
                JSONObject j1 = koneksi.getDataServer(
                        "/api/tugas_simpan_bysiswa?nik="+username+
                                "&tkid="+tkid+
                                "&tid="+tid+
                                "&text="+text
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

            mBottomSheetDialog.dismiss();

            if(result){
                getTugas();

            }else{
            }

        }
        @Override
        protected void onPreExecute() {
            sweetAlertDialog.show();
        }
    }


    public class kirimTugas2Sync extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            String username = sharedpreferences.getString("username", "");
            String tkid = editKey.getText().toString();
            String tid = editKey2.getText().toString();
            String text = editText.getText().toString();
            String file = editFile.getText().toString();


            try {

                byte[] byteArraycapture = FileUtils.getBytesFromFile( new File(file) );

                JSONObject j1 = koneksi.setDataFileServer2(
                        "/api/tugas_simpan_bysiswa?nik="+username+
                                "&tkid="+tkid+
                                "&tid="+tid+
                                "&text="+text
                        ,byteArraycapture);

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

            mBottomSheetDialog.dismiss();

            if(result){

                sweetAlertDialog = new SweetAlertDialog(HalamanTugas.this, SweetAlertDialog.SUCCESS_TYPE);
                sweetAlertDialog.setTitleText("Berhasil");
                sweetAlertDialog.setContentText("Tugas berhasil dikirim.");
                sweetAlertDialog.showCancelButton(true);
                sweetAlertDialog.setConfirmText("Ya Baiklah!");
                sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.cancel());
                sweetAlertDialog.show();
                sweetAlertDialog.setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();

                    getTugas();

                });

                getTugas();

            }else{
                sweetAlertDialog = new SweetAlertDialog(HalamanTugas.this, SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitleText("Oops");
                sweetAlertDialog.setContentText("Gagal kirim tugas, kemungkinan sudah mengirimkan.");
                sweetAlertDialog.showCancelButton(true);
                sweetAlertDialog.setConfirmText("Ya Baiklah!");
                sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.cancel());
                sweetAlertDialog.show();
                sweetAlertDialog.setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();

                    getTugas();

                });
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

                String tanggal = nilaiList.get(position).tugas_tanggal;
                String tanggal_today = sdf.format(new Date());
                if(tanggal.equalsIgnoreCase(tanggal_today)){
                    tanggal+= " - hari ini";
                }

                itemHeaderViewHolder.tvHeader.setText(tanggal);

            }else {

                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

                final Tugas itemTugas = nilaiList.get(position);

                itemViewHolder.tv1.setText(itemTugas.tugas_tanggal);
                itemViewHolder.tv2.setText(itemTugas.tugas_title);
                itemViewHolder.tv4.setText(itemTugas.guru_nama);

                itemViewHolder.action_edit.setVisibility(View.VISIBLE);

                if(!TextUtils.isEmpty(itemTugas.tugas_dikerjakan_status)){
                    itemViewHolder.action_edit.setColorFilter(0xff43a047);
                }
                itemViewHolder.action_edit.setOnClickListener(view -> {

                    editKey.setText(itemTugas.tugas_dikerjakan_id);
                    editKey2.setText(itemTugas.tugas_id);
                    editFile.setText(itemTugas.tugas_dikerjakan_file);
                    editText.setText(itemTugas.tugas_dikerjakan_text);

                    tv1.setText(itemTugas.tugas_tanggal);
                    tv2.setText(itemTugas.tugas_title);

                    tv3.setVisibility(View.GONE);
                    if(!itemTugas.tugas_point.equalsIgnoreCase("0")){
                        tv3.setVisibility(View.VISIBLE);
                        tv3.setText(itemTugas.tugas_point);
                    }

                    tv4.setText(itemTugas.tugas_keterangan);

                    lyCard.setVisibility(View.GONE);
                    editFile.setText("");
                    sendCard.setText("Kirimkan");
                    if(!TextUtils.isEmpty(itemTugas.tugas_dikerjakan_file)){
                        sendCard.setText("Perbaharui");

                        lyCard.setVisibility(View.VISIBLE);
                        tv1Card.setText(itemTugas.tugas_dikerjakan_file);
                        //editFile.setText(itemTugas.tugas_dikerjakan_file);

                    }


                    mBottomSheetDialog.show();



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
            ImageButton action_attachment, action_edit, action_del;

            public ItemViewHolder(View itemView) {
                super(itemView);
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



    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, REQUEST_PERMISSION_GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_PERMISSION_CAMERA);
    }

    public void chooseFileFromStorage() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf|text/plain|application/msword|application/vnd.openxmlformats-officedocument.wordprocessingml.document|application/vnd.ms-powerpoint|application/vnd.openxmlformats-officedocument.presentationml.presentation|application/vnd.ms-excel|application/vnd.openxmlformats-officedocument.spreadsheetml.sheet|text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),REQUEST_PERMISSION_FILE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        lyCard.setVisibility(View.GONE);
        editFile.setText("");
        if (requestCode == REQUEST_PERMISSION_GALLERY && resultCode == RESULT_OK && data != null) {
            lyCard.setVisibility(View.VISIBLE);
            uriFile = new File(FileUtils.getPath(context, data.getData()));

            tv1Card.setText(uriFile.getName());
            editFile.setText(uriFile.getPath());


        } else if (requestCode == REQUEST_PERMISSION_CAMERA && resultCode == RESULT_OK && data != null) {
            lyCard.setVisibility(View.VISIBLE);
            uriFile = new File(FileUtils.getPath(context, data.getData()));

            tv1Card.setText(uriFile.getName());
            editFile.setText(uriFile.getPath());

        } else if (requestCode == REQUEST_PERMISSION_FILE && resultCode == RESULT_OK && data != null) {
            lyCard.setVisibility(View.VISIBLE);
            uriFile = new File(FileUtils.getPath(context, data.getData()));

            tv1Card.setText(uriFile.getName());
            editFile.setText(uriFile.getPath());

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