package com.absensi.sekolah.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.absensi.sekolah.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HalamanLokasi extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    private GoogleMap googleMap;
    private LatLng garageLocation;
    private UiSettings mUiSettings;
    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Normal", "Satellite", "Terrain", "Hybrid"};
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    private static String TAG = Map.class.getSimpleName();
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    ProgressDialog pd;

    String xlat, xlong, dari;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_location);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }




        xlat = getIntent().getStringExtra("xlat");
        xlong = getIntent().getStringExtra("xlong");
        dari = getIntent().getStringExtra("dari");

        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(this);

        FloatingActionButton zoom_in = findViewById(R.id.btn_zoom_in);
        zoom_in.setOnClickListener(v -> googleMap.animateCamera(CameraUpdateFactory.zoomIn()));
        FloatingActionButton zoom_out = findViewById(R.id.btn_zoom_out);
        zoom_out.setOnClickListener(v -> googleMap.animateCamera(CameraUpdateFactory.zoomOut()));
        FloatingActionButton maptype = findViewById(R.id.btn_layer);
        maptype.setOnClickListener(v -> showMapTypeSelectorDialog(v));

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mUiSettings = googleMap.getUiSettings();
        mUiSettings.setMapToolbarEnabled(false);
        // Enabling MyLocation Layer of Google Map
        mUiSettings.setMyLocationButtonEnabled(false);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        mUiSettings.setMapToolbarEnabled(false);

        if(!TextUtils.isEmpty(xlat) && !TextUtils.isEmpty(xlong)){


            double latitude = Double.parseDouble(xlat);
            double longitude = Double.parseDouble(xlong);

            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
            googleMap.clear();

            MarkerOptions mp = new MarkerOptions();

            mp.position(new LatLng(latitude, longitude));

            mp.title("My Position");

            googleMap.addMarker(mp);
            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);

        CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);

        MarkerOptions mp = new MarkerOptions().position(latLng).title(  getCompleteAddress(latitude,longitude) );

        googleMap.clear();
        googleMap.addMarker(mp);
        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);

        garageLocation = latLng;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            // TODO: Handle the error.
            Log.i(TAG, status.getStatusMessage());

        }
    }



    private void showMapTypeSelectorDialog(View v) {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = googleMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 0:
                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                break;
                            case 1:
                                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }


    public String getCompleteAddress(double latitude, double longitude) {
        String location = "";
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String state, city, zip, street;
                if (address.getAdminArea() != null) {
                    state = address.getAdminArea();
                } else {
                    state = "";
                }
                if (address.getLocality() != null) {
                    city = address.getLocality();
                } else {
                    city = "";
                }
                if (address.getPostalCode() != null) {
                    zip = address.getPostalCode();
                } else {
                    zip = "";
                }

                if (address.getThoroughfare() != null) {
                    street = address.getSubLocality() + "," + address.getThoroughfare();
                } else {
                    street = address.getSubLocality() + "," + address.getFeatureName();
                }
                location = street + "," + city + "," + zip + "," + state;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return location;
    }





    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
