package com.absensi.sekolah.config;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import github.nisrulz.easydeviceinfo.base.EasyLocationMod;

public class Location {
    private String lokasi_lat;
    private String lokasi_long;

    public Location(Context context) {
        EasyLocationMod easyLocationMod = new EasyLocationMod(context);
        try {
            double[] l = easyLocationMod.getLatLong();

            lokasi_lat = String.valueOf(l[0]);
            lokasi_long = String.valueOf(l[1]);
        } catch (SecurityException e) {
            lokasi_lat = String.valueOf(-1);
            lokasi_long = String.valueOf(-1);
        }
    }

    public String getLat() {
        return lokasi_lat;
    }

    public String getLong() {
        return lokasi_long;
    }
}
