package com.bcp.CustomerSurvey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;


public class Activity_Recylce extends Activity {

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

    String[] ShipTo,Perusahaan,Alamat,KodeBCP;
    ArrayList<Data_Pelanggan> PelangganList = new ArrayList<Data_Pelanggan>();

    JSONArray PelangganArray;
    private final String TAG_PELANGGANDATA= "PelangganData";

    List AreaList;
    LinearLayout LytData;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    TextView TxtCari;

    String FilterKeys="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_cycle);

        AreaList = new ArrayList();

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

        final RecyclerView recList = (RecyclerView) findViewById(R.id.Pelanggan_RecyclerView);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        final AdapterRecycle adapter = new AdapterRecycle(PelangganList,getApplicationContext());
        //recList.setAdapter(ca);

        recList.setAdapter(new ScaleInAnimationAdapter(adapter));
        recList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    LytData.setVisibility(View.VISIBLE);
                }else{
                    LytData.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        TxtCari = (TextView) findViewById(R.id.Pelanggan_Search);
        TxtCari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                adapter.filter(TxtCari.getText().toString(),FilterKeys);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        LytData = (LinearLayout) findViewById(R.id.Pelanggan_LayoutData);

    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }
}
