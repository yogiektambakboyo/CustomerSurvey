package com.bcp.CustomerSurvey;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Activity_Verify extends Activity {

    //DB Handler
    private FN_DBHandler db,dbsrv;
    private FN_NetCon netStatus;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/BCS";
    private String      DB_MASTER="MASTER";
    private String      DB_SURVEY="SURVEY_";
    private final String TAG_SHIPTO = "kode";
    private final String TAG_KODEBCP = "kodebcp";
    private final String TAG_MODE = "mode";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_ALAMAT = "alamat";
    private final String TAG_PENGHUBUNG = "penghubung";
    private final String TAG_KOTA = "kota";
    private final String TAG_TELP = "telp";
    private final String TAG_SEGMENT = "segment";
    private final String TAG_SUBSEGMENT = "subsegment";
    private final String TAG_KODEPOS = "kodepos";
    private final String TAG_KECAMATAN = "kecamatan";
    private final String TAG_KELURAHAN = "kelurahan";
    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";
    private final String TAG_PEMILIK = "pemilik";
    private final String TAG_NOHP = "nohp";
    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_CABANG = "cabang";

    String OldKodeBCP = "";


    String Kode,Perusahaan,Alamat,Penghubung,Segment,Kota,KodePos,Kecamatan,Kelurahan,Telp,NoHP,Longitude,Latitude,Pemilik,Mode,SubSegment;
    JSONArray PelangganArray,PelangganArrayS,SegmentArray;

    EditText InputKode,InputPerusahaan,InputAlamat,InputPenghubung,InputSegment,InputKota,InputKodePos,InputKecamatan,Inputkelurahan,InputTelp,InputNoHP,InputPemilik,InputSubSegment;
    TextView InputLatitude,InputLongitude,TxtDetailGeo,TxtLoading;
    ProgressBar PbLoading;
    Button  BtnCariLokasi,BtnMaps;
    Spinner SpnSegment;

    private LocationManager locationManager=null;
    private LocationListener locationListener=null;

    String cityName=" ";
    List<Address> addresses;
    private final String TAG_PELANGGANDATA= "PelangganData";
    String[] ArrKodeSeg,ArrNamaSeg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_verify);

        Intent in = getIntent();
        Kode= in.getStringExtra(TAG_SHIPTO);
        Mode=in.getStringExtra(TAG_MODE);

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        dbsrv = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_SURVEY+getPref(TAG_CABANG));

        JSONObject PelangganJSON = null,SegmentJSON = null;

        if(dbFile.exists()){
            try {
                if(Mode.equals("0")){
                    PelangganJSON = db.getPelangganOne(Kode);
                }else if (Mode.equals("1")){
                    PelangganJSON = dbsrv.getPelangganOneNewData(Kode);
                }

                if (Mode.equals("2")){

                }else{
                    // Getting Array of Pelanggan
                    PelangganArray = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);

                    // looping through All Pelanggan
                    for(int i = 0; i < PelangganArray.length(); i++){
                        JSONObject c = PelangganArray.getJSONObject(i);

                        Perusahaan = c.getString(TAG_PERUSAHAAN);
                        Alamat = c.getString(TAG_ALAMAT);
                        Penghubung = c.getString(TAG_PENGHUBUNG);
                        Segment = c.getString(TAG_SEGMENT);
                        Kota = c.getString(TAG_KOTA);
                        Kecamatan = c.getString(TAG_KECAMATAN);
                        Kelurahan = c.getString(TAG_KELURAHAN);
                        Longitude = c.getString(TAG_LONGITUDE);
                        Latitude = c.getString(TAG_LATITUDE);
                        Telp = c.getString(TAG_TELP);
                        NoHP = c.getString(TAG_NOHP);
                        KodePos = c.getString(TAG_KODEPOS);

                        if (Mode.equals("1")){
                            OldKodeBCP = c.getString(TAG_KODEBCP);
                            Pemilik = c.getString(TAG_PEMILIK);
                            SubSegment = c.getString(TAG_SUBSEGMENT);
                        }
                    }
                }

                if((Mode.equals("2"))||((Kode.substring(3,5)).equals("N/"))){
                    SegmentJSON = dbsrv.getSegment("%");
                }else{
                    SegmentJSON = dbsrv.getSegment(Kode.substring(3,5));
                }

                SegmentArray = SegmentJSON.getJSONArray(TAG_PELANGGANDATA);

                ArrKodeSeg = new String[SegmentArray.length()];
                ArrNamaSeg = new String[SegmentArray.length()];

                for (int j=0;j<SegmentArray.length();j++){
                    JSONObject d = SegmentArray.getJSONObject(j);
                    ArrKodeSeg[j] = d.getString(TAG_SHIPTO);
                    ArrNamaSeg[j] = d.getString("nama");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }

        db.close();
        dbsrv.close();

        getActionBar().setDisplayShowTitleEnabled(true);
        if(Mode.equals("2")){
            getActionBar().setTitle("Pelanggan Baru");
        }else{
            getActionBar().setTitle(""+Perusahaan);
        }
        getActionBar().setIcon(R.drawable.bcs_pelanggan);

        if(Mode.equals("2")){
            Kode = getPref(TAG_CABANG)+"/N/"+String.format("%06d",Integer.parseInt(dbsrv.getmaxkodeplgbaru(getPref(TAG_CABANG)+"/N/"))+1);
        }

        InputKode = (EditText) findViewById(R.id.Verify_Kode);
        InputKode.setText(Kode);
        InputKode.setEnabled(false);
        InputKode.setKeyListener(null);

        InputPerusahaan = (EditText) findViewById(R.id.Verify_Perusahaan);
        InputPerusahaan.setText(Perusahaan);

        InputAlamat = (EditText) findViewById(R.id.Verify_Alamat);
        InputAlamat.setText(Alamat);

        InputPenghubung = (EditText) findViewById(R.id.Verify_Penghubung);
        InputPenghubung.setText(Penghubung);

        InputSegment = (EditText) findViewById(R.id.Verify_Segment);
        InputSegment.setText(Segment);
        InputSegment.setEnabled(false);
        InputSegment.setKeyListener(null);

        InputKota = (EditText) findViewById(R.id.Verify_Kota);
        InputKota.setText(Kota);

        InputKodePos = (EditText) findViewById(R.id.Verify_KodePos);
        InputKodePos.setText(KodePos);

        InputKecamatan = (EditText) findViewById(R.id.Verify_Kecamatan);
        InputKecamatan.setText(Kecamatan);

        Inputkelurahan = (EditText) findViewById(R.id.Verify_kelurahan);
        Inputkelurahan.setText(Kelurahan);

        InputNoHP = (EditText) findViewById(R.id.Verify_NoHP);
        InputNoHP.setText(NoHP);

        InputTelp = (EditText) findViewById(R.id.Verify_Telp);
        InputTelp.setText(Telp);

        InputLatitude = (TextView) findViewById(R.id.Verify_latitude);
        InputLatitude.setText(Latitude);

        InputLongitude = (TextView) findViewById(R.id.Verify_Longitude);
        InputLongitude.setText(Longitude);

        TxtDetailGeo = (TextView) findViewById(R.id.Verify_DetailGeo);

        InputPemilik = (EditText) findViewById(R.id.Verify_Pemilik);
        InputPemilik.setText(Pemilik);

        InputSubSegment = (EditText) findViewById(R.id.Verify_SubSegment);
        InputSubSegment.setText(SubSegment);

        SpnSegment = (Spinner) findViewById(R.id.Verify_SpnSegment);

        ArrayAdapter SegmentKodeAdapter = new ArrayAdapter(Activity_Verify.this, android.R.layout.simple_spinner_item, ArrKodeSeg);
        SegmentKodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter SegmentAdapter = new ArrayAdapter(Activity_Verify.this, android.R.layout.simple_spinner_item, ArrNamaSeg);
        SegmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpnSegment.setAdapter(SegmentAdapter);
        if(Mode.equals("1")){
            SpnSegment.setSelection(SegmentAdapter.getPosition(Segment));
        }else if(Mode.equals("0")){
            SpnSegment.setSelection(SegmentKodeAdapter.getPosition(Segment));
        }
        SpnSegment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                InputSegment.setText(ArrKodeSeg[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        netStatus = new FN_NetCon();

        BtnCariLokasi = (Button) findViewById(R.id.Verify_BtnLokasi);
        BtnCariLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TxtLoading.setVisibility(View.VISIBLE);
                PbLoading.setVisibility(View.VISIBLE);
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationListener = new MyLocationListener();

                if(netStatus.getConnectivityStatusString(getApplicationContext())==2){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10,locationListener);
                }else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10,locationListener);
                }
            }
        });

        BtnMaps = (Button) findViewById(R.id.Verify_BtnMap);
        BtnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(InputLatitude.getText().toString().length()>4){
                    Intent in = new Intent(getApplicationContext(),Activity_Map.class);
                    in.putExtra(TAG_LATITUDE,InputLatitude.getText().toString());
                    in.putExtra(TAG_LONGITUDE,InputLongitude.getText().toString());
                    in.putExtra(TAG_PERUSAHAAN,Perusahaan);
                    startActivityForResult(in,1988);
                }else{
                    Toast.makeText(getApplicationContext(),"Geolocation masih kosong",Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.sfa_save);

        final FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KonfirmasiInsert();
            }
        });

        TxtLoading = (TextView) findViewById(R.id.Verify_TxtLoading);
        PbLoading = (ProgressBar) findViewById(R.id.Verify_Loading);

        TxtLoading.setVisibility(View.GONE);
        PbLoading.setVisibility(View.GONE);
    }

    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    cityName = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1);
                }

                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 1; i++) {
                        sb.append("Jalan : "+address.getThoroughfare());
                        sb.append("\n");
                        sb.append("Kecamatan : "+address.getLocality());
                        sb.append("\n");
                        sb.append("Kelurahan : "+address.getSubLocality());
                        sb.append("\n");
                        sb.append("Kode Pos : "+address.getPostalCode());
                        sb.append("\n");
                        sb.append("Kota : "+address.getSubAdminArea());
                        sb.append("\n");
                        sb.append("Provinsi : " + address.getAdminArea());
                    }
                    TxtDetailGeo.setText("" + sb.toString());
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Gagal Mendapatkan Lokasi : Kode 01 - "+e.getMessage(),Toast.LENGTH_SHORT).show();
                TxtLoading.setVisibility(View.GONE);
                PbLoading.setVisibility(View.GONE);
            }

            if (loc == null){
                loc  = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Toast.makeText(getApplicationContext(),"Gagal Mendapatkan Lokasi : Kode 02",Toast.LENGTH_SHORT).show();
                TxtLoading.setVisibility(View.GONE);
                PbLoading.setVisibility(View.GONE);
            }

            if (loc != null){
                //Toast.makeText(getApplication(),cityName,Toast.LENGTH_SHORT).show();
                InputLatitude.setText(Double.toString(loc.getLatitude()));
                InputLongitude.setText(Double.toString(loc.getLongitude()));
            }
            TxtLoading.setVisibility(View.GONE);
            PbLoading.setVisibility(View.GONE);

            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(),"Gagal Mendapatkan Lokasi : Kode 03 - Provider Non Aktif",Toast.LENGTH_SHORT).show();
            TxtLoading.setVisibility(View.GONE);
            PbLoading.setVisibility(View.GONE);
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Toast.makeText(getApplicationContext(),"Gagal Mendapatkan Lokasi : Kode 03 - Provider Non Aktif",Toast.LENGTH_SHORT).show();
                    TxtLoading.setVisibility(View.GONE);
                    PbLoading.setVisibility(View.GONE);
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Toast.makeText(getApplicationContext(),"Gagal Mendapatkan Lokasi : Kode 03 - Provider Non Aktif",Toast.LENGTH_SHORT).show();
                    TxtLoading.setVisibility(View.GONE);
                    PbLoading.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1988){
            if(resultCode == RESULT_OK){
                InputLatitude.setText(data.getStringExtra("Latitude"));
                InputLongitude.setText(data.getStringExtra("Longitude"));
                TxtDetailGeo.setText(data.getStringExtra("DetailGeo"));
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent intent = new Intent(getApplicationContext(), Activity_MainMenu.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Konfirmasi");
                builder.setMessage("Apakah anda yakin membatalkan transaksi ini?").setPositiveButton("Ya", dialogClickListener)
                        .setNegativeButton("Tidak", dialogClickListener).show();
        }
        return false;
    }

    public void KonfirmasiInsert(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if (InputPerusahaan.getText().toString().length()<=0){
                            Toast.makeText(getApplicationContext(),"Perusahaan tidak boleh kosong",Toast.LENGTH_SHORT).show();
                        }else if (InputPemilik.getText().toString().length()<=0){
                            Toast.makeText(getApplicationContext(),"Pemilik tidak boleh kosong",Toast.LENGTH_SHORT).show();
                        }else if (InputAlamat.getText().toString().length()<=0){
                            Toast.makeText(getApplicationContext(),"Alamat tidak boleh kosong",Toast.LENGTH_SHORT).show();
                        }else if (InputKota.getText().toString().length()<=0){
                            Toast.makeText(getApplicationContext(),"Kota tidak boleh kosong",Toast.LENGTH_SHORT).show();
                        }else if (InputKodePos.getText().toString().length()<=0){
                            Toast.makeText(getApplicationContext(),"Kode Pos tidak boleh kosong",Toast.LENGTH_SHORT).show();
                        }else{
                            String KodeBCP = "";
                            if(Mode.equals("1")){
                                KodeBCP = OldKodeBCP;
                                dbsrv.deletePelanggan(KodeBCP,InputKode.getText().toString());
                            }else if(Mode.equals("2")){
                                KodeBCP = getPref(TAG_CABANG)+"/"+String.format("%06d",Integer.parseInt(dbsrv.getmaxkode(getPref(TAG_CABANG)+"/"))+1);
                            }else{
                                KodeBCP = Kode.substring(0,2)+"/"+String.format("%06d",Integer.parseInt(dbsrv.getmaxkode(Kode.substring(0,2)))+1);
                            }

                            dbsrv.insertPelanggan(KodeBCP,InputKode.getText().toString(),InputPerusahaan.getText().toString(),InputAlamat.getText().toString(),InputPenghubung.getText().toString(),InputKota.getText().toString(),InputTelp.getText().toString(),InputSegment.getText().toString(),SpnSegment.getSelectedItem().toString(),InputKodePos.getText().toString(),InputKecamatan.getText().toString(),Inputkelurahan.getText().toString(),InputLongitude.getText().toString(),InputLatitude.getText().toString(),InputNoHP.getText().toString(),InputPemilik.getText().toString(),InputSubSegment.getText().toString());
                            Toast.makeText(getApplicationContext(),"Data Tersimpan",Toast.LENGTH_SHORT).show();

                            Intent in = new Intent(getApplicationContext(),Activity_MainMenu.class);
                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(in);
                        }

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah anda yakin akan memproses transaksi ini?").setPositiveButton("Ya", dialogClickListener)
                .setNegativeButton("Tidak", dialogClickListener).show();
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pelanggan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_linked) {
            ShowListPelanggan();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowListPelanggan(){
        final Dialog dialog = new Dialog(Activity_Verify.this, android.R.style.Theme_Dialog);
        dialog.setTitle("Pelanggan Terverifikasi");
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.d_daftarpelanggan);
        Context dContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) dContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        ListView list;
        final EditText TxtSearch = (EditText) dialog.findViewById(R.id.PelangganVerifikasi_Search);

        final AdapterPelangganListView adapter;
        ArrayList<Data_Pelanggan> PelangganList = new ArrayList<Data_Pelanggan>();
        JSONObject PelangganJSON = null;

        try {
            PelangganJSON = dbsrv.getPelangganVerifikasi(Kode);
            // Getting Array of Pelanggan
            PelangganArray = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);

            String[] ShipToS = new String[PelangganArray.length()];
            String[] PerusahaanS = new String[PelangganArray.length()];
            String[] AlamatS = new String[PelangganArray.length()];
            String[] KodeBCPS = new String[PelangganArray.length()];

            // looping through All Pelanggan
            for(int i = 0; i < PelangganArray.length(); i++){
                JSONObject c = PelangganArray.getJSONObject(i);

                //status = c.getInt(TAG_STATUS);
                String ShipToSS = c.getString(TAG_SHIPTO);
                String PerusahaanSS = c.getString(TAG_PERUSAHAAN);
                String AlamatSS= c.getString(TAG_ALAMAT);
                String KodeBCPSS= c.getString(TAG_KODEBCP);

                ShipToS[i] = ShipToSS;
                PerusahaanS[i] = PerusahaanSS;
                AlamatS[i] = AlamatSS;
                KodeBCPS[i] = KodeBCPSS;
            }

            for (int i = 0; i < ShipToS.length; i++)
            {
                Data_Pelanggan plg = new Data_Pelanggan(ShipToS[i], PerusahaanS[i], AlamatS[i],KodeBCPS[i]);
                PelangganList.add(plg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        dbsrv.close();

        list = (ListView) dialog.findViewById(R.id.PelangganVerifikasiListView);

        // Pass results to ListViewAdapter Class
        adapter = new AdapterPelangganListView(getApplicationContext(), PelangganList,1);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog.dismiss();
                JSONObject PelangganJSON = null;

                String KodeBCPX="",KodeX=adapter.getItem(i).getShipTo(),PerusahaanX=adapter.getItem(i).getPerusahaan(),AlamatX=adapter.getItem(i).getAlamat(),PenghubungX="",SegmentX="",KotaX="",KodePosX="",KecamatanX="",KelurahanX="",TelpX="",NoHPX="",LongitudeX="",LatitudeX="",PemilikX="";
                try {
                    PelangganJSON = dbsrv.getPelangganOneNewData(adapter.getItem(i).getShipTo());
                    PelangganArrayS = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);

                    for(int j = 0; j < PelangganArrayS.length(); j++){
                        JSONObject c = PelangganArrayS.getJSONObject(j);

                        KodeX = c.getString(TAG_SHIPTO);
                        PerusahaanX = c.getString(TAG_PERUSAHAAN);
                        AlamatX= c.getString(TAG_ALAMAT);
                        PenghubungX = c.getString(TAG_PENGHUBUNG);
                        SegmentX = c.getString(TAG_SEGMENT);
                        KotaX = c.getString(TAG_KOTA);
                        KecamatanX = c.getString(TAG_KECAMATAN);
                        KelurahanX = c.getString(TAG_KELURAHAN);
                        LongitudeX = c.getString(TAG_LONGITUDE);
                        LatitudeX = c.getString(TAG_LATITUDE);
                        TelpX = c.getString(TAG_TELP);
                        NoHPX = c.getString(TAG_NOHP);
                        KodePosX = c.getString(TAG_KODEPOS);
                        KodeBCPX = c.getString(TAG_KODEBCP);
                        PemilikX = c.getString(TAG_PEMILIK);
                        Mode="1";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                KonfirmasiLinkPelanggan(PemilikX,PerusahaanX,PenghubungX,SegmentX,AlamatX,KotaX,KodePosX,KecamatanX,KelurahanX,TelpX,NoHPX,LongitudeX,LatitudeX,KodeBCPX);
            }
        });

        TxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.filter(TxtSearch.getText().toString(),"");
            }
        });

        dialog.show();
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.bcs_link);
    }

    public void KonfirmasiLinkPelanggan(final String PemilikZ,final String PerusahaanZ,final String PenghubungZ,final String SegmentZ,final String AlamatZ,final String KotaZ,final String KodePosZ,final String KecamatanZ,final String KelurahanZ,final String TelpZ,final String NoHPZ,final String LongitudeZ,final String LatitudeZ,final String KodeBCPZ){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getApplicationContext(),"Berhasil menghubungkan pelanggan",Toast.LENGTH_SHORT).show();
                        InputPerusahaan.setText(PerusahaanZ);
                        InputAlamat.setText(AlamatZ);
                        InputPenghubung.setText(PenghubungZ);
                        InputKota.setText(KotaZ);
                        InputKodePos.setText(KodePosZ);
                        InputKecamatan.setText(KecamatanZ);
                        Inputkelurahan.setText(KelurahanZ);
                        InputNoHP.setText(NoHPZ);
                        InputTelp.setText(TelpZ);
                        InputLatitude.setText(LatitudeZ);
                        InputLongitude.setText(LongitudeZ);
                        InputPemilik.setText(PemilikZ);
                        OldKodeBCP = KodeBCPZ;
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah anda yakin akan menghubungkan pelanggan ini dengan pelanggaan : "+PerusahaanZ+" ?").setPositiveButton("Ya", dialogClickListener)
                .setNegativeButton("Tidak", dialogClickListener).show();
    }
}
