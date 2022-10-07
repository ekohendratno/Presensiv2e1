package com.absensi.sekolah.controller.orangtua.adapters;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.absensi.sekolah.R;
import com.absensi.sekolah.controller.siswa.HalamanTugas;
import com.absensi.sekolah.models.Tugas;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAkumulasi2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Tugas> nilaiList;
    private static final int LAYOUT_HEADER= 0;
    private static final int LAYOUT_CHILD= 1;
    Context c;

    private static BottomSheetDialog mBottomSheetDialog;
    private View bottomSheetLayout;
    private BottomSheetBehavior mBehavior;

    static AppCompatEditText editKey,editKey2,editFile,editText;
    static TextView tv1,tv2,tv3,tv4;

    private static LinearLayout lyJawaban;
    private static CardView lyCard;
    private static TextView tv1Card;
    private static MaterialButton sendCard;

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public AdapterAkumulasi2(List<Tugas> nilaiList, Context c) {
        this.nilaiList = nilaiList;
        this.c = c;




        /**
         * BOTTOM SHEET ADD
         */
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        this.bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_siswa_tugas, null);
        this.mBottomSheetDialog = new BottomSheetDialog(c);

        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        this.mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });

        this.editKey = bottomSheetLayout.findViewById(R.id.editKey);
        this.editKey2 = bottomSheetLayout.findViewById(R.id.editKey2);
        this.editFile = bottomSheetLayout.findViewById(R.id.editFile);
        this.editText = bottomSheetLayout.findViewById(R.id.editText);

        this.tv1 = bottomSheetLayout.findViewById(R.id.tv1);
        this.tv2 = bottomSheetLayout.findViewById(R.id.tv2);
        this.tv3 = bottomSheetLayout.findViewById(R.id.tv3);
        this.tv4 = bottomSheetLayout.findViewById(R.id.tv4);

        editKey.setText("");
        editKey2.setText("");
        editFile.setText("");
        editText.setText("");

        tv1.setText("");
        tv2.setText("");
        tv3.setText("");
        tv4.setText("");

        this.lyJawaban = bottomSheetLayout.findViewById(R.id.lyJawaban);
        this.lyCard = bottomSheetLayout.findViewById(R.id.lyCard);
        this.tv1Card = bottomSheetLayout.findViewById(R.id.tv1Card);

        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());
        bottomSheetLayout.findViewById(R.id.fab_upload).setOnClickListener(view -> {

            //showPictureDialog(view.getContext());

        });

        this.sendCard = bottomSheetLayout.findViewById(R.id.action_send);
        sendCard.setOnClickListener(view -> {
            //new kirimTugas2Sync().execute();
        });


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
            holder = new AdapterAkumulasi2.ItemHeaderViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tugas, parent, false);
            holder = new AdapterAkumulasi2.ItemViewHolder(view);
        }
        return holder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if(holder.getItemViewType()== LAYOUT_HEADER){
            AdapterAkumulasi2.ItemHeaderViewHolder itemHeaderViewHolder = (AdapterAkumulasi2.ItemHeaderViewHolder) holder;

            String tanggal = nilaiList.get(position).tugas_tanggal;
            String tanggal_today = sdf.format(new Date());
            if(tanggal.equalsIgnoreCase(tanggal_today)){
                tanggal+= " - hari ini";
            }

            itemHeaderViewHolder.tvHeader.setText(tanggal);

        }else {

            final AdapterAkumulasi2.ItemViewHolder itemViewHolder = (AdapterAkumulasi2.ItemViewHolder) holder;

            final Tugas itemTugas = nilaiList.get(position);

            itemViewHolder.tv1.setText(itemTugas.tugas_tanggal);
            itemViewHolder.tv2.setText(itemTugas.tugas_title);
            itemViewHolder.tv4.setText("Tugas Dari Guru, " + itemTugas.guru_nama);

            //itemViewHolder.action_edit.setVisibility(View.VISIBLE);


            itemViewHolder.action_check.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(itemTugas.tugas_dikerjakan_status)){
                itemViewHolder.action_edit.setColorFilter(0xff43a047);

                itemViewHolder.action_check.setImageDrawable(itemViewHolder.itemView.getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24));
                itemViewHolder.action_check.setColorFilter(0xff43a047);
            }

            itemViewHolder.itemView.setOnClickListener(view -> {

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

                lyJawaban.setVisibility(View.GONE);
                lyCard.setVisibility(View.GONE);
                sendCard.setVisibility(View.GONE);

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
        ImageView action_check;

        public ItemViewHolder(View itemView) {
            super(itemView);
            action_check = itemView.findViewById(R.id.action_check);
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