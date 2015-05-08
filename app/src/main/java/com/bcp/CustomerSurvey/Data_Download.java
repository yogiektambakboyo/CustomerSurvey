package com.bcp.CustomerSurvey;

/**
 * Created by IT-SUPERMASTER on 27/03/2015.
 */
public class Data_Download {
    String status;
    String data;

    public Data_Download(String status, String data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
