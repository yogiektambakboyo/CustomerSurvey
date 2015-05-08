package com.bcp.CustomerSurvey;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by IT-SUPERMASTER on 06/04/2015.
 */
public class FN_NetCon {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static Integer getConnectivityStatusString(Context context) {
        int conn = FN_NetCon.getConnectivityStatus(context);
        Integer status = null;
        if (conn == FN_NetCon.TYPE_WIFI) {
            status = 1;
        } else if (conn == FN_NetCon.TYPE_MOBILE) {
            status = 2;
        } else if (conn == FN_NetCon.TYPE_NOT_CONNECTED) {
            status = 0;
        }
        return status;
    }
}
