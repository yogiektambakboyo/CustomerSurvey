package com.bcp.CustomerSurvey;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Activity_Map extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private Double Latitude = 0.0,Longitude = 0.0;
    private String Perusahaan="";
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_maps);
        Intent in = getIntent();
        Latitude = Double.parseDouble(in.getStringExtra(TAG_LATITUDE));
        Longitude = Double.parseDouble(in.getStringExtra(TAG_LONGITUDE));
        Perusahaan = in.getStringExtra(TAG_PERUSAHAAN);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(Latitude, Longitude)).title(Perusahaan));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Latitude, Longitude),14));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String DetailGeo = "Tidak Menemukan";
                mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

                try {
                    addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < 1; i++) {
                            sb.append("Jalan : "+address.getThoroughfare());
                            sb.append("\n");
                            sb.append("Kecamatan : "+address.getLocality());
                            sb.append("\n");
                            sb.append("Kelurahan : "+address.getSubLocality());
                            sb.append("\n");
                            sb.append("Kode Pos : "+address.getPostalCode());
                            sb.append("\n");
                            sb.append("Kota : "+address.getSubAdminArea());
                            sb.append("\n");
                            sb.append("Provinsi : " + address.getAdminArea());
                        }
                        DetailGeo = sb.toString();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Gagal Mendapatkan Lokasi Maps : Kode 01 - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                Intent in = new Intent();
                in.putExtra("Latitude", latLng.latitude + "");
                in.putExtra("Longitude", latLng.longitude + "");
                in.putExtra("DetailGeo",DetailGeo+"");
                setResult(RESULT_OK,in);
                finish();
            }
        });
    }

}
