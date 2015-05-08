package com.bcp.CustomerSurvey;

import android.content.Context;
import android.test.FlakyTest;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by IT-SUPERMASTER on 28/03/2015.
 */
public class AdapterPelangganListView extends BaseAdapter {
    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Data_Pelanggan> pelanggandatalist = null;
    private ArrayList<Data_Pelanggan> arraylist;
    int Mode;

    public AdapterPelangganListView(Context context, List<Data_Pelanggan> pelanggandatalist,int Mode) {
        mContext = context;
        this.pelanggandatalist = pelanggandatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Data_Pelanggan>();
        this.arraylist.addAll(pelanggandatalist);
        this.Mode = Mode;
    }

    public class ViewHolder {
        TextView ShipTo;
        TextView Perusahaan;
        TextView Alamat;
        TextView JdlAlamat;
        TextView KodeBCP;
        ImageView ImgNav;
    }

    public void removeData(String Kode){
        String Key = Kode.toLowerCase(Locale.getDefault());
        for (int i=0;i<arraylist.size();i++){
            if (arraylist.get(i).getShipTo().toLowerCase(Locale.getDefault()).equals(Key)){
                arraylist.remove(i);
            }
        }
        for (int j=0;j<pelanggandatalist.size();j++){
            if (pelanggandatalist.get(j).getShipTo().toLowerCase(Locale.getDefault()).equals(Key)){
                pelanggandatalist.remove(j);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pelanggandatalist.size();
    }

    @Override
    public Data_Pelanggan getItem(int position) {
        return pelanggandatalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.l_pelanggan, null);
            // Locate the TextViews in listview_item.xml
            holder.ShipTo = (TextView) view.findViewById(R.id.Pelanggan_ShipTo);
            holder.Perusahaan = (TextView) view.findViewById(R.id.Pelanggan_Perusahaan);
            holder.Alamat = (TextView) view.findViewById(R.id.Pelanggan_Alamat);
            holder.JdlAlamat = (TextView) view.findViewById(R.id.DeliveryOrderList_TxtTgl);
            holder.ImgNav = (ImageView) view.findViewById(R.id.Pelanggan_Image);
            holder.KodeBCP = (TextView) view.findViewById(R.id.Pelanggan_KodeBCP);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Set the results into TextViews
        holder.ShipTo.setText(pelanggandatalist.get(position).getShipTo());
        holder.Perusahaan.setText(pelanggandatalist.get(position).getPerusahaan());
        holder.Alamat.setText(pelanggandatalist.get(position).getAlamat());
        holder.KodeBCP.setText(pelanggandatalist.get(position).getKodeBCP());

        if(Mode==1){
            holder.Alamat.setVisibility(View.GONE);
            holder.JdlAlamat.setVisibility(View.GONE);
            holder.ImgNav.setVisibility(View.GONE);
        }
        return view;
    }

    public void filter(String charText,String FilterKey) {
        charText = charText.toLowerCase(Locale.getDefault());
        FilterKey = FilterKey.toLowerCase(Locale.getDefault());
        pelanggandatalist.clear();
        if ((charText.equals(""))&&(FilterKey.equals(""))) {
                pelanggandatalist.addAll(arraylist);
        }
        else
        {
            for (Data_Pelanggan plg : arraylist)
            {
                if (plg.getPerusahaan().toLowerCase(Locale.getDefault()).contains(charText)){
                    if(FilterKey.equals("baru")){
                        if(plg.getShipTo().toLowerCase(Locale.getDefault()).contains("/n/")){
                            pelanggandatalist.add(plg);
                        }
                    }else{
                        if(plg.getKodeBCP().toLowerCase(Locale.getDefault()).contains(FilterKey)){
                            pelanggandatalist.add(plg);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}
