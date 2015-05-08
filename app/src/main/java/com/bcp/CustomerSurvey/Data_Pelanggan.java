package com.bcp.CustomerSurvey;

/**
 * Created by IT-SUPERMASTER on 28/03/2015.
 */
public class Data_Pelanggan {
    private String ShipTo;
    private String Perusahaan;
    private String Alamat;
    private String KodeBCP;

    public String getKodeBCP() {
        return KodeBCP;
    }

    public void setKodeBCP(String kodeBCP) {
        KodeBCP = kodeBCP;
    }

    public String getShipTo() {
        return ShipTo;
    }

    public void setShipTo(String shipTo) {
        ShipTo = shipTo;
    }

    public String getPerusahaan() {
        return Perusahaan;
    }

    public void setPerusahaan(String perusahaan) {
        Perusahaan = perusahaan;
    }

    public String getAlamat() {
        return Alamat;
    }

    public void setAlamat(String alamat) {
        Alamat = alamat;
    }

    public Data_Pelanggan(String shipTo, String perusahaan, String alamat, String kodeBCP) {
        ShipTo = shipTo;
        Perusahaan = perusahaan;
        Alamat = alamat;
        KodeBCP = kodeBCP;
    }
}
