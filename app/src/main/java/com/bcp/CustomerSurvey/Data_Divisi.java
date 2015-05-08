package com.bcp.CustomerSurvey;

/**
 * Created by IT-SUPERMASTER on 26/03/2015.
 */
public class Data_Divisi {
    String Kode,Keterangan;
    public Data_Divisi(String Kode, String Keterangan) {
        this.Kode = Kode;
        this.Keterangan = Keterangan;
    }

    public String getKode() {
        return Kode;
    }

    public void setKode(String kode) {
        Kode = kode;
    }

    public String getKeterangan() {
        return Keterangan;
    }

    public void setKeterangan(String keterangan) {
        Keterangan = keterangan;
    }
}
