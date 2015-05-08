package com.bcp.CustomerSurvey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class Activity_Setting extends Activity {
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/BCS";
    private String      DB_SETTING="SETTING";
    private final String TAG_WEB = "web";
    private final String TAG_APPVERSION = "appversion";
    private final String TAG_DBVERSION = "dbversion";
    private final String TAG_USERLOGIN = "userlogin";
    private final String TAG_PASSLOGIN = "passlogin";
    private final String TAG_SETTINGSTATUS="SETSTATUS";

    EditText InputUsername,InputPassword,InputWebServer;
    Button Btn_Submit;

    private String Web,AppVersion,DBVersion,UserLogin,PassLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_setting);

        Intent in = getIntent();

        // Get Data From DB SETTING
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_SETTING);


        if(in.getStringExtra(TAG_SETTINGSTATUS).equals("1")){
            db.InsertSetting("bcs","bcs","",1,1);
        }else{
            JSONObject MenuJSON = null;
            try{
                MenuJSON = db.GetSetting();
                Web = MenuJSON.getString(TAG_WEB);
                AppVersion = MenuJSON.getString(TAG_APPVERSION);
                DBVersion = MenuJSON.getString(TAG_DBVERSION);
                UserLogin = MenuJSON.getString(TAG_USERLOGIN);
                PassLogin = MenuJSON.getString(TAG_PASSLOGIN);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Close DB Connection
        db.close();

        InputWebServer = (EditText) findViewById(R.id.Setting_WebServer);
        InputWebServer.setText(Web);

        InputUsername = (EditText) findViewById(R.id.Setting_User);
        InputUsername.setText(UserLogin);

        InputPassword = (EditText) findViewById(R.id.Setting_Password);
        InputPassword.setText(PassLogin);

        Btn_Submit = (Button) findViewById(R.id.Setting_BtlSubmit);
        Btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(InputWebServer.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(), "Web Server harus Diisi!", Toast.LENGTH_SHORT).show();
                }else if(InputUsername.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"User harus diisi!",Toast.LENGTH_SHORT).show();
                }else if(InputPassword.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"Password harus diisi!",Toast.LENGTH_SHORT).show();
                }else{
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    Toast.makeText(getApplicationContext(),"Pengaturan Tersimpan",Toast.LENGTH_SHORT).show();
                                    db.UpdateSettingFull(InputWebServer.getText().toString(),InputUsername.getText().toString(),InputPassword.getText().toString());
                                    Intent intent = new Intent(getApplicationContext(), Activity_Main.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Setting.this);
                    builder.setTitle("Konfirmasi");
                    builder.setIcon(R.drawable.bcs_pass);
                    builder.setMessage("Simpan Pengaturan?").setPositiveButton("Ya", dialogClickListener)
                            .setNegativeButton("Tidak", dialogClickListener).show();
                }
            }
        });
    }
}
