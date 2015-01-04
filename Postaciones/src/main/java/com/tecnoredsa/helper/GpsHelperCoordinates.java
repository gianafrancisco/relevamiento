package com.tecnoredsa.helper;

import android.location.Location;
import android.util.FloatMath;
import android.util.Log;

import com.tecnoredsa.adapter.PostesArrayAdapter;
import com.tecnoredsa.model.Postacion;

import static java.lang.Math.abs;
import static java.lang.Math.floor;

/**
 * Created by francisco on 31/01/14.
 */
public class GpsHelperCoordinates {
    public GpsHelperCoordinates() {
    }

    public String decimal2degree(double decimal){
        boolean neg = decimal < 0;
        decimal = abs(decimal);

        Integer deg = new Integer((int)floor(decimal));
        Integer minutes = new Integer((int)floor((decimal * 60) % 60));
        Integer seconds = new Integer((int)(decimal * 3600) % 60);
        String degree;
        degree=deg.toString()+"º "+minutes.toString()+"' "+seconds.toString()+"\"";
        if (neg)
            degree=" -"+degree;
        return degree;
    }
    public float calulatePath(PostesArrayAdapter plist){
        float path=0;
        Log.v("DebugAPP",Integer.toString(plist.getCount()));
        for (int i=1;i < plist.getCount();i++){
            double d=0;
            Postacion c=plist.getItem(i);
            Postacion p= plist.getItem(i-1);

            /*Location current_location;
            current_location = new Location("");
            current_location.setLatitude(c.getGps_latitude());
            current_location.setLongitude(c.getGps_longitude());
            Location previous_location;
            previous_location = new Location("");
            current_location.setLatitude(p.getGps_latitude());
            current_location.setLongitude(p.getGps_longitude());
            //d=current_location.distanceTo(previous_location);
            d=current_location.
            Log.v("DebugAPP",p.getCodificacion()+ "  "+c.getCodificacion() +" "+Float.toString(d));
            */
            d=gps2m(c.getGps_latitude().floatValue(),c.getGps_longitude().floatValue(),p.getGps_latitude().floatValue(),p.getGps_longitude().floatValue());
            Log.v("DebugAPP",p.getCodificacion()+ "  "+c.getCodificacion() +" "+Double.toString(d));
            path+=d;

        }
        return path;
    }

    private double gps2m(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180/3.14169);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        float t1 = FloatMath.cos(a1)*FloatMath.cos(a2)*FloatMath.cos(b1)*FloatMath.cos(b2);
        float t2 = FloatMath.cos(a1)*FloatMath.sin(a2)*FloatMath.cos(b1)*FloatMath.sin(b2);
        float t3 = FloatMath.sin(a1)*FloatMath.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000*tt;
    }

}
