package com.bcp.CustomerSurvey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Activity_Pelanggan extends Activity {

    //DB Handler
    private FN_DBHandler db,dbsrv;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/BCS";
    private String      DB_MASTER="MASTER";
    private String      DB_SURVEY="SURVEY_";
    private final String TAG_SHIPTO = "kode";
    private final String TAG_KODEBCP = "kodebcp";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_ALAMAT = "alamat";
    private final String TAG_CABANG = "cabang";
    private final String TAG_MODE = "mode";
    private final String TAG_PREF="SETTINGPREF";

    // Declare Variables
    ListView list;
    AdapterPelangganListView adapter;
    EditText TxtCari;
    ImageView ImgFilter,ImgTambah;
    TextView TxtData;
    LinearLayout LytData;

    String[] ShipTo,Perusahaan,Alamat,KodeBCP;
    ArrayList<Data_Pelanggan> PelangganList = new ArrayList<Data_Pelanggan>();

    JSONArray PelangganArray;
    private final String TAG_PELANGGANDATA= "PelangganData";

    List AreaList;
    String FilterKeys ="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_pelanggan);

        AreaList = new ArrayList();

        ImgFilter = (ImageView) findViewById(R.id.Pelanggan_ImgFilter);
        ImgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFilter();
            }
        });

        ImgTambah = (ImageView) findViewById(R.id.Pelanggan_ImgTmbah);
        ImgTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialogTambah();
            }
        });
        //ImgFilter.setVisibility(View.GONE);

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        dbsrv = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_SURVEY+getPref(TAG_CABANG));

        JSONObject PelangganJSON = null;

        if(dbFile.exists()){
            try {
                PelangganJSON = db.getPelangganJoin(DB_PATH, DB_SURVEY + getPref(TAG_CABANG), getPref(TAG_CABANG));
                //PelangganJSON = db.getPelanggan(getPref(TAG_CABANG));
                // Getting Array of Pelanggan
                PelangganArray = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);

                ShipTo = new String[PelangganArray.length()];
                Perusahaan = new String[PelangganArray.length()];
                Alamat = new String[PelangganArray.length()];
                KodeBCP= new String[PelangganArray.length()];

                // looping through All Pelanggan
                for(int i = 0; i < PelangganArray.length(); i++){
                    JSONObject c = PelangganArray.getJSONObject(i);

                    //status = c.getInt(TAG_STATUS);
                    String ShipToS = c.getString(TAG_SHIPTO);
                    String PerusahaanS = c.getString(TAG_PERUSAHAAN);
                    String AlamatS= c.getString(TAG_ALAMAT);
                    String KodeBCPS= c.getString(TAG_KODEBCP);

                    ShipTo[i] = ShipToS;
                    Perusahaan[i] = PerusahaanS;
                    Alamat[i] = AlamatS;
                    KodeBCP[i] = KodeBCPS;
                }

                for (int i = 0; i < ShipTo.length; i++)
                {
                    Data_Pelanggan plg = new Data_Pelanggan(ShipTo[i], Perusahaan[i], Alamat[i],KodeBCP[i]);
                    PelangganList.add(plg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            db.close();
        }else{
            Toast.makeText(getApplicationContext(), "DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }

        dbsrv.close();


        list = (ListView) findViewById(R.id.PelangganListView);

        // Pass results to ListViewAdapter Class
        adapter = new AdapterPelangganListView(getApplicationContext(), PelangganList,0);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapter.getItem(i).getShipTo().contains("/N/")){
                    ShowDialogDelete(adapter.getItem(i).getShipTo(),adapter.getItem(i).getPerusahaan(),adapter.getItem(i).getKodeBCP());
                }else{
                    String Jum = dbsrv.cekPelangganExist(adapter.getItem(i).getShipTo());
                    if(Jum.equals("1")) {
                        ShowDialogDelete(adapter.getItem(i).getShipTo(),adapter.getItem(i).getPerusahaan(),adapter.getItem(i).getKodeBCP());
                    }
                }
                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String Jum = dbsrv.cekPelangganExist(adapter.getItem(position).getShipTo());
                if(Jum.equals("0")) {
                    Intent in = new Intent(getApplicationContext(), Activity_Verify.class);
                    in.putExtra(TAG_SHIPTO, adapter.getItem(position).getShipTo());
                    in.putExtra(TAG_MODE, "0");
                    startActivity(in);
                }else{
                    KonfirmasiInsert(position);
                }
            }
        });

        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    LytData.setVisibility(View.VISIBLE);
                }else{
                    LytData.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {

            }

        });

        TxtCari = (EditText) findViewById(R.id.Pelanggan_Search);

        // Capture Text in EditText
        TxtCari.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                adapter.filter(TxtCari.getText().toString(),FilterKeys);
                TxtData.setText(""+adapter.getCount());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        TxtData = (TextView) findViewById(R.id.Pelanggan_TxtData);
        TxtData.setText(""+adapter.getCount());

        LytData = (LinearLayout) findViewById(R.id.Pelanggan_LayoutData);
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    public void KonfirmasiInsert(final int pos){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent in = new Intent(getApplicationContext(), Activity_Verify.class);
                        in.putExtra(TAG_SHIPTO, adapter.getItem(pos).getShipTo());
                        in.putExtra(TAG_MODE, "1");
                        startActivity(in);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Data pelanggan sudah pernah diverifikasi, Apakah ingin di verifikasi lagi?").setPositiveButton("Ya", dialogClickListener)
                .setNegativeButton("Tidak", dialogClickListener).show();
    }

    public void ShowFilter(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter");
        builder.setIcon(R.drawable.sfa_filter_up);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 10, 30, 10);

        String[] ArrFilter = new String[4];

        ArrFilter[0] = "Semua";
        ArrFilter[1] = "Terverifikasi";
        ArrFilter[2] = "Belum Terverifikasi";
        ArrFilter[3] = "Pelanggan Baru";

        final TextView TxtLblFilterTgl = new TextView(this);
        TxtLblFilterTgl.setText("Filter :");

        final Spinner SpnFilterTgl = new Spinner(this);

        ArrayAdapter adapterFilterTgl = new ArrayAdapter(this,android.R.layout.simple_spinner_item, ArrFilter);
        adapterFilterTgl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpnFilterTgl.setAdapter(adapterFilterTgl);
        SpnFilterTgl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0 :{
                        FilterKeys = "";
                        break;
                    }
                    case 1 :{
                        FilterKeys = "/";
                        break;
                    }
                    case 2 :{
                        FilterKeys = "belum";
                        break;
                    }
                    case 3 : {
                        FilterKeys = "baru";
                    }
                    default:break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        layout.addView(TxtLblFilterTgl,params);
        layout.addView(SpnFilterTgl,params);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.filter(TxtCari.getText().toString(),FilterKeys);
                TxtData.setText(""+adapter.getCount());
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builder.show();
    }

    public void ShowDialogTambah(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setIcon(R.drawable.bcs_plus_up);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 10, 30, 10);


        final TextView TxtLblFilterTgl = new TextView(this);
        TxtLblFilterTgl.setText("Apakah anda yakin akan membuat pelanggan baru?");


        layout.addView(TxtLblFilterTgl,params);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in = new Intent(getApplicationContext(), Activity_Verify.class);
                in.putExtra(TAG_SHIPTO, "Pelanggan Baru");
                in.putExtra(TAG_MODE, "2");
                startActivity(in);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builder.show();
    }

    public void ShowDialogDelete(final String Shipto,String Perusahaan, final String KodeBCP){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setIcon(R.drawable.bcs_trash);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 10, 30, 10);


        final TextView TxtLblFilterTgl = new TextView(this);
        TxtLblFilterTgl.setText("Apakah anda yakin akan menghapus verifikasi pelanggan : "+Perusahaan+" ?");


        layout.addView(TxtLblFilterTgl,params);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbsrv.deletePelanggan(KodeBCP,Shipto);
                adapter.removeData(Shipto);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
