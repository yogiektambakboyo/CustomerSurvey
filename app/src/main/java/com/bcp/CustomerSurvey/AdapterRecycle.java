package com.bcp.CustomerSurvey;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by IT-SUPERMASTER on 15/04/2015.
 */
public class AdapterRecycle extends RecyclerView.Adapter<AdapterRecycle.ContactViewHolder> {

    private List<Data_Pelanggan> PelangganList;
    private ArrayList<Data_Pelanggan> TempPelangganList;
    Context mContext;

    public AdapterRecycle(List<Data_Pelanggan> pelangganList,Context m) {
        this.PelangganList = pelangganList;
        this.mContext = m;
        this.TempPelangganList = new ArrayList<Data_Pelanggan>();
        this.TempPelangganList.addAll(PelangganList);
    }


    @Override
    public int getItemCount() {
        return TempPelangganList.size();
    }

    public void removeData(String Kode){
        String Key = Kode.toLowerCase(Locale.getDefault());
        for (int i=0;i<PelangganList.size();i++){
            if (PelangganList.get(i).getShipTo().toLowerCase(Locale.getDefault()).equals(Key)){
                PelangganList.remove(i);
            }
        }
        for (int j=0;j<TempPelangganList.size();j++){
            if (TempPelangganList.get(j).getShipTo().toLowerCase(Locale.getDefault()).equals(Key)){
                TempPelangganList.remove(j);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public Data_Pelanggan getItem(int position) {
        return TempPelangganList.get(position);
    }

    public void filter(String charText,String FilterKey) {
        charText = charText.toLowerCase(Locale.getDefault());
        FilterKey = FilterKey.toLowerCase(Locale.getDefault());
        TempPelangganList.clear();
        if ((charText.equals(""))&&(FilterKey.equals(""))) {
            TempPelangganList.addAll(PelangganList);
        }
        else
        {
            for (Data_Pelanggan plg : PelangganList)
            {
                if (plg.getPerusahaan().toLowerCase(Locale.getDefault()).contains(charText)){
                    if(FilterKey.equals("baru")){
                        if(plg.getShipTo().toLowerCase(Locale.getDefault()).contains("/n/")){
                            TempPelangganList.add(plg);
                        }
                    }else{
                        if(plg.getKodeBCP().toLowerCase(Locale.getDefault()).contains(FilterKey)){
                            TempPelangganList.add(plg);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        Data_Pelanggan ci = TempPelangganList.get(i);
        contactViewHolder.vAlamat.setText(ci.getAlamat());
        contactViewHolder.vPerusahaan.setText(ci.getPerusahaan());
        contactViewHolder.vKode.setText(ci.getShipTo());
        contactViewHolder.vKodeBCP.setText(ci.getKodeBCP());
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.l_pelanggan, viewGroup, false);

        return new ContactViewHolder(itemView,mContext);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView vPerusahaan;
        protected TextView vAlamat;
        protected TextView vKode;
        protected TextView vKodeBCP;
        Context m;

        public ContactViewHolder(View v,Context c) {
            super(v);
            vPerusahaan =  (TextView) v.findViewById(R.id.Pelanggan_Perusahaan);
            vAlamat = (TextView)  v.findViewById(R.id.Pelanggan_Alamat);
            vKode = (TextView)  v.findViewById(R.id.Pelanggan_ShipTo);
            vKodeBCP = (TextView)  v.findViewById(R.id.Pelanggan_KodeBCP);
            v.setOnClickListener(this);
            m = c;
        }

        @Override
        public void onClick(View view) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(m,""+vPerusahaan.getText().toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}