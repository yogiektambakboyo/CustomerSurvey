package com.bcp.CustomerSurvey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Activity_Main extends Activity {

    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/BCS";
    private String      DB_SETTING="SETTING";
    private String      TAG_PREF="SETTINGPREF";
    private final String TAG_WEB = "web";
    private final String TAG_APPVERSION = "appversion";
    private final String TAG_DBVERSION = "dbversion";
    private final String TAG_USERLOGIN = "userlogin";
    private final String TAG_PASSLOGIN = "passlogin";
    private final String TAG_CABANG = "cabang";
    private final String TAG_SETTINGSTATUS="SETSTATUS";

    EditText InputUsername,InputPassword;
    Button Btn_Submit;
    Spinner SpnCabang;

    String[]  ArrCabang;
    private String Web,AppVersion,DBVersion,UserLogin,PassLogin,Cabang="00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Data From DB SETTING
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_SETTING);
        File dbFile = new File(DB_PATH+"/"+DB_SETTING);

        JSONObject MenuJSON = null;

        if(dbFile.exists()){
            try {
                MenuJSON = db.GetSetting();
                Web = MenuJSON.getString(TAG_WEB);
                AppVersion = MenuJSON.getString(TAG_APPVERSION);
                DBVersion = MenuJSON.getString(TAG_DBVERSION);
                UserLogin = MenuJSON.getString(TAG_USERLOGIN);
                PassLogin = MenuJSON.getString(TAG_PASSLOGIN);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            File BCSDir = new File(DB_PATH);
            BCSDir.mkdirs();
            db.CreateSetting();
            Intent in = new Intent(getApplicationContext(), Activity_Setting.class);
            in.putExtra(TAG_SETTINGSTATUS,"1");
            startActivity(in);
        }
        db.close();

        setPref(Web,AppVersion,DBVersion,UserLogin,PassLogin,Cabang);

        InputUsername = (EditText) findViewById(R.id.Login_InputUsername);
        InputPassword = (EditText) findViewById(R.id.Login_InputPassword);

        SpnCabang = (Spinner) findViewById(R.id.Login_CmbCabang);
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


        ArrayAdapter CabangAdapter = new ArrayAdapter(Activity_Main.this, android.R.layout.simple_spinner_item, ArrCabang);
        CabangAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpnCabang.setAdapter(CabangAdapter);

        SpnCabang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(SpnCabang.getSelectedItemPosition() > 0){
                    if (i<10){
                        Cabang = "0"+i;
                    }else{
                        Cabang = i+"";
                    }
                }else{
                    Cabang ="00";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Btn_Submit = (Button) findViewById(R.id.Login_BtnSubmit);
        Btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((InputUsername.getText().toString().equals(UserLogin))&&(InputPassword.getText().toString().equals(PassLogin))&&(SpnCabang.getSelectedItemPosition()>0)){
                    setPref(Web,AppVersion,DBVersion,UserLogin,PassLogin,Cabang);
                    Intent in = new Intent(getApplicationContext(), Activity_MainMenu.class);
                    startActivity(in);
                }else{
                    Toast.makeText(getApplicationContext(),"Username/Password/Cabang Salah",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            DialodSetting();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            Activity_Main.this.finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Konfirmasi Keluar");
            builder.setMessage("Yakin ingin keluar dari aplikasi?").setPositiveButton("Ya", dialogClickListener)
                    .setNegativeButton("Tidak", dialogClickListener).show();
        }
        return false;
    }

    public void DialodSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Keamanan");
        builder.setIcon(R.drawable.bcs_pass);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine();
        input.setHint("Masukkan Password");
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(11);
        input.setFilters(FilterArray);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().equals(getToday()+"BCS")){
                    Intent in = new Intent(getApplicationContext(),Activity_Setting.class);
                    in.putExtra(TAG_SETTINGSTATUS,"0");
                    startActivity(in);
                }else{
                    Toast.makeText(getApplicationContext(), "Password Salah!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.show();
    }

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public void setPref(String Web, String AppVersion, String DBVersion,String User, String Pass,String Cabang){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor SettingPrefEditor = SettingPref.edit();
        SettingPrefEditor.putString(TAG_WEB,Web);
        SettingPrefEditor.putString(TAG_USERLOGIN,User);
        SettingPrefEditor.putString(TAG_PASSLOGIN,Pass);
        SettingPrefEditor.putString(TAG_APPVERSION,AppVersion);
        SettingPrefEditor.putString(TAG_DBVERSION,DBVersion);
        SettingPrefEditor.putString(TAG_CABANG,Cabang);
        SettingPrefEditor.commit();
    }
}
