package com.bcp.CustomerSurvey;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class Activity_Export extends ListActivity {
    private FN_DBHandler dbsrv;
    private final String TAG_MENU = "menu";
    private final String TAG_ICON = "1";
    private final String TAG_ID = "id";
    private final String TAG_PREF="SETTINGPREF";
    private String      DB_SURVEY="SURVEY_";
    private final String TAG_CABANG = "cabang";
    private String DB_PATH_CSV_SUCCESS= Environment.getExternalStorageDirectory()+"/BCS/CSV";
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/BCS";
    private String filename="";
    private final String TAG_WEB = "web";
    public static String ENDPOINT = "http://192.168.31.10:9020/BCS";

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] flags = new int[]{
            R.drawable.bcs_csv,
            R.drawable.bcs_upload
    };
    static String Tgl = "";

    TextView TxtData;
    ProgressBar Pb_Loading;

    String FilePath="";
    String upLoadServerUri = "";
    private int serverResponseCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_export);

        upLoadServerUri = "http://192.168.31.10:9020/BCS/uploadfile.php";

        dbsrv = new FN_DBHandler(getApplicationContext(),DB_PATH,DB_SURVEY+getPref(TAG_CABANG));

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> OperatorMenuList = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> maporder = new HashMap<String, String>();
        maporder.put(TAG_ICON, Integer.toString(flags[0]));
        maporder.put(TAG_ID, "0");
        maporder.put(TAG_MENU, "Generate CSV");

        HashMap<String, String> mapbarang = new HashMap<String, String>();
        mapbarang.put(TAG_ICON, Integer.toString(flags[1]));
        mapbarang.put(TAG_ID, "1");
        mapbarang.put(TAG_MENU, "Upload");


        OperatorMenuList.add(maporder);
        OperatorMenuList.add(mapbarang);

        /**
         * Updating parsed JSON data into ListView
         * */
        ListAdapter adapter = new Adapter_MainMenu(this, OperatorMenuList,
                R.layout.l_mainmenu,
                new String[] { TAG_ICON, TAG_MENU, TAG_ID },
                new int[] { R.id.imageViewOP, R.id.MainMenuNama, R.id.MainMenuID });

        setListAdapter(adapter);

        // selecting single ListView item
        ListView lv = getListView();

        // Launching new screen on Selecting Single ListItem
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String menuid = ((TextView) view.findViewById(R.id.MainMenuID)).getText().toString();

                if(menuid.equals("0")){
                    ShowFilterGenerateCSV(0);
                }else if(menuid.equals("1")){
                    ShowFilterGenerateCSV(1);
                }
            }
        });

        TxtData = (TextView) findViewById(R.id.Export_TxtData);
        TxtData.setText(""+dbsrv.cekPelangganExistToday());

        dbsrv.close();

        Pb_Loading = (ProgressBar) findViewById(R.id.Export_Loading);
        Pb_Loading.setVisibility(View.GONE);

    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    public boolean generateCSV(String cabang){
        File BCSdircsv = new File(DB_PATH_CSV_SUCCESS);
        if(!BCSdircsv.exists()){
            BCSdircsv.mkdirs();
        }

        //--------------------delete file 1 minggu
        BCSdircsv = new File(DB_PATH_CSV_SUCCESS);
        for (File f : BCSdircsv.listFiles()) {
            if (f.isFile()){
                if(f.getName().toString().contains(getDateTime("ddMMyyyy",-7))){
                    f.delete();
                }
            }
        }

        filename="SURVEY_"+cabang+"_"+getDateTime("yyyyMMdd_HHmm",0)+".csv";

        //---------------------create file-----------------------------------
        try {
            Cursor cursor= dbsrv.getAllRawPelanggan(Tgl);

            FileWriter fw = new FileWriter(DB_PATH_CSV_SUCCESS+"/"+filename);
            fw.append("KodeCabang;");
            fw.append("KodeDivisi;");
            fw.append("Perusahaan;");
            fw.append("Alamat;");
            fw.append("Penghubung;");
            fw.append("Kota;");
            fw.append("Telp;");
            fw.append("KodeSegment;");
            fw.append("Segment;");
            fw.append("SubSegment;");
            fw.append("KodePos;");
            fw.append("Kecamatan;");
            fw.append("Kelurahan;");
            fw.append("Longitude;");
            fw.append("Latitude;");
            fw.append("NoHP;");
            fw.append("CreateDate;");
            fw.append("Pemilik");
            fw.append('\n');

            if (cursor.moveToFirst()) {
                do {
                    fw.append(cursor.getString(cursor.getColumnIndex("kodebcp"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("kode"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("perusahaan")).replaceAll("\r", "").replaceAll("\n", "")+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("alamat")).replaceAll("\r", "").replaceAll("\n", "")+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("penghubung"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("kota"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("telp"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("kodesegment"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("segment"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("subsegment"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("kodepos"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("kecamatan"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("kelurahan"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("longitude"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("latitude"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("nohp"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("createdate"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("pemilik")).replaceAll("\r", "").replaceAll("\n", "")+"\n");
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            fw.close();
            dbsrv.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Generate CSV Gagal : "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getDateTime(String format,int hari) {
        Calendar calendar = Calendar.getInstance();
        Date myDate = new Date();
        calendar.setTime(myDate);
        calendar.add(Calendar.DAY_OF_YEAR, hari);
        myDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                format, Locale.getDefault());
        return dateFormat.format(myDate);
    }

    public void ShowFilterGenerateCSV(final int Upload){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String ket = "";
        if(Upload==0){
            ket = "CSV";
        }else{
            ket = "Upload";
        }
        builder.setTitle("Konfirmasi Export - "+ket);
        builder.setIcon(R.drawable.sfa_filter_up);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 10, 30, 10);

        String[] ArrFilter = new String[2];
        String[] ArrKoneksi = new String[2];

        Tgl = "%";

        ArrFilter[0] = "Semua";
        ArrFilter[1] = "Hari Ini : "+getDateTime("yyyy-MM-dd");

        ArrKoneksi[0] = "Internal";
        ArrKoneksi[1] = "Eksternal";

        final TextView TxtLblFilterTgl = new TextView(this);
        TxtLblFilterTgl.setText("Filter :");

        final TextView TxtLblFilterKoneksi = new TextView(this);
        TxtLblFilterKoneksi.setText("Koneksi :");

        final Spinner SpnFilterTgl = new Spinner(this);

        ArrayAdapter adapterFilterTgl = new ArrayAdapter(this,android.R.layout.simple_spinner_item, ArrFilter);
        adapterFilterTgl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpnFilterTgl.setAdapter(adapterFilterTgl);
        SpnFilterTgl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0 :{
                        Tgl = "%";
                        break;
                    }
                    case 1 :{
                        Tgl = getDateTime("yyyy-MM-dd");
                        break;
                    }
                    default:break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        final Spinner SpnFilterKoneksi = new Spinner(this);

        ArrayAdapter adapterFilterKoneksi = new ArrayAdapter(this,android.R.layout.simple_spinner_item, ArrKoneksi);
        adapterFilterKoneksi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpnFilterKoneksi.setAdapter(adapterFilterKoneksi);
        SpnFilterKoneksi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0 :{
                        ENDPOINT = "http://192.168.31.10:9020/BCS";
                        upLoadServerUri = "http://192.168.31.10:9020/BCS/uploadfile.php";
                        break;
                    }
                    case 1 :{
                        ENDPOINT = "http://"+getPref(TAG_WEB);
                        upLoadServerUri = "http://"+getPref(TAG_WEB)+"/uploadfile.php";
                        break;
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
        layout.addView(TxtLblFilterKoneksi,params);
        layout.addView(SpnFilterKoneksi,params);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if((dbsrv.cekPelangganExistToday().equals("0"))&&(SpnFilterTgl.getSelectedItemPosition()>0)){
                    Toast.makeText(getApplicationContext(),"Belum ada pelanggan yang terverifikasi hari ini",Toast.LENGTH_SHORT).show();
                }else{
                    if(generateCSV(getPref(TAG_CABANG))){
                        if(Upload==1){
                            FilePath = DB_PATH_CSV_SUCCESS+"/"+filename;
                            Pb_Loading.setVisibility(View.VISIBLE);
                            new Thread(new Runnable() {
                                public void run() {
                                    uploadFile(FilePath,filename);
                                }
                            }).start();
                        }else{
                            Toast.makeText(getApplicationContext(),"Generate CSV berhasil : File disimpan dengan nama = "+filename,Toast.LENGTH_LONG).show();
                            ShowKonfirmasi(filename);
                        }
                    }
                }
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

    public String getDateTime(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                format, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public int uploadFile(String sourceFileUri,final String filename) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :" + FilePath);

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Activity_Export.this, "File Tidak Ditemukan " + FilePath, Toast.LENGTH_SHORT).show();
                    Pb_Loading.setVisibility(View.GONE);
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            requestStatusUpload(getPref(TAG_CABANG));
                        }
                    });
                }else{
                    Pb_Loading.setVisibility(View.GONE);
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Activity_Export.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Activity_Export.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file", "Exception : "  + e.getMessage(), e);
            }
            return serverResponseCode;

        }
    }

    private void requestStatusUpload(String Cabang){

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(5, TimeUnit.MINUTES);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(new OkClient(okHttpClient))
                .build();

        API_BCS api = adapter.create(API_BCS.class);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        api.getStatusUpload(Cabang,telephonyManager.getDeviceId()+"",filename,new Callback<List<Data_Status>>() {
            @Override
            public void success(List<Data_Status> data_statuses, Response response) {
                String Status="0";
               for (int i=0;i<data_statuses.size();i++){
                   Data_Status c = data_statuses.get(i);
                   Status = c.getStatus();
               }

                if (Status.equals("1")){
                    Toast.makeText(getApplicationContext(), "Upload Data Berhasil", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Gagal Upload Data - Kode 80", Toast.LENGTH_SHORT).show();
                }

                Pb_Loading.setVisibility(View.GONE);

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "Gagal Upload Data" + error, Toast.LENGTH_SHORT).show();
                Pb_Loading.setVisibility(View.GONE);
            }
        });
    }

    public void ShowKonfirmasi(String NamaFile){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Export");
        builder.setIcon(R.drawable.sfa_filter_up);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 10, 30, 10);

        final TextView TxtLblKet = new TextView(this);
        TxtLblKet.setText("Generate file csv berhasil,Disimpan dengan nama : "+NamaFile+" . Apakah anda ingin membuka lokasi filenya?");

        layout.addView(TxtLblKet,params);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.estrongs.android.pop");
                startActivity(launchIntent);
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
