package com.absensi.sekolah.controller.orangtua.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.absensi.sekolah.R;
import com.absensi.sekolah.models.Presensi;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAkumulasi1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Presensi> presensiList;
    private static final int LAYOUT_HEADER= 0;
    private static final int LAYOUT_CHILD= 1;
    Context c;

    public AdapterAkumulasi1(List<Presensi> presensiList, Context c) {
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
            holder = new AdapterAkumulasi1.ItemHeaderViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_presensi, parent, false);
            holder = new AdapterAkumulasi1.ItemViewHolder(view);
        }
        return holder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if(holder.getItemViewType()== LAYOUT_HEADER){
            AdapterAkumulasi1.ItemHeaderViewHolder itemHeaderViewHolder = (AdapterAkumulasi1.ItemHeaderViewHolder) holder;

            itemHeaderViewHolder.tvHeader.setText(presensiList.get(position).absensi_tanggal);

        }else {

            final AdapterAkumulasi1.ItemViewHolder itemViewHolder = (AdapterAkumulasi1.ItemViewHolder) holder;

            final Presensi itemPresensi = presensiList.get(position);


            itemViewHolder.tv1.setText(itemPresensi.absensi_keterangan);
            itemViewHolder.tv2.setText(itemPresensi.absensi_jam);

            if(!TextUtils.isEmpty(itemPresensi.absensi_foto)){
                Picasso.with(itemViewHolder.itemView.getContext())
                        .load(itemPresensi.absensi_foto)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(itemViewHolder.foto);
            }


            itemViewHolder.action_lokasi.setOnClickListener(v->{
                Intent intent = new Intent(v.getContext(), com.absensi.sekolah.controller.HalamanLokasi.class);
                intent.putExtra("xlat",itemPresensi.absensi_lat);
                intent.putExtra("xlong",itemPresensi.absensi_long);
                intent.putExtra("dari","orangtua");
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

