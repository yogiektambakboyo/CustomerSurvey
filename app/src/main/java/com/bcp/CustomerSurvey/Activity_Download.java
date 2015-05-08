package com.bcp.CustomerSurvey;

import android.app.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.okhttp.OkHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class Activity_Download extends Activity {

    TextView TxtCabang;
    ProgressBar PbLoading;
    Button BtnDownload;
    TextView TxtLoading,TxtStatus;
    Spinner SpnKoneksi;

    String[]  ArrCabang,ArrKoneksi;

    public static String ENDPOINT = "http://192.168.31.10:9020/BCS";
    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_CABANG = "cabang";
    private final String TAG_WEB = "web";
    String Cabang="00";

    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/BCS";
    private FN_DBHandler dbsrv;
    private String      DB_SURVEY="SURVEY_";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_download);

        ArrKoneksi = new String[2];
        ArrKoneksi[0] = "Intenal";
        ArrKoneksi[1] = "Eksternal";

        SpnKoneksi = (Spinner) findViewById(R.id.SpnKoneksi);

        ArrayAdapter adapter = new ArrayAdapter(Activity_Download.this,android.R.layout.simple_spinner_item,ArrKoneksi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpnKoneksi.setAdapter(adapter);
        SpnKoneksi.setSelection(0);
        SpnKoneksi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    ENDPOINT = "http://192.168.31.10:9020/BCS";
                }else{
                    ENDPOINT = "http://"+getPref(TAG_WEB);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        dbsrv = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_SURVEY+getPref(TAG_CABANG));

        ArrCabang = new String[26];
        ArrCabang[0] = "PILIH CABANG";
        ArrCabang[1] = "SURABAYA";
        ArrCabang[2] = "MALANG";
        ArrCabang[3] = "JEMBER";
        ArrCabang[4] = "KEDIRI";
        ArrCabang[5] = "DENPASAR";
        ArrCabang[6] = "MANADO";
        ArrCabang[7] = "MAKASAR";
        ArrCabang[8] = "PARE-PARE";
        ArrCabang[9] = "PALU";
        ArrCabang[10] = "PALOPO";
        ArrCabang[11] = "MADURA";
        ArrCabang[12] = "GORONTALO";
        ArrCabang[13] = "KENDARI";
        ArrCabang[14] = "LOMBOK";
        ArrCabang[15] = "LATUBO";
        ArrCabang[16] = "USS";
        ArrCabang[17] = "LUWUK";
        ArrCabang[18] = "MADIUN";
        ArrCabang[19] = "KUPANG";
        ArrCabang[20] = "MAMUJU";
        ArrCabang[21] = "SUMBAWA";
        ArrCabang[22] = "ATAMBUA";
        ArrCabang[23] = "BIMA";
        ArrCabang[24] = "TOLI-TOLI";
        ArrCabang[25] = "INDONESIA TIMUR";

        Cabang = getPref(TAG_CABANG);

        TxtCabang = (TextView) findViewById(R.id.Download_SpnCabang);
        TxtCabang.setText(ArrCabang[Integer.parseInt(Cabang)]);

        TxtLoading = (TextView) findViewById(R.id.Download_Loading);
        TxtLoading.setVisibility(View.GONE);

        TxtStatus = (TextView) findViewById(R.id.Download_Status);
        TxtStatus.setVisibility(View.GONE);

        PbLoading = (ProgressBar) findViewById(R.id.Download_PbLoading);
        PbLoading.setVisibility(View.GONE);

        BtnDownload = (Button) findViewById(R.id.Download_BtnSubmit);
        BtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(requestDataSegment()){
                    requestDataMaster(Cabang);
                }
            }
        });
        dbsrv.close();
    }


    static boolean status = true;
    private boolean requestDataSegment(){
        PbLoading.setVisibility(View.VISIBLE);
        TxtLoading.setVisibility(View.VISIBLE);

        RestAdapter adapter =new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();

        API_BCS api = adapter.create(API_BCS.class);

        api.getSegment(Cabang, new Callback<List<Data_Segment>>() {
            @Override
            public void success(List<Data_Segment> data_segments, Response response) {
                dbsrv.deleteSegment();

                for (int i = 0; i < data_segments.size(); i++) {
                    Data_Segment c = data_segments.get(i);
                    String Kode = c.getKode();
                    String Keterangan = c.getKeterangan();
                    dbsrv.insertSegment(Kode,Keterangan);
                }
                status = true;
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "Gagal Mendapatkan Data Segment", Toast.LENGTH_SHORT).show();
                PbLoading.setVisibility(View.GONE);
                TxtLoading.setVisibility(View.GONE);
                BtnDownload.setVisibility(View.VISIBLE);
                status = false;
            }
        });
        return status;
    }

    private void requestDataMaster(final String Cabang) {
        PbLoading.setVisibility(View.VISIBLE);
        TxtLoading.setVisibility(View.VISIBLE);
        TxtLoading.setText("Memproses Data. .");

        BtnDownload.setVisibility(View.GONE);


        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(5, TimeUnit.MINUTES);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(new OkClient(okHttpClient))
                .build();

        API_BCS api = adapter.create(API_BCS.class);

        api.getMaster(Cabang, new Callback<List<Data_Download>>() {
            @Override
            public void success(List<Data_Download> data_downloads, Response response) {
                PbLoading.setVisibility(View.GONE);
                TxtLoading.setVisibility(View.GONE);
                TxtStatus.setVisibility(View.VISIBLE);

                String Data = "";
                String Status = "0";

                for (int i = 0; i < data_downloads.size(); i++) {
                    Data_Download c = data_downloads.get(i);
                    Data = c.getData();
                    Status = c.getStatus();
                }


                if (Status.equals("1")) {
                    new UpdateMaster(Activity_Download.this).execute();
                } else {
                    TxtStatus.setText("Download Gagal : Kode 02 - "+Status);
                    Toast.makeText(getApplicationContext(), "Gagal " + Data, Toast.LENGTH_SHORT).show();
                }

                BtnDownload.setVisibility(View.VISIBLE);

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "Gagal Terhubung Server", Toast.LENGTH_SHORT).show();
                PbLoading.setVisibility(View.GONE);
                TxtLoading.setVisibility(View.GONE);

                BtnDownload.setVisibility(View.VISIBLE);
                TxtStatus.setVisibility(View.VISIBLE);
                TxtStatus.setText("Download Gagal : Kode 01 -" + error);
            }
        });
    }

    public class UpdateMaster extends AsyncTask<Void, Integer, Void> {

        Context context;
        Handler handler;
        Dialog dialog;
        TextView txtLoadingProgress;
        String StatusGenerate ="0";

        UpdateMaster(Context context, Handler handler){
            this.context=context;
            this.handler=handler;
        }

        UpdateMaster(Context context){
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // create dialog
            dialog=new Dialog(context);
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.p_loading);
            txtLoadingProgress =(TextView) dialog.findViewById(R.id.TxtLoading);
            txtLoadingProgress.setText("Downloading Data. .");

            dialog.show();
        }


        @Override
        protected Void doInBackground(Void... arg0) {

            final String fileName = "MASTER_"+Cabang;
            try {
                URL urls = new URL(ENDPOINT+"/sqlite/MASTER_"+Cabang);
                File file = new File(DB_PATH,fileName);
                URLConnection uconn = null;
                try {
                    uconn = urls.openConnection();
                    uconn.setReadTimeout(5*60000);
                    uconn.setConnectTimeout(5*60000);

                    InputStream is = uconn.getInputStream();
                    BufferedInputStream bufferinstream = new BufferedInputStream(is);

                    ByteArrayBuffer baf = new ByteArrayBuffer(5000);
                    int current = 0;
                    while((current = bufferinstream.read()) != -1){
                        baf.append((byte) current);
                    }

                    FileOutputStream fos = new FileOutputStream( file);
                    fos.write(baf.toByteArray());
                    fos.flush();
                    fos.close();

                    if(cekExistMaster(fileName)){
                        renameOldMaster(fileName);
                    }

                    if (renameNewMaster(fileName)){
                        deleteOldMaster();
                        StatusGenerate = "1";
                    }else{
                        renameNewMaster("MASTER_TMP");
                        StatusGenerate = "Gagal Rename File Master";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    StatusGenerate = e.getMessage();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                StatusGenerate = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(StatusGenerate.equals("1")){
                Toast.makeText(getApplicationContext(),"Download Berhasil",Toast.LENGTH_SHORT).show();
                TxtStatus.setText("Download Master Berhasil");
            }else{
                Toast.makeText(getApplicationContext(),"Download Gagal : "+StatusGenerate,Toast.LENGTH_SHORT).show();
                TxtStatus.setText("Download Master Gagal : Kode 03 - "+StatusGenerate);
            }
        }
    }

    public boolean deleteOldMaster(){
        File file = new File(DB_PATH,"MASTER_TMP");
        if(file.exists()){
            boolean deleted = file.delete();
            return deleted;
        }
        return true;
    }
    public boolean cekExistMaster(String filename){
        File file = new File(DB_PATH,"MASTER_"+filename);
        if(file.exists()){
            return true;
        }
        return false;
    }
    public boolean renameNewMaster(String fileName){
        File from = new File(DB_PATH,fileName);
        File to = new File(DB_PATH,"MASTER");
        boolean rename = from.renameTo(to);
        return rename;
    }

    public boolean renameOldMaster(String filename){
        File from = new File(DB_PATH,"MASTER_"+filename);
        File to = new File(DB_PATH,"MASTER_TMP");
        boolean rename = from.renameTo(to);
        return rename;
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

}
