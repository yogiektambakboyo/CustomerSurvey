package com.bcp.CustomerSurvey;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by IT-SUPERMASTER on 26/03/2015.
 */
public interface API_BCS {
    @GET("/ws/bcs_getsegment.php")
    public void getSegment(@Query("cabang") String cabang, Callback<List<Data_Segment>> response);

    @GET("/masukdata.php")
    public void getStatusUpload(@Query("cabang") String cabang,@Query("user") String user, @Query("filename") String filename,Callback<List<Data_Status>> response);

    @GET("/ws/bcs_master.php")
    public void getMaster(@Query("cabang") String cabang, Callback<List<Data_Download>> response);

}
