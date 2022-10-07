package com.absensi.sekolah.controller.orangtua;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.absensi.sekolah.LoginActivity;
import com.absensi.sekolah.R;
import com.absensi.sekolah.Splash;
import com.absensi.sekolah.config.Koneksi;
import com.absensi.sekolah.config.NonSwipeableViewPager;
import com.absensi.sekolah.controller.guru.HalamanPelajaranDetail;
import com.absensi.sekolah.controller.orangtua.adapters.AdapterAkumulasi1;
import com.absensi.sekolah.controller.orangtua.adapters.AdapterAkumulasi2;
import com.absensi.sekolah.models.Pelajaran;
import com.absensi.sekolah.models.Presensi;
import com.absensi.sekolah.models.Tugas;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import biz.laenger.android.vpbs.BottomSheetUtils;
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity {
    static SweetAlertDialog sweetAlertDialog;


    static Context context;
    static Koneksi koneksi;
    static SharedPreferences sharedpreferences;
    String nama;
    static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orangtua_dashboard);


        context = Dashboard.this;


        koneksi = new Koneksi(context);

        koneksi.policyAllow();



        TextView tvNama = findViewById(R.id.tvNama);
        TextView tvNIK = findViewById(R.id.tvNIK);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        nama = sharedpreferences.getString("nama", "");
        username = sharedpreferences.getString("username", "");

        tvNama.setText(nama);
        tvNIK.setText(username);


        TabLayout bottomSheetTabLayout = findViewById(R.id.tabLayoutX);
        NonSwipeableViewPager bottomSheetViewPager = findViewById(R.id.viewPagerX);
        DialogFragment.SimplePagerAdapter dialogFragment = new DialogFragment.SimplePagerAdapter(this);

        //bottomSheetViewPager.setOffscreenPageLimit(1);
        bottomSheetViewPager.setAdapter(dialogFragment);
        bottomSheetTabLayout.setupWithViewPager(bottomSheetViewPager);
        BottomSheetUtils.setupViewPager(bottomSheetViewPager);

        findViewById(R.id.btn_action_logout).setOnClickListener(v -> {
            signOutComplete();
        });

        findViewById(R.id.btn_action_notif).setOnClickListener(v -> {
            startActivity(new Intent(v.getContext(), com.absensi.sekolah.controller.HalamanNotifikasi.class));
        });
        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }




    RecyclerView recyclerView1,recyclerView2;
    LinearLayout empty_view1,empty_view2;


    static ArrayList<Presensi> items1 = new ArrayList<>();
    static ArrayList<Tugas> items2 = new ArrayList<>();

    public static class DialogFragment extends ViewPagerBottomSheetDialogFragment {


        public static class SimplePagerAdapter extends PagerAdapter {

            private Context context;
            SimplePagerAdapter(Context context){
                this.context=context;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return object == view;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String title = null;
                if (position == 0){
                    title = "Absensi";
                }else if (position == 1){
                    title = "Tugas";
                }
                return title;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position){
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View ly1 = layoutInflater.inflate(R.layout.activity_orangtua_absensi,container,false);
                View ly2 = layoutInflater.inflate(R.layout.activity_orangtua_tugas,container,false);



                RecyclerView recyclerView1 = ly1.findViewById(R.id.recycle_view);
                RecyclerView recyclerView2 = ly2.findViewById(R.id.recycle_view);

                LinearLayout empty_view1 = ly1.findViewById(R.id.empty_view);
                LinearLayout empty_view2 = ly2.findViewById(R.id.empty_view);

                LinearLayoutManager layoutManager1 = new LinearLayoutManager(context);
                recyclerView1.setLayoutManager(layoutManager1);

                LinearLayoutManager layoutManager2 = new LinearLayoutManager(context);
                recyclerView2.setLayoutManager(layoutManager2);




                empty_view1.setVisibility(View.VISIBLE);
                if(items1.size() > 0){
                    empty_view1.setVisibility(View.GONE);
                }



                empty_view2.setVisibility(View.VISIBLE);
                if(items2.size() > 0){
                    empty_view2.setVisibility(View.GONE);
                }


                AdapterAkumulasi1 adapter1 = new AdapterAkumulasi1(items1,context);
                recyclerView1.setItemAnimator(new DefaultItemAnimator());
                recyclerView1.setAdapter(adapter1);



                AdapterAkumulasi2 adapter2 = new AdapterAkumulasi2(items2,context);
                recyclerView2.setItemAnimator(new DefaultItemAnimator());
                recyclerView2.setAdapter(adapter2);




                View viewarr[]={ly1,ly2};
                container.addView(viewarr[position]);
                return viewarr[position];
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        }

    }


    void getList(){
        try {

            /**
             * PRESENSI
             */

            JSONObject j1 = koneksi.getDataServer("/api/presensi_byortu?key="+username);
            if( j1.getBoolean("success") ) {
                JSONArray tanggal = j1.getJSONArray("response");
                for (int i = 0; i < tanggal.length(); i++) {
                    JSONObject command2 = tanggal.getJSONObject(i);

                    items1.add(new Presensi(
                            command2.getString("tanggal")
                    ));

                    JSONArray tanggal_data = command2.getJSONArray("tanggal_data");
                    for (int j = 0; j < tanggal_data.length(); j++) {
                        JSONObject command = tanggal_data.getJSONObject(j);

                        items1.add(new Presensi(
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

                sweetAlertDialog.dismiss();


            }else{
                empty_view1.setVisibility(View.VISIBLE);
                sweetAlertDialog.dismiss();
            }


            /**
             * TUGAS
             */



            JSONObject j2 = koneksi.getDataServer("/api/tugas_byortu?key="+username);
            if( j2.getBoolean("success") ) {
                JSONArray tanggal = j2.getJSONArray("response");
                for (int i = 0; i < tanggal.length(); i++) {
                    JSONObject command2 = tanggal.getJSONObject(i);

                    items2.add(new Tugas(
                            command2.getString("tanggal")
                    ));

                    JSONArray tanggal_data = command2.getJSONArray("tanggal_data");
                    for (int j = 0; j < tanggal_data.length(); j++) {
                        JSONObject command = tanggal_data.getJSONObject(j);

                        items2.add(new Tugas(
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

                sweetAlertDialog.dismiss();


            }else{
                empty_view2.setVisibility(View.VISIBLE);
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




    @Override
    protected void onStart() {
        super.onStart();

        getList();


    }




    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {


            finish();
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Ketuk sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    private void signOutComplete(){

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin?")
                .setContentText("Sesi akan kami akhiri segera!");
        sweetAlertDialog.setConfirmText("Keluar!");
        sweetAlertDialog.setConfirmClickListener(sDialog -> {
            sDialog.dismiss();


            sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("uid");
            editor.remove("username");
            editor.remove("password");
            editor.remove("nama");
            editor.remove("foto");
            editor.remove("level");
            editor.apply();

            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();


        });
        sweetAlertDialog.setCancelText("Batal");
        sweetAlertDialog.showCancelButton(true);
        sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismiss());
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

    }
}