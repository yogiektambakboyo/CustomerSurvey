package com.bcp.CustomerSurvey;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.view.KeyEvent;
import android.widget.*;
import com.bcp.CustomerSurvey.util.SystemUiHider;

import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Activity_MainMenu extends ListActivity {
    private final String TAG_MENU = "menu";
    private final String TAG_ICON = "1";
    private final String TAG_ID = "id";
    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_USERLOGIN = "userlogin";
    private final String TAG_CABANG = "cabang";

    private String      DB_SURVEY="SURVEY_";
    private FN_DBHandler dbsrv;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/BCS";


    String Web;

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] flags = new int[]{
            R.drawable.bcs_download,
            R.drawable.bcs_verify,
            R.drawable.bcs_export
    };

    String[]  ArrCabang;
    TextView TxtUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_mainmenu);

        dbsrv = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_SURVEY+getPref(TAG_CABANG));
        File dbFileSurvey = new File(DB_PATH+"/"+DB_SURVEY+getPref(TAG_CABANG));

        if (!dbFileSurvey.exists()){
            dbsrv.CreateMaster();
        }

        dbsrv.close();


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

        TxtUser = (TextView) findViewById(R.id.MainMenu_TxtUser);


        TxtUser.setText(getPref(TAG_USERLOGIN)+" ("+ArrCabang[Integer.parseInt(getPref(TAG_CABANG))]+")");

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> OperatorMenuList = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> maporder = new HashMap<String, String>();
        maporder.put(TAG_ICON, Integer.toString(flags[0]));
        maporder.put(TAG_ID, "0");
        maporder.put(TAG_MENU, "Download");

        HashMap<String, String> mapbarang = new HashMap<String, String>();
        mapbarang.put(TAG_ICON, Integer.toString(flags[1]));
        mapbarang.put(TAG_ID, "1");
        mapbarang.put(TAG_MENU, "Verify");

        HashMap<String, String> mapdaftarorder = new HashMap<String, String>();
        mapdaftarorder.put(TAG_ICON, Integer.toString(flags[2]));
        mapdaftarorder.put(TAG_ID, "2");
        mapdaftarorder.put(TAG_MENU, "Export");

        HashMap<String, String> maprecycle = new HashMap<String, String>();
        maprecycle.put(TAG_ICON, Integer.toString(flags[2]));
        maprecycle.put(TAG_ID, "3");
        maprecycle.put(TAG_MENU, "Recycle View");



        OperatorMenuList.add(maporder);
        OperatorMenuList.add(mapbarang);
        OperatorMenuList.add(mapdaftarorder);
        //OperatorMenuList.add(maprecycle);

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
                String nama = ((TextView) view.findViewById(R.id.MainMenuNama)).getText().toString();
                String menuid = ((TextView) view.findViewById(R.id.MainMenuID)).getText().toString();

                if(menuid.equals("0")){
                    Intent in = new Intent(getApplicationContext(),Activity_Download.class);
                    startActivity(in);
                }else if(menuid.equals("1")){
                    Intent in = new Intent(getApplicationContext(),Activity_Pelanggan.class);
                    startActivity(in);
                }else if(menuid.equals("2")){
                    Intent in = new Intent(getApplicationContext(),Activity_Export.class);
                    startActivity(in);
                }else if(menuid.equals("3")){
                    Intent in = new Intent(getApplicationContext(),Activity_Recylce.class);
                    startActivity(in);
                }
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent intent = new Intent(getApplicationContext(), Activity_Main.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Konfirmasi Log Out");
            builder.setMessage("Yakin ingin logout?").setPositiveButton("Ya", dialogClickListener)
                    .setNegativeButton("Tidak", dialogClickListener).show();
        }
        return false;
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }


}
