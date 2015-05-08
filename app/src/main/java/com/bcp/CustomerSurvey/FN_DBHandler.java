package com.bcp.CustomerSurvey;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FN_DBHandler extends SQLiteOpenHelper {
    private final String TAG_PELANGANDATA= "PelangganData";
    private final String TAG_STATUS = "status";

    public FN_DBHandler(Context context, String DB_PATH, String DB_NAME) {
        super(context, DB_PATH+ File.separator +DB_NAME, null, 1);
    }

    // Date n Time

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public String getToday2(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public String getDateTime(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                format, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedTime = df.format(c.getTime());
        return  formattedTime;
    }

    // End

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //---------------------- Setting -----------------------------//
    public void CreateSetting(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Pengaturan");
        db.execSQL("CREATE TABLE IF NOT EXISTS Pengaturan "
                + "(userlogin TEXT,passlogin TEXT,web TEXT,appversion INTEGER, dbversion INTEGER)");
    }

    public void InsertSetting(String User,String Password,String WebT, int appversion, int dbversion){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Pengaturan(userlogin,passlogin,web,appversion,dbversion) VALUES('"+User+"','"+Password+"','"+WebT+"',"+appversion+","+dbversion+")");
    }

    public void UpdateSettingAppVersion(String update){
        Integer appversion = Integer.parseInt(update);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET appversion="+appversion);
    }

    public void UpdateSettingDBVersion(String update){
        Integer dbversion = Integer.parseInt(update);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET dbversion="+dbversion);
    }
    public void UpdateSetting(String lastlogin,String namelogin){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET lastlogin='"+lastlogin+"',namelogin='"+namelogin+"',tgllogin=datetime('now', 'localtime')");
    }
    public void UpdateSettingFull(String WebT,String User,String Pass){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET web='"+WebT+"',userlogin='"+User+"',passlogin='"+Pass+"'");
    }
    public JSONObject GetSetting() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT userlogin,passlogin,web,appversion,dbversion FROM Pengaturan";

        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        if (cursor.moveToFirst()){
            jsonresult.put("status",1);
            jsonresult.put("userlogin", cursor.getString(cursor.getColumnIndex("userlogin")));
            jsonresult.put("passlogin", cursor.getString(cursor.getColumnIndex("passlogin")));
            jsonresult.put("web", cursor.getString(cursor.getColumnIndex("web")));
            jsonresult.put("appversion", cursor.getInt(cursor.getColumnIndex("appversion")));
            jsonresult.put("dbversion", cursor.getInt(cursor.getColumnIndex("dbversion")));
        }
        else{
            jsonresult.put("status",0);
        }
        return jsonresult;
    }

    // End Setting

    //------------------ Master----------------//

    public void CreateMaster(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS PelangganBaru");
        db.execSQL("DROP TABLE IF EXISTS Segment");
        db.execSQL("CREATE TABLE IF NOT EXISTS PelangganBaru(kodebcp TEXT,kode TEXT,perusahaan TEXT,alamat TEXT,penghubung TEXT,kota TEXT,telp TEXT,kodesegment TEXT,segment TEXT,kodepos TEXT,kecamatan TEXT,kelurahan TEXT,longitude TEXT,latitude TEXT,nohp TEXT,pemilik TEXT,createdate DATETIME,subsegment TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Segment(kode TEXT,nama TEXT)");
    }

    //----------------------------- PELANGGAN -----------------------------------------//

    public void insertPelanggan(String kodebcp,String kode,String perusahaan,String alamat,String penghubung,String kota,String telp,String kodesegment,String segment, String kodepos,String kecamatan,String kelurahan,String longitude,String latitude,String nohp,String Pemilik, String SubSegment){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO PelangganBaru VALUES('"+kodebcp+"','"+kode+"','"+perusahaan+"','"+alamat+"','"+penghubung+"','"+kota+"','"+telp+"','"+kodesegment+"','"+segment+"','"+kodepos+"','"+kecamatan+"','"+kelurahan+"','"+longitude+"','"+latitude+"','"+nohp+"','"+Pemilik+"',datetime('now', 'localtime'),'"+SubSegment+"')");
    }

    public void deletePelanggan(String kodebcp,String kode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM PelangganBaru WHERE kodebcp='"+kodebcp+"' and kode='"+kode+"'");
    }

    public JSONObject getPelangganJoin(String DB_PATH,String DB_NAME,String Cabang) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '"+ DB_PATH+ File.separator +DB_NAME +"' AS dborder");

        //String sql="select p.kode,p.perusahaan,p.alamat,IFNULL(b.kodebcp,'belum') as kodebcp from Pelanggan p LEFT JOIN dborder.PelangganBaru  b on b.kode=p.kode where substr(p.kode,1,2)='"+Cabang+"' ORDER BY p.perusahaan ASC";
        String sql="select kode,perusahaan,alamat,kodebcp from (select p.kode,p.perusahaan,p.alamat,IFNULL(b.kodebcp,'belum') as kodebcp from Pelanggan p LEFT JOIN dborder.PelangganBaru  b on b.kode=p.kode where substr(p.kode,1,2)='"+Cabang+"' " +
                   "union all select b.kode,b.perusahaan,b.alamat,b.kodebcp from dborder.PelangganBaru b where b.kode not in (select kode from Pelanggan) and substr(b.kode,1,2)='"+Cabang+"') a order by perusahaan";


        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        JSONArray jArray=new JSONArray();
        if (cursor.moveToFirst()){
            jsonresult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("kodebcp", cursor.getString(cursor.getColumnIndex("kodebcp")));
                JData.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                JData.put("alamat", cursor.getString(cursor.getColumnIndex("alamat")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jsonresult.put(TAG_PELANGANDATA, jArray);
        }
        else{
            jsonresult.put(TAG_STATUS,0);
        }
        db.execSQL("DETACH DATABASE dborder");
        return jsonresult;
    }

    public JSONObject getPelanggan(String Cabang) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select kode,perusahaan,alamat from Pelanggan where substr(kode,1,2)='"+Cabang+"' ORDER BY perusahaan ASC";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                JData.put("alamat", cursor.getString(cursor.getColumnIndex("alamat")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject getPelangganVerifikasi(String Kode) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select kode,perusahaan,alamat,kodebcp from PelangganBaru where kode != '"+Kode+"' ORDER BY perusahaan ASC";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("kodebcp", cursor.getString(cursor.getColumnIndex("kodebcp")));
                JData.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                JData.put("alamat", cursor.getString(cursor.getColumnIndex("alamat")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public String cekPelangganExist(String Kode){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select count(kodebcp) as jum from PelangganBaru where kode='"+Kode+"'";

        Cursor cursor = db.rawQuery(sql, null);

        String jum = "0";

        if (cursor.moveToFirst()){
            do {
                jum = cursor.getString(cursor.getColumnIndex("jum"));
            } while (cursor.moveToNext());
        }
        else{
        }
        return jum;
    }

    public String cekPelangganExistToday(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select count(kodebcp) as jum from PelangganBaru where date(createdate)='"+getDateTime("yyyy-MM-dd")+"'";

        Cursor cursor = db.rawQuery(sql, null);

        String jum = "0";

        if (cursor.moveToFirst()){
            do {
                jum = cursor.getString(cursor.getColumnIndex("jum"));
            } while (cursor.moveToNext());
        }
        else{
        }
        return jum;
    }

    public JSONObject getPelangganOne(String Kode) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select kode,perusahaan,alamat,penghubung,kota,telp,segment,kodepos,kecamatan,kelurahan,longitude,latitude,nohp from Pelanggan where kode='"+Kode+"'";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                JData.put("alamat", cursor.getString(cursor.getColumnIndex("alamat")));
                JData.put("penghubung", cursor.getString(cursor.getColumnIndex("penghubung")));
                JData.put("kota", cursor.getString(cursor.getColumnIndex("kota")));
                JData.put("telp", cursor.getString(cursor.getColumnIndex("telp")));
                JData.put("segment", cursor.getString(cursor.getColumnIndex("segment")));
                JData.put("kodepos", cursor.getString(cursor.getColumnIndex("kodepos")));
                JData.put("kecamatan", cursor.getString(cursor.getColumnIndex("kecamatan")));
                JData.put("kelurahan", cursor.getString(cursor.getColumnIndex("kelurahan")));
                JData.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
                JData.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
                JData.put("nohp", cursor.getString(cursor.getColumnIndex("nohp")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject getPelangganOneNewData(String Kode) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select kodebcp,kode,perusahaan,alamat,penghubung,kota,telp,kodesegment,segment,kodepos,kecamatan,kelurahan,longitude,latitude,nohp,pemilik,subsegment from PelangganBaru where kode='"+Kode+"'";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("kodebcp", cursor.getString(cursor.getColumnIndex("kodebcp")));
                JData.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                JData.put("alamat", cursor.getString(cursor.getColumnIndex("alamat")));
                JData.put("penghubung", cursor.getString(cursor.getColumnIndex("penghubung")));
                JData.put("kota", cursor.getString(cursor.getColumnIndex("kota")));
                JData.put("telp", cursor.getString(cursor.getColumnIndex("telp")));
                JData.put("kodesegment", cursor.getString(cursor.getColumnIndex("kodesegment")));
                JData.put("segment", cursor.getString(cursor.getColumnIndex("segment")));
                JData.put("subsegment", cursor.getString(cursor.getColumnIndex("subsegment")));
                JData.put("kodepos", cursor.getString(cursor.getColumnIndex("kodepos")));
                JData.put("kecamatan", cursor.getString(cursor.getColumnIndex("kecamatan")));
                JData.put("kelurahan", cursor.getString(cursor.getColumnIndex("kelurahan")));
                JData.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
                JData.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
                JData.put("nohp", cursor.getString(cursor.getColumnIndex("nohp")));
                JData.put("pemilik", cursor.getString(cursor.getColumnIndex("pemilik")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public String getmaxkode(String kode){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kodebcp AS kode FROM PelangganBaru WHERE kodebcp like '"+kode+"%'";
        Cursor cursor = db.rawQuery(sql, null);
        String kodefinal="000000";
        if(cursor.getCount()>0){
            String sql2="SELECT MAX(kodebcp) AS kode FROM PelangganBaru WHERE kodebcp like '"+kode+"%'";
            Cursor cursor2 = db.rawQuery(sql2, null);
            cursor2.moveToFirst();
            kodefinal=cursor2.getString(cursor2.getColumnIndex("kode"));
            kodefinal = kodefinal.substring(kodefinal.length()-5,kodefinal.length());
        }
        return kodefinal;
    }

    public String getmaxkodeplgbaru(String kode){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode AS kode FROM PelangganBaru WHERE kode like '"+kode+"%'";
        Cursor cursor = db.rawQuery(sql, null);
        String kodefinal="000000";
        if(cursor.getCount()>0){
            String sql2="SELECT MAX(kode) AS kode FROM PelangganBaru WHERE kode like '"+kode+"%'";
            Cursor cursor2 = db.rawQuery(sql2, null);
            cursor2.moveToFirst();
            kodefinal=cursor2.getString(cursor2.getColumnIndex("kode"));
            kodefinal = kodefinal.substring(kodefinal.length()-5,kodefinal.length());
        }
        return kodefinal;
    }

    public Cursor getAllRawPelanggan(String Tgl){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kodebcp,kode,perusahaan,alamat,penghubung,kota,telp,kodesegment,segment,kodepos,kecamatan,kelurahan,longitude,latitude,nohp,strftime('%m/%d/%Y', createdate) AS createdate,pemilik,subsegment From PelangganBaru WHERE date(createdate) like '"+Tgl+"'";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    //-------------------- Segment --------------------//
    public void insertSegment(String kode,String nama){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Segment VALUES('"+kode+"','"+nama+"')");
    }

    public void deleteSegment(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Segment");
    }

    public JSONObject getSegment(String Divisi) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select kode,nama from Segment where kode like '"+Divisi+"/%' ORDER BY nama";
        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("nama", cursor.getString(cursor.getColumnIndex("nama")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

}
