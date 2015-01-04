package com.tecnoredsa.postaciones;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tecnoredsa.helper.GpsHelperCoordinates;

import java.util.Iterator;

import static java.lang.Math.abs;
import static java.lang.Math.floor;

/**
 * Created by francisco on 27/01/14.
 */
public class GpsFragment extends Fragment implements GpsStatus.Listener{

    private TextView tv_sat=null;
    private TextView tv_lat=null;
    private TextView tv_long=null;
    private TextView tv_accu=null;

    LocationManager locationManager = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gps, container, false);
        tv_sat=(TextView)rootView.findViewById(R.id.gps_fragment_satelites);
        tv_lat=(TextView)rootView.findViewById(R.id.gps_fragment_latitude);
        tv_long=(TextView)rootView.findViewById(R.id.gps_fragment_longitude);
        tv_accu=(TextView)rootView.findViewById(R.id.gps_fragment_accuracy);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.addGpsStatusListener(this);
        return rootView;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if(event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){
            GpsStatus gpsStatus = locationManager.getGpsStatus(null);
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(gpsStatus != null) {
                Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
                Iterator<GpsSatellite> sat = satellites.iterator();
                int i=0;

                while (sat.hasNext()) {
                    GpsSatellite satellite = sat.next();
                    if(satellite.usedInFix())
                        i++;
                }
                tv_sat.setText(" "+new Integer(i).toString());
            }else{
                tv_sat.setText(" 0");
            }
            if(loc != null){
                GpsHelperCoordinates gps=new GpsHelperCoordinates();
                tv_lat.setText(" "+gps.decimal2degree(loc.getLatitude()));
                tv_long.setText(" "+gps.decimal2degree(loc.getLongitude()));
                tv_accu.setText(" "+String.format("%.2f",loc.getAccuracy()));
                //Log.v("AccurracyDebug", Float.toString(loc.getAccuracy()));
                //tv_lat.setText(" "+new Double(loc.getLatitude()).toString());
                //tv_long.setText(" "+new Double(loc.getLongitude()).toString());
            }
        }
    }
}
